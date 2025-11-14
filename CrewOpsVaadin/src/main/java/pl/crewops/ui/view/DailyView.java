package pl.crewops.ui.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import java.time.LocalDate;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.ui.component.content.DailyEntryContent;
import pl.crewops.ui.component.dialog.dailyEntryDialog.DateSelectorDialog;
import pl.crewops.ui.component.notification.FailNotification;
import pl.crewops.ui.view.layout.MainLayout;
import pl.crewops.util.AuthenticationResolver;

@Route("daily")
@PageTitle("Daily Entry")
public final class DailyView extends MainLayout implements BeforeEnterObserver {

    private static final String MAIN_CONTENT_WIDTH_PX = "1600px";

    private final Tab currentDayTab = new Tab(getTranslation("dailyView.currentDay"));
    private final Tab calendarTab = new Tab(getTranslation("dailyView.calendar"));
    private final Tabs tabs = new Tabs(currentDayTab, calendarTab);

    private final DateSelectorDialog dateSelectorDialog = new DateSelectorDialog();

    private DailyEntryContent dailyEntryContent;

    public DailyView(CoreAPI coreAPI, AuthenticationResolver authenticationResolver) {
        super(coreAPI, authenticationResolver);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticationResolver.principalIsAuthenticated()) {
            try {
                mainContent.removeAll();
                listeners.forEach(Registration::remove);
                buildContent();
            } catch (Exception e) {
                new FailNotification(getTranslation("dailyView.failNotification"));
            }
        } else {
            event.forwardTo(HomeView.class);
            UI.getCurrent().getPage().setLocation("/");
        }
    }

    private void buildContent() {
        fixMultipleTimelineRendering();

        dailyEntryContent.updateDependsOnSelectedDate(LocalDate.now());

        configureDateSelector();
        addDailyActivityFormListeners();
        addDailyModificationFormListeners();

        // INFO: main container:
        // aggregate all components of this view
        // allow to set timeline setWidthFull() and control that size by mainContainer.setWidth options ℹ️
        var mainContainer = new VerticalLayout();

        mainContainer.add(getToolbar(), dailyEntryContent);
        mainContainer.setMaxWidth(MAIN_CONTENT_WIDTH_PX);

        mainContent.setAlignItems(FlexComponent.Alignment.CENTER);
        mainContent.add(mainContainer);
    }

    private void fixMultipleTimelineRendering() {
        // Try to make DailyEntryContent private final and revisit DailyEntry to see this issue with
        // strange rendering behavior of third part component like Timeline what enforce this solution where
        // DailyTimelinePanel is creating brand new object each time revisit DailyView.
        this.dailyEntryContent = new DailyEntryContent(coreAPI, authenticationResolver);
    }

    private void addDailyActivityFormListeners() {
        listeners.add(dailyEntryContent.openCreateNotePanel());
        listeners.add(dailyEntryContent.createNote());
        listeners.add(dailyEntryContent.closeCreateNotePanel());
        listeners.add(dailyEntryContent.openReadNotePanel());
    }

    private void addDailyModificationFormListeners() {
        listeners.add(dailyEntryContent.createDailyListener());
        listeners.add(dailyEntryContent.changeTimesheetListener());
        listeners.add(dailyEntryContent.changeAttendanceListener());
        listeners.add(dailyEntryContent.confirmAttendanceListener());
        listeners.add(dailyEntryContent.approveDaily());
    }

    private void configureDateSelector() {
        dateSelectorDialog.setWidth("75%");
        dateSelectorDialog.setHeight("75%");

        var registration2 = dateSelectorDialog.addSelectDateListener(selectedDateEvent -> {
            dailyEntryContent.updateDependsOnSelectedDate(selectedDateEvent.getSelectedDate());

            if (selectedDateEvent.getSelectedDate().equals(LocalDate.now())) {
                tabs.setSelectedTab(currentDayTab);
            } else {
                try {
                    tabs.setSelectedTab(null);
                } catch (NullPointerException ignored) {
                }
            }

            dailyEntryContent.setReadNotesVisible(false);
            dateSelectorDialog.close();
        });
        listeners.add(registration2);
    }

    private Tabs getToolbar() {
        tabs.setFlexGrowForEnclosedTabs(3);
        tabs.setSelectedTab(currentDayTab);

        var registration = tabs.addSelectedChangeListener(event -> {
            Tab selectedTab = event.getSelectedTab();

            if (selectedTab.equals(currentDayTab)) {
                handleCurrentDaySelection();
            } else if (selectedTab.equals(calendarTab)) {
                handleCalendarSelection();
            }
        });
        listeners.add(registration);

        return tabs;
    }

    private void handleCurrentDaySelection() {
        dailyEntryContent.updateDependsOnSelectedDate(LocalDate.now());
    }

    private void handleCalendarSelection() {
        dateSelectorDialog.open();
        try {
            tabs.setSelectedTab(null);
        } catch (NullPointerException ignored) {
        }
    }
}
