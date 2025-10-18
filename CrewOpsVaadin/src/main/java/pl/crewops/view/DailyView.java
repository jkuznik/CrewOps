package pl.crewops.view;

import static pl.crewops.enums.DailyAttendanceStatus.*;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.shared.Registration;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import pl.crewops.component.custom.DailyTimeline;
import pl.crewops.component.dialog.dailyEntryDialog.DateSelectorDialog;
import pl.crewops.component.form.daily.DailyActivityForm;
import pl.crewops.component.form.daily.DailyModificationForm;
import pl.crewops.component.form.daily.TimesheetForm;
import pl.crewops.component.notification.FailNotification;
import pl.crewops.component.notification.NotAuthenticatedNotification;
import pl.crewops.component.notification.SuccessNotification;
import pl.crewops.enums.DailyAttendanceStatus;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.dto.dailyEntry.CreateDailyEntryDTO;
import pl.crewops.model.dto.dailyEntry.DailyEntryDTO;
import pl.crewops.model.dto.dailyEntry.UpdateDailyEntryCommand;
import pl.crewops.security.jwt.JwtServiceVaadin;
import pl.crewops.util.AuthenticationResolver;
import pl.crewops.util.BrowserResolver;
import pl.crewops.util.contract.DateSensitive;
import pl.crewops.view.layout.MainLayout;

// TODO: dodać kolumnę 'stanowisko' do tabeli daily_entry
//  dodać opcję 'historia zmian wpisu' w komponencie zarządzania wpisem

@Route("daily")
@PageTitle("Daily Entry")
@CssImport("./styles/component/timeline.css")
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

    public static final String FORMS_HEIGHT = "450px";
    public static final String FORMS_BORDER_PX = "3px";

    private final Button currentDay = new Button();
    private final Button calendar = new Button();
    private final DateSelectorDialog dateSelectorDialog = new DateSelectorDialog();

    // strange behavior of third part component like Timeline has described with initialize this object
    private DailyTimeline timeline;

    private final TimesheetForm timesheetForm;
    private final DailyActivityForm dailyActivityForm;
    private final DailyModificationForm dailyModificationForm;

    private final boolean isMobile = BrowserResolver.isMobile();

    private Optional<DailyEntryDTO> dailyEntryDTO = Optional.empty();
    private LocalDate selectedDate = LocalDate.now();

    public DailyView(CoreAPI coreAPI, JwtServiceVaadin jwtService, AuthenticationResolver authenticationResolver) {
        super(coreAPI, jwtService, authenticationResolver);
        this.timesheetForm = new TimesheetForm();
        this.dailyActivityForm = new DailyActivityForm(authenticationResolver);
        this.dailyModificationForm = new DailyModificationForm(authenticationResolver);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // todo: implement annotation that do same thing.
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
        timeline = new DailyTimeline(dailyEntryDTO.orElse(null));

        localize();

        updateDependsOnDate(selectedDate);

        addDailyModificationFormListeners();

        var layout = getLayoutDependsOnUserDevice();

        mainContent.add(getToolbar(), timeline, layout);
    }

    @Override
    public void updateDependsOnDate(LocalDate date) {
        selectedDate = date;
        try {
            dailyEntryDTO = coreAPI.findDailyEntryByEmployeeIdAndDate(
                    authenticationResolver.getPrincipal().getEmployeeId(), date);

            if (dailyEntryDTO.isPresent()) {
                var dailyEntry = dailyEntryDTO.get();
                timeline.updateTimeline(dailyEntry, null);
                timesheetForm.setDailyEntry(dailyEntry);
                dailyModificationForm.setDailyEntry(dailyEntry);

            } else {
                timesheetForm.setDailyEntry(null);
                timeline.updateTimeline(null, date);
                dailyModificationForm.setDailyEntry(null);
            }

            timesheetForm.updateDependsOnDate(date);
            dailyActivityForm.updateDependsOnDate(date);
        } catch (NotAuthenticatedException e) {
            new NotAuthenticatedNotification(e.getMessage());
        }
    }

    private Component getLayoutDependsOnUserDevice() {
        if (isMobile) {
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
        calendar.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
        currentDay.setWidth("160px");

        calendar.setWidth("160px");

        var registration = currentDay.addClickListener(event -> {
            updateDependsOnDate(LocalDate.now());

            dateSelectorDialog.setDate(selectedDate);
            currentDay.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            calendar.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
        });
        listeners.add(registration);

        var registration1 = calendar.addClickListener(event -> {
            dateSelectorDialog.open();
        });
        listeners.add(registration1);

        var registration2 = dateSelectorDialog.addSelectDateListener(selectedDateEvent -> {
            selectedDate = selectedDateEvent.getSelectedDate();
            updateDependsOnDate(selectedDate);

            calendar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            currentDay.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);

            if (selectedDateEvent.getSelectedDate().equals(LocalDate.now())) {
                currentDay.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                calendar.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
            }
            dateSelectorDialog.close();
        });
        listeners.add(registration2);

        toolbar.add(currentDay, calendar);

        return toolbar;
    }

    // TODO: implement each button logic: createDaily - done, changeTimesheet - done, changeAttendance - done,
    //  confirmAttendance - done,
    private void addDailyModificationFormListeners() {
        listeners.add(createDailyListener());

        listeners.add(changeTimesheetListener());

        listeners.add(changeAttendanceListener());

        listeners.add(confirmAttendanceListener());
    }

    private Registration confirmAttendanceListener() {
        return dailyModificationForm.addConfirmAttendanceEventListener(event -> {
            sharedUpdateAttendanceLogic(PRESENT);
        });
    }

    private Registration changeAttendanceListener() {
        return dailyModificationForm.addChangeAttendanceEventListener(event -> {
            sharedUpdateAttendanceLogic(event.getStatus());
        });
    }

    private Registration changeTimesheetListener() {
        return dailyModificationForm.addChangeTimesheetEventListener(event -> {
            updateDailyTimesheetLogic();
        });
    }

    private Registration createDailyListener() {
        return dailyModificationForm.addCreateDailyEventListener(event -> {
            createDailyLogic();
        });
    }

    private void updateDailyTimesheetLogic() {
        DailyEntryDTO dailyEntry = dailyEntryIsNullFallback();

        UUID myselfId = dailyEntry.employeeId();

        Instant startTime = timesheetForm.getStartTime();
        Instant endTime = timesheetForm.getEndTime();
        BigDecimal overtime = timesheetForm.getOvertime();

        boolean changed = !Objects.equals(startTime, dailyEntry.startTime())
                || !Objects.equals(endTime, dailyEntry.endTime())
                || (dailyEntry.overTime() == null && overtime != null && overtime.compareTo(BigDecimal.ZERO) != 0)
                || (dailyEntry.overTime() != null
                        && overtime != null
                        && dailyEntry.overTime().compareTo(overtime) != 0);

        if (!changed) {
            return;
        }

        var updateCommand = new UpdateDailyEntryCommand.UpdateWorkTime(
                myselfId, dailyEntry.entryDate(), myselfId, startTime, endTime, overtime, "");

        sharedUpdateDailyEntryLogic(updateCommand);
    }

    private void createDailyLogic() {
        if (timesheetForm.getStartTime() == null) {
            if (isMobile) {
                new FailNotification(getTranslation("timesheetForm.startTimeError"));
            }
            timesheetForm.setStartTimePickerInvalid(true);
            return;
        } else {
            timesheetForm.setStartTimePickerInvalid(false);
        }

        var createDailyEntryDTO = CreateDailyEntryDTO.builder()
                .employeeId(authenticationResolver
                        .getPrincipal()
                        .getEmployeeId()) // to sie zmieni kiedy manager bedzie mial mozliwosc jednostkowego
                // utworzenia dailyentry dla danego pracownika przez managera
                .entryDate(selectedDate)
                .actionByEmployeeId(authenticationResolver.getPrincipal().getEmployeeId())
                .startTime(timesheetForm.getStartTime())
                .endTime(timesheetForm.getEndTime())
                .overTime(timesheetForm.getOvertime())
                .build();

        try {
            Optional<DailyEntryDTO> dailyEntry = coreAPI.createDailyEntry(createDailyEntryDTO);
            if (dailyEntry.isPresent()) {
                updateDependsOnDate(selectedDate);
                new SuccessNotification(getTranslation("dailyView.createDailyEntrySuccess"));
            } else {
                new FailNotification(getTranslation("dailyView.failNotification"));
            }

        } catch (NotAuthenticatedException e) {
            new NotAuthenticatedNotification(e.getMessage());
        }
    }

    private void sharedUpdateAttendanceLogic(DailyAttendanceStatus dailyAttendanceStatus) {
        DailyEntryDTO dailyEntry = dailyEntryIsNullFallback();

        if (dailyEntry.attendance().equals(dailyAttendanceStatus)) {
            return;
        }
        UUID myselfId = dailyEntry.employeeId();

        var updateCommand = new UpdateDailyEntryCommand.UpdateAttendance(
                myselfId, dailyEntry.entryDate(), myselfId, dailyAttendanceStatus, "");

        sharedUpdateDailyEntryLogic(updateCommand);
    }

    private void sharedUpdateDailyEntryLogic(UpdateDailyEntryCommand updateCommand) {
        try {
            Optional<DailyEntryDTO> dailyEntryDTO1 = coreAPI.updateDailyEntrySelfPemission(updateCommand);
            if (dailyEntryDTO1.isPresent()) {
                dailyEntryDTO = dailyEntryDTO1;
                updateDependsOnDate(dailyEntryDTO1.get().entryDate());
                new SuccessNotification(getTranslation("dailyView.updateTimesheetSuccess"));
            } else {
                new FailNotification(getTranslation("dailyView.failNotification"));
            }
        } catch (NotAuthenticatedException e) {
            new NotAuthenticatedNotification(e.getMessage());
        }
    }

    private DailyEntryDTO dailyEntryIsNullFallback() {
        return dailyEntryDTO.orElseGet(() -> {
            new FailNotification(getTranslation("dailyView.failNotification"));
            UI.getCurrent().refreshCurrentRoute(true);
            return null;
        });
    }

    private void localize() {
        currentDay.setText(getTranslation("dailyView.currentDay"));
        calendar.setText(getTranslation("dailyView.calendar"));
    }
}
