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
import pl.crewops.ui.component.custom.schedule.Calendar;
import pl.crewops.ui.component.custom.schedule.MainScheduleComponent;
import pl.crewops.ui.component.notification.FailNotification;
import pl.crewops.ui.view.layout.MainLayout;
import pl.crewops.util.AuthenticationResolver;

@Route("daily")
@PageTitle("Daily Entry")
public final class DailyView extends MainLayout implements BeforeEnterObserver {

    // todo: bud w prezentacji aktualnego stanu dniowki w tabeli audytowej - brak prezentacji wartosci ktore byly
    // wybrane w momencie tworzenia wpisu
    //  prezentowane sa jedynie wartosci zmieniane
    public static final String MAIN_CONTENT_WIDTH_PX = "1600px";

    private final MainScheduleComponent mainScheduleComponent = new MainScheduleComponent();

    private final Tab currentDayTab = new Tab(getTranslation("dailyView.currentDay"));
    private final Tab calendarTab = new Tab(getTranslation("dailyView.calendar"));
    private final Tab scheduleTab = new Tab(getTranslation("dailyView.schedule"));
    private final Tabs tabs = new Tabs(currentDayTab, calendarTab, scheduleTab);

    private DailyEntryContent dailyEntryContent;
    private final Calendar calendarComponent = new Calendar();

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
        if (!authenticationResolver.principalHasManagerPermission()) {
            scheduleTab.setVisible(false);
        }

        fixMultipleTimelineRendering();

        dailyEntryContent.updateDependsOnSelectedDate(LocalDate.now());

        calendarComponent.setVisible(false);
        mainScheduleComponent.setVisible(false);

        configureToolbar();
        configureCalendarLogic();
        addDailyActivityFormListeners();
        addDailyModificationFormListeners();

        // INFO: main container:
        // aggregate all components of this view
        // allow to set timeline setWidthFull() and control that size by mainContainer.setWidth options ℹ️
        var mainContainer = new VerticalLayout();
        mainContainer.add(tabs, dailyEntryContent, calendarComponent, mainScheduleComponent);
        mainContainer.setMaxWidth(MAIN_CONTENT_WIDTH_PX);
        mainContainer.setFlexGrow(1, calendarComponent);

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

    private void configureCalendarLogic() {
        var reg = calendarComponent.addSelectDateListener(event -> {
            LocalDate selectedDate = event.getSelectedDate();

            // Ta sama logika co wcześniej w DateSelectorDialog
            dailyEntryContent.updateDependsOnSelectedDate(selectedDate);
            calendarComponent.setVisible(false);
            dailyEntryContent.setVisible(true);

            if (selectedDate.equals(LocalDate.now())) {
                tabs.setSelectedTab(currentDayTab);
            } else {
                // Unikanie błędu przy odznaczaniu tabów
                try {
                    tabs.setSelectedTab(null);
                } catch (Exception ignored) {
                }
            }
        });
        listeners.add(reg);
    }

    private void configureToolbar() {
        tabs.setFlexGrowForEnclosedTabs(3);
        tabs.setSelectedTab(currentDayTab);

        var registration = tabs.addSelectedChangeListener(event -> {
            Tab selectedTab = event.getSelectedTab();

            if (selectedTab.equals(currentDayTab)) {
                handleCurrentDaySelection();
            } else if (selectedTab.equals(calendarTab)) {
                handleCalendarSelection();
            } else if (selectedTab.equals(scheduleTab)) {
                handleScheduleSelection();
            }
        });
        listeners.add(registration);
    }

    private void handleCurrentDaySelection() {
        dailyEntryContent.updateDependsOnSelectedDate(LocalDate.now());
        dailyEntryContent.setVisible(true);
        calendarComponent.setVisible(false);
        mainScheduleComponent.setVisible(false);
    }

    private void handleCalendarSelection() {
        dailyEntryContent.setVisible(false);
        calendarComponent.setVisible(true);
        mainScheduleComponent.setVisible(false);

        calendarComponent.forceRender();
        try {
            tabs.setSelectedTab(null);
        } catch (NullPointerException ignored) {
        }
    }

    private void handleScheduleSelection() {
        dailyEntryContent.setVisible(false);
        calendarComponent.setVisible(false);
        mainScheduleComponent.setVisible(true);
    }
}
