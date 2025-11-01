package pl.crewops.ui.view;

import static pl.crewops.enums.DailyAttendanceStatus.OTHER;
import static pl.crewops.enums.DailyAttendanceStatus.PRESENT;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import pl.crewops.enums.DailyAttendanceStatus;
import pl.crewops.enums.DailyEntryStatus;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.dto.dailyEntry.CreateDailyEntryDTO;
import pl.crewops.model.dto.dailyEntry.DailyEntryDTO;
import pl.crewops.model.dto.dailyEntry.UpdateDailyEntryCommand;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;
import pl.crewops.model.dto.note.NoteDTO;
import pl.crewops.security.jwt.JwtServiceVaadin;
import pl.crewops.ui.component.dialog.dailNoteDialog.CreateDailyNoteDialog;
import pl.crewops.ui.component.dialog.dailyEntryDialog.DateSelectorDialog;
import pl.crewops.ui.component.form.daily.DailyActivityForm;
import pl.crewops.ui.component.form.daily.DailyModificationForm;
import pl.crewops.ui.component.form.daily.DailyTimeline;
import pl.crewops.ui.component.form.daily.TimesheetForm;
import pl.crewops.ui.component.notification.FailNotification;
import pl.crewops.ui.component.notification.InfoNotification;
import pl.crewops.ui.component.notification.NotAuthenticatedNotification;
import pl.crewops.ui.component.notification.SuccessNotification;
import pl.crewops.ui.view.layout.MainLayout;
import pl.crewops.util.AuthenticationResolver;
import pl.crewops.util.BrowserResolver;

// TODO: dodać opcję 'historia zmian wpisu' w komponencie zarządzania wpisem

@Route("daily")
@PageTitle("Daily Entry")
public final class DailyView extends MainLayout implements BeforeEnterObserver {

    /**
     * Defines the minimum required width (in pixels) for forms in the Daily View
     * to ensure they stick together seamlessly when placed side-by-side.
     * <p>
     * This constant is the base value used to calculate the final width that prevents
     * visual separation (the "gap") between adjacent form components.
     */
    private static final int ENSURE_STICK_FORMS = 540;

    /**
     * The final, calculated width string for all forms in the daily view.
     * <p>
     * The dependency between this field and {@link #ENSURE_STICK_FORMS} ensures that
     * the current configuration prevents visual gaps, allowing adjacent forms in the
     * Daily View to be perfectly aligned and "stuck" together.
     * The additional offset (e.g., +10) often accounts for padding, borders, or
     * minimum component requirements established by the UI framework (Vaadin).
     */
    public static final String FORMS_WIDTH = ENSURE_STICK_FORMS - 10 + "px";

    public static final String FORMS_HEIGHT = "450px";
    public static final String FORMS_BORDER_PX = "3px";

    private static final String WIDTH_PX = "1600px";

    private final Button currentDay = new Button();
    private final Button calendar = new Button();
    private final DateSelectorDialog dateSelectorDialog = new DateSelectorDialog();

    // strange behavior of third part component like Timeline has described with initialize this object
    private DailyTimeline timeline;

    private final TimesheetForm timesheetForm;
    private final DailyActivityForm dailyActivityForm;
    private final DailyModificationForm dailyModificationForm;

    private final boolean isMobile = BrowserResolver.isMobile();

    private final List<NoteDTO> selectedDateNotes = new ArrayList<>();
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

        updateDependsOnSelectedDate(selectedDate);

        addDailyActivityFormListeners();
        addDailyModificationFormListeners();

        // INFO: main container:
        // aggregate all components of this view
        // allow to set timeline setWidthFull() and control that size by mainContainer.setWidth options ℹ️
        var mainContainer = new VerticalLayout();

        var paddingForTimeline = new VerticalLayout(timeline);
        paddingForTimeline.setSpacing(true);
        paddingForTimeline.setPadding(true);

        mainContainer.add(getToolbar(), paddingForTimeline, getLayoutDependsOnUserDevice());
        mainContainer.setMaxWidth(WIDTH_PX);

        mainContent.setAlignItems(FlexComponent.Alignment.CENTER);
        mainContent.add(mainContainer);
    }

    public void updateDependsOnSelectedDate(LocalDate date) {
        selectedDate = date;
        try {
            dailyEntryDTO = coreAPI.findDailyEntryByEmployeeIdAndDate(
                    authenticationResolver.getPrincipal().getEmployeeId(), date);

            if (dailyEntryDTO.isPresent()) {
                var dailyEntry = dailyEntryDTO.get();
                timeline.updateTimeline(dailyEntry, null);
                timesheetForm.setDailyEntry(dailyEntry);
                dailyActivityForm.setDailyEntry(dailyEntry);
                dailyModificationForm.setDailyEntry(dailyEntry);

            } else {
                timeline.updateTimeline(null, date);
                timesheetForm.setDailyEntry(null);
                dailyActivityForm.setDailyEntry(null);
                dailyModificationForm.setDailyEntry(null);
            }

            timesheetForm.updateDependsOnSelectedDate(date);
            dailyActivityForm.updateDependsOnSelectedDate(date);

            List<NoteDTO> allNotesByDate = coreAPI.getAllPublicNotesByDate(selectedDate);

            if (!allNotesByDate.isEmpty()) {
                new InfoNotification("There is " + allNotesByDate.size() + " notes");
            }
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

            var horizontalLayout = new HorizontalLayout();
            horizontalLayout.setSpacing(true);
            horizontalLayout.setPadding(true);

            horizontalLayout.setMaxWidth(WIDTH_PX);
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
            updateDependsOnSelectedDate(LocalDate.now());

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
            updateDependsOnSelectedDate(selectedDate);

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
                .jobPositionDTO(timeline.getJobPosition())
                .attendance(OTHER)
                .build();

        try {
            Optional<DailyEntryDTO> dailyEntry = coreAPI.createDailyEntry(createDailyEntryDTO);
            if (dailyEntry.isPresent()) {
                updateDependsOnSelectedDate(selectedDate);
                new SuccessNotification(getTranslation("dailyView.createDailyEntrySuccess"));
            } else {
                new FailNotification(getTranslation("dailyView.failNotification"));
            }

        } catch (NotAuthenticatedException e) {
            new NotAuthenticatedNotification(e.getMessage());
        }
    }

    private void updateDailyEntryInformation() {
        DailyEntryDTO dailyEntry = dailyEntryIsNullFallback();

        UUID myselfId = dailyEntry.employeeId();

        Instant startTime = timesheetForm.getStartTime();
        Instant endTime = timesheetForm.getEndTime();
        BigDecimal formOvertime = timesheetForm.getOvertime();
        BigDecimal entryOvertime = dailyEntry.overTime();
        JobPositionDTO jobPosition = timeline.getJobPosition();

        boolean changed = !Objects.equals(startTime, dailyEntry.startTime())
                || !Objects.equals(endTime, dailyEntry.endTime())
                || !Objects.equals(dailyEntry.jobPosition(), jobPosition);

        if (!changed) {
            changed = isOvertimeChanged(entryOvertime, formOvertime);
        }

        if (!changed) {
            return;
        }

        var updateCommand = new UpdateDailyEntryCommand.UpdateDailyEntryInformation(
                myselfId, dailyEntry.entryDate(), myselfId, startTime, endTime, formOvertime, jobPosition, "");

        sharedUpdateDailyEntryLogic(updateCommand);
    }

    /**
     * Sprawdza, czy wartości nadgodzin różnią się, traktując poprawnie przypadki null i BigDecimal.
     * * Ta logika zapewnia, że zmiana z wartości dodatniej na ZERO jest uznawana za zmianę.
     * * @param entryOvertime Nadgodziny z aktualnego DTO (entryDaily.overTime()).
     * @param formOvertime Nadgodziny z formularza (timesheetForm.getOvertime()).
     * @return true, jeśli wartości są różne (uwzględniając null/0); false w przeciwnym razie.
     */
    private boolean isOvertimeChanged(BigDecimal entryOvertime, BigDecimal formOvertime) {
        if (entryOvertime == null && formOvertime == null) {
            return false;
        }

        if (entryOvertime == null || formOvertime == null) {
            return true;
        }

        // C. Żadna nie jest null, porównujemy wartości liczbowe (0 vs 0 -> NIE jest zmianą, 2 vs 0 -> JEST zmianą)
        // BigDecimal.compareTo() jest prawidłowym sposobem porównywania wartości BigDecimals.
        return entryOvertime.compareTo(formOvertime) != 0;
    }

    private void updateAttendance(DailyAttendanceStatus dailyAttendanceStatus) {
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
            Optional<DailyEntryDTO> dailyEntryDTO1 = coreAPI.updateDailyEntrySelfPermission(updateCommand);
            if (dailyEntryDTO1.isPresent()) {
                dailyEntryDTO = dailyEntryDTO1;
                updateDependsOnSelectedDate(dailyEntryDTO1.get().entryDate());
                new SuccessNotification(getTranslation("dailyView.updateTimesheetSuccess"));
            } else {
                new FailNotification(getTranslation("dailyView.failNotification"));
            }
        } catch (NotAuthenticatedException e) {
            new NotAuthenticatedNotification(e.getMessage());
        }
    }

    private void approveDailyEntry() {
        DailyEntryDTO dailyEntry = dailyEntryIsNullFallback();

        var updateCommand = new UpdateDailyEntryCommand.ApproveEntry(
                dailyEntry.employeeId(),
                dailyEntry.entryDate(),
                dailyEntry.startTime(),
                dailyEntry.endTime(),
                dailyEntry.overTime(),
                dailyEntry.status(),
                authenticationResolver.getPrincipal().getEmployeeId(),
                DailyEntryStatus.APPROVED,
                "");

        try {
            Optional<DailyEntryDTO> dailyEntryDTO1 = coreAPI.approveDailyEntry(updateCommand);
            if (dailyEntryDTO1.isPresent()) {
                dailyEntryDTO = dailyEntryDTO1;
                updateDependsOnSelectedDate(dailyEntryDTO1.get().entryDate());
                new SuccessNotification(getTranslation("dailyView.updateTimesheetSuccess"));
            } else {
                // todo : dodać komunikat o MOŻLIWEJ przyczynie niepowodzenia z powodu modyfikacji wpisu w
                //  międzyczasie oraz dodać podstawowe informacje które pomogą to zweryfikować takie jak czas
                //  ostatniej modyfikacji (albo nawet przeliczyc i podać czas w minutach od ostatniej modyfikacji)
                new FailNotification(getTranslation("dailyView.failNotification"));
            }
        } catch (NotAuthenticatedException e) {
            new FailNotification(getTranslation("dailyView.failNotification"));
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

    private void addDailyActivityFormListeners() {
        listeners.add(createNoteListener());
    }

    private Registration createNoteListener() {
        return dailyActivityForm.addCreateNoteListener(event -> {
            new CreateDailyNoteDialog(dailyEntryDTO.orElse(null), selectedDate);
        });
    }

    private void addDailyModificationFormListeners() {
        listeners.add(createDailyListener());

        listeners.add(changeTimesheetListener());

        listeners.add(changeAttendanceListener());

        listeners.add(confirmAttendanceListener());

        listeners.add(approveDaily());
    }

    private Registration approveDaily() {
        return dailyModificationForm.addApproveDailyEventListener(event -> {
            approveDailyEntry();
        });
    }

    private Registration confirmAttendanceListener() {
        return dailyModificationForm.addConfirmAttendanceEventListener(event -> {
            updateAttendance(PRESENT);
        });
    }

    private Registration changeAttendanceListener() {
        return dailyModificationForm.addChangeAttendanceEventListener(event -> {
            updateAttendance(event.getStatus());
        });
    }

    private Registration changeTimesheetListener() {
        return dailyModificationForm.addChangeTimesheetEventListener(event -> {
            updateDailyEntryInformation();
        });
    }

    private Registration createDailyListener() {
        return dailyModificationForm.addCreateDailyEventListener(event -> {
            createDailyLogic();
        });
    }
}
