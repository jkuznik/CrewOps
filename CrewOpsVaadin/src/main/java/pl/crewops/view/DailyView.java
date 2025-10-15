package pl.crewops.view;

import static pl.crewops.enums.DailyAttendanceStatus.*;

import com.vaadin.componentfactory.timeline.model.Item;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import pl.crewops.component.custom.DailyTimeline;
import pl.crewops.component.dialog.dateSelectorDialog.DateSelectorDialog;
import pl.crewops.component.form.daily.DailyActivityForm;
import pl.crewops.component.form.daily.DailyModificationForm;
import pl.crewops.component.form.daily.TimesheetForm;
import pl.crewops.component.notification.NotAuthenticatedNotification;
import pl.crewops.component.notification.SuccessNotification;
import pl.crewops.enums.DailyEntryStatus;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.dto.dailyEntry.CreateDailyEntryDTO;
import pl.crewops.model.dto.dailyEntry.DailyEntryDTO;
import pl.crewops.security.jwt.JwtServiceVaadin;
import pl.crewops.util.AuthenticationResolver;
import pl.crewops.util.BrowserResolver;
import pl.crewops.util.contract.DateSensitive;
import pl.crewops.view.layout.MainLayout;

@Route("daily")
@PageTitle("Daily Entry")
public final class DailyView extends MainLayout implements BeforeEnterObserver, DateSensitive {

    /**
     * Defines the minimum required width (in pixels) for forms in the Daily View
     * to ensure they stick together seamlessly when placed side-by-side.
     * <p>
     * This constant is the base value used to calculate the final width that prevents
     * visual separation (the "gap") between adjacent form components.
     */
    private static final int ENSURE_STICK_FORMS = 440;

    /**
     * The final, calculated width string for all forms in the daily view.
     * <p>
     * The dependency between this field and {@link #ENSURE_STICK_FORMS} ensures that
     * the current configuration prevents visual gaps, allowing adjacent forms in the
     * Daily View to be perfectly aligned and "stuck" together.
     * The additional offset (e.g., +10) often accounts for padding, borders, or
     * minimum component requirements established by the UI framework (Vaadin).
     */
    public static final String FORMS_WIDTH = ENSURE_STICK_FORMS + 10 + "px";

    public static final String FORMS_HEIGHT = "400px";

    private final Button currentDay = new Button();
    private final Button calendar = new Button();

    // strange behavior of third part component like Timeline has described with initialize this object
    private DailyTimeline timeline;

    private final TimesheetForm timesheetForm;
    private final DailyActivityForm dailyActivityForm;
    private final DailyModificationForm dailyModificationForm;

    public DailyView(CoreAPI coreAPI, JwtServiceVaadin jwtService, AuthenticationResolver authenticationResolver) {
        super(coreAPI, jwtService, authenticationResolver);
        this.timesheetForm = new TimesheetForm();
        this.dailyActivityForm = new DailyActivityForm(authenticationResolver);
        this.dailyModificationForm = new DailyModificationForm(coreAPI, authenticationResolver);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // todo: implement annotation that do same thing.
        if (authenticationResolver.principalIsAuthenticated()) {
            buildContent();
        } else {
            event.forwardTo(HomeView.class);
            UI.getCurrent().getPage().setLocation("/");
        }
    }

    private void buildContent() {
        mainContent.removeAll();
        timeline = new DailyTimeline();

        localize();

        updateDependsOnDate(LocalDate.now());

        addDailyModificationFormListeners();

        var layout = getLayoutDependsOnUserDevice();

        mainContent.add(getToolbar(), timeline, layout);
    }

    private void localize() {
        currentDay.setText(getTranslation("dailyView.currentDay"));
        calendar.setText(getTranslation("dailyView.calendar"));
    }

    @Override
    public void updateDependsOnDate(LocalDate date) {
        try {
            Optional<DailyEntryDTO> dailyEntryDTO = coreAPI.findDailyEntryByEmployeeIdAndDate(
                    authenticationResolver.getPrincipal().getEmployeeId(), date);

            if (dailyEntryDTO.isPresent()) {
                var dailyEntry = dailyEntryDTO.get();
                timeline.setAttendanceStatus(dailyEntry.attendance());
                timesheetForm.setDailyEntry(dailyEntry);

                // this dailyModificationForm logic has to be in that specific order
                if (dailyEntry.attendance() != null) {
                    dailyModificationForm.setAttendanceSelected(true);
                }
                dailyModificationForm.setCurrentStatus(dailyEntry.status());
            } else {
                timeline.setAttendanceStatus(NULL);
                timesheetForm.setDailyEntry(null);
            }

            timesheetForm.updateDependsOnDate(date);
            dailyActivityForm.updateDependsOnDate(date);
        } catch (NotAuthenticatedException e) {
            new NotAuthenticatedNotification(e.getMessage());
        }
    }

    private Component getLayoutDependsOnUserDevice() {
        if (BrowserResolver.isMobile()) {
            var verticalLayout = new VerticalLayout();
            verticalLayout.setSizeFull();
            verticalLayout.setSpacing(true);
            verticalLayout.setPadding(true);

            verticalLayout.add(timesheetForm, dailyActivityForm);

            return verticalLayout;
        } else {
            final String FORM_HEIGHT = "500px";
            final String FORM_WIDTH = ENSURE_STICK_FORMS + "px";
            final String CONTAINER_MAX_WIDTH = "1200px";

            var horizontalLayout = new HorizontalLayout();
            horizontalLayout.setSpacing(false);
            horizontalLayout.setPadding(false);

            horizontalLayout.setMaxWidth(CONTAINER_MAX_WIDTH);
            horizontalLayout.setWidthFull();

            timesheetForm.setWidth(FORM_WIDTH);
            timesheetForm.setHeight(FORM_HEIGHT);

            dailyActivityForm.setWidth(FORM_WIDTH);
            dailyActivityForm.setHeight(FORM_HEIGHT);

            dailyModificationForm.setWidth(FORM_WIDTH);
            dailyModificationForm.setHeight(FORM_HEIGHT);

            horizontalLayout.add(timesheetForm, dailyActivityForm, dailyModificationForm);

            return horizontalLayout;
        }
    }

    private HorizontalLayout getToolbar() {
        var toolbar = new HorizontalLayout();

        currentDay.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        currentDay.setWidth("160px");

        calendar.setWidth("160px");

        currentDay.addClickListener(event -> {
            updateDependsOnDate(LocalDate.now());

            currentDay.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            calendar.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
        });

        calendar.addClickListener(event -> {
            var dateSelectorDialog = new DateSelectorDialog();
            dateSelectorDialog.addSelectDateListener(selectedDateEvent -> {
                updateDependsOnDate(selectedDateEvent.getSelectedDate());

                calendar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                currentDay.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);

                if (selectedDateEvent.getSelectedDate().equals(LocalDate.now())) {
                    currentDay.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                    calendar.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
                }
            });
        });

        toolbar.add(currentDay, calendar);

        return toolbar;
    }

    // TODO: implement each button logic: createDaily - done,
    private void addDailyModificationFormListeners() {
        dailyModificationForm.addCreateDailyEventListener(event -> {
            var createDailyEntryDTO = CreateDailyEntryDTO.builder()
                    .employeeId(authenticationResolver
                            .getPrincipal()
                            .getEmployeeId()) // to sie zmieni kiedy manager bedzie mial mozliwosc jednostkowego
                    // utworzenia dailyentry dla danego pracownika przez managera
                    .entryDate(LocalDate.now())
                    .actionByEmployeeId(authenticationResolver.getPrincipal().getEmployeeId())
                    .startTime(timesheetForm.getStartTime())
                    .endTime(timesheetForm.getEndTime())
                    .overTime(timesheetForm.getOvertime())
                    .attendance(PRESENT)
                    .status(DailyEntryStatus.DRAFT)
                    .build();

            try {
                Optional<DailyEntryDTO> dailyEntry = coreAPI.createDailyEntry(createDailyEntryDTO);
                dailyEntry.ifPresent(dailyEntryDTO -> {
                    new SuccessNotification(getTranslation("dailyView.createDailyEntrySuccess"));
                });

            } catch (NotAuthenticatedException e) {
                new NotAuthenticatedNotification(e.getMessage());
            }
        });
    }

    private List<Item> createTimelineItems() {
        LocalDate today = LocalDate.now();

        Item item1 = new Item(
                LocalDateTime.of(2025, 10, 8, 2, 30, 00),
                LocalDateTime.of(2021, 8, 11, 8, 00, 00),
                "Item 1 - Praca na projekcie A");
        item1.setId("1");

        Item item2 = new Item(
                LocalDateTime.of(2021, 8, 11, 9, 00, 00),
                LocalDateTime.of(2021, 8, 11, 17, 00, 00),
                "Item 2 - Spotkanie z klientem");
        item2.setId("2");

        Item item3 = new Item(today.atTime(0, 30, 00), today.atTime(3, 0, 00), "Item 3 - Migracja serwera");
        item3.setId("3");

        Item item4 = new Item(today.atTime(4, 30, 00), today.atTime(20, 0, 00), "Item 4 - Dzień wolny");
        item4.setId("4");

        Item item5 = new Item(today.atTime(21, 30, 00), today.plusDays(1).atTime(1, 15, 00), "Item 5 - Wdrożenie");
        item5.setId("5");

        List<Item> items = Arrays.asList(item1, item2, item3, item4, item5);

        items.forEach(i -> {
            i.setEditable(false);
            i.setUpdateTime(false);
        });

        return items;
    }
}
