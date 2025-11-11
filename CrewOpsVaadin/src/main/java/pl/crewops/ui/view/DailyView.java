package pl.crewops.ui.view;

import static pl.crewops.enums.DailyAttendanceStatus.OTHER;
import static pl.crewops.enums.DailyAttendanceStatus.PRESENT;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab; // NOWY IMPORT
import com.vaadin.flow.component.tabs.Tabs; // NOWY IMPORT
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
import pl.crewops.model.NoteFormModel;
import pl.crewops.model.dto.dailyEntry.CreateDailyEntryDTO;
import pl.crewops.model.dto.dailyEntry.DailyEntryDTO;
import pl.crewops.model.dto.dailyEntry.UpdateDailyEntryCommand;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;
import pl.crewops.model.dto.note.FetchNotesRequest;
import pl.crewops.model.dto.note.NoteDTO;
import pl.crewops.ui.component.dialog.dailyEntryDialog.DateSelectorDialog;
import pl.crewops.ui.component.notification.FailNotification;
import pl.crewops.ui.component.notification.NotAuthenticatedNotification;
import pl.crewops.ui.component.notification.SuccessNotification;
import pl.crewops.ui.component.panel.daily.*;
import pl.crewops.ui.view.layout.MainLayout;
import pl.crewops.util.AuthenticationResolver;
import pl.crewops.util.BrowserResolver;

@Route("daily")
@PageTitle("Daily Entry")
public final class DailyView extends MainLayout implements BeforeEnterObserver {

    private static final String MAIN_CONTENT_WIDTH_PX = "1600px";

    private final Tab currentDayTab;
    private final Tab calendarTab;
    private final DateSelectorDialog dateSelectorDialog = new DateSelectorDialog();

    // strange behavior of third part component like Timeline has described with initialize this object
    private DailyTimelinePanel timelinePanel;

    private final TimesheetPanel timesheetPanel;
    private final DailyActivityPanel dailyActivityPanel;
    private final CreateDailyNotePanel createDailyNotePanel;
    private final DailyModificationPanel dailyModificationPanel;

    private final ReadNotesPanel readNotesPanel = new ReadNotesPanel();

    private final boolean isMobile = BrowserResolver.isMobile();

    private List<NoteDTO> selectedDateNotes = new ArrayList<>();
    private Optional<DailyEntryDTO> dailyEntryDTO = Optional.empty();
    private LocalDate selectedDate = LocalDate.now();

    public DailyView(CoreAPI coreAPI, AuthenticationResolver authenticationResolver) {
        super(coreAPI, authenticationResolver);
        this.currentDayTab = new Tab(getTranslation("dailyView.currentDay"));
        this.calendarTab = new Tab(getTranslation("dailyView.calendar"));

        this.timesheetPanel = new TimesheetPanel();
        this.dailyActivityPanel = new DailyActivityPanel(authenticationResolver);
        this.createDailyNotePanel = new CreateDailyNotePanel();
        this.dailyModificationPanel = new DailyModificationPanel(authenticationResolver);
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
        timelinePanel = new DailyTimelinePanel(dailyEntryDTO.orElse(null));

        updateDependsOnSelectedDate(selectedDate);

        addDailyActivityFormListeners();
        addDailyModificationFormListeners();

        // INFO: main container:
        // aggregate all components of this view
        // allow to set timeline setWidthFull() and control that size by mainContainer.setWidth options ℹ️
        var mainContainer = new VerticalLayout();

        var timeline = new VerticalLayout(timelinePanel);
        timeline.setSpacing(true);
        timeline.setPadding(true);

        mainContainer.add(getToolbar(), timeline, getLayoutDependsOnUserDevice());
        mainContainer.setMaxWidth(MAIN_CONTENT_WIDTH_PX);

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
                timelinePanel.updateTimeline(dailyEntry, null);
                timesheetPanel.setDailyEntry(dailyEntry);
                dailyActivityPanel.setDailyEntry(dailyEntry);
                dailyModificationPanel.setDailyEntry(dailyEntry);

            } else {
                timelinePanel.updateTimeline(null, date);
                timesheetPanel.setDailyEntry(null);
                dailyActivityPanel.setDailyEntry(null);
                dailyModificationPanel.setDailyEntry(null);
            }

            timesheetPanel.updateDependsOnSelectedDate(date);
            dailyActivityPanel.updateDependsOnSelectedDate(date);
            createDailyNotePanel.setDate(date);

            var fetchNotesRequest = FetchNotesRequest.builder()
                    .employeeId(authenticationResolver.getPrincipal().getEmployeeId())
                    .date(selectedDate)
                    .build();

            selectedDateNotes = coreAPI.getAllPublicAndPrincipalPrivateNotesByDate(fetchNotesRequest);

            if (!selectedDateNotes.isEmpty()) {
                dailyActivityPanel.setReadNotesVisible();
                readNotesPanel.updateGrid(selectedDateNotes.stream()
                        .map(NoteFormModel::toNoteFormModel)
                        .toList());
            }
        } catch (NotAuthenticatedException e) {
            new NotAuthenticatedNotification(e.getMessage());
        }
    }

    private Component getLayoutDependsOnUserDevice() {

        createDailyNotePanel.setVisible(false);
        readNotesPanel.setVisible(false);

        if (isMobile) {
            var verticalLayout = new VerticalLayout();
            verticalLayout.setSizeFull();
            verticalLayout.setSpacing(true);
            verticalLayout.setPadding(true);

            verticalLayout.add(timesheetPanel, createDailyNotePanel, dailyActivityPanel, dailyModificationPanel);

            return verticalLayout;
        } else {
            final String PANEL_HEIGHT = "540px";
            final String PANEL_WIDTH = "540px";

            var horizontalLayout = new HorizontalLayout();
            horizontalLayout.setSpacing(true);
            horizontalLayout.setPadding(true);

            horizontalLayout.setMaxWidth(MAIN_CONTENT_WIDTH_PX);
            horizontalLayout.setWidthFull();

            timesheetPanel.setWidth(PANEL_WIDTH);
            timesheetPanel.setHeight(PANEL_HEIGHT);

            createDailyNotePanel.setWidth(PANEL_WIDTH);
            createDailyNotePanel.setHeight(PANEL_HEIGHT);

            dailyActivityPanel.setWidth(PANEL_WIDTH);
            dailyActivityPanel.setHeight(PANEL_HEIGHT);

            dailyModificationPanel.setWidth(PANEL_WIDTH);
            dailyModificationPanel.setHeight(PANEL_HEIGHT);

            horizontalLayout.add(timesheetPanel, createDailyNotePanel, dailyActivityPanel, dailyModificationPanel);

            var panelRows = new VerticalLayout();
            panelRows.setSpacing(true);
            panelRows.setPadding(true);

            panelRows.add(horizontalLayout, readNotesPanel);

            return panelRows;
        }
    }

    private Tabs getToolbar() {
        Tabs tabs = new Tabs(currentDayTab, calendarTab);
        tabs.setFlexGrowForEnclosedTabs(2);

        updateDependsOnSelectedDate(LocalDate.now());
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

        var registration2 = dateSelectorDialog.addSelectDateListener(selectedDateEvent -> {
            selectedDate = selectedDateEvent.getSelectedDate();
            updateDependsOnSelectedDate(selectedDate);

            if (selectedDateEvent.getSelectedDate().equals(LocalDate.now())) {
                tabs.setSelectedTab(currentDayTab);
            } else {
                tabs.setSelectedTab(calendarTab);
            }

            readNotesPanel.setVisible(false);
            dateSelectorDialog.close();
        });
        listeners.add(registration2);

        return tabs;
    }

    // Nowa, wyodr\u0119bniona logika dla Tab\u00f3w
    private void handleCurrentDaySelection() {
        updateDependsOnSelectedDate(LocalDate.now());
        dateSelectorDialog.setDate(selectedDate);
    }

    // Nowa, wyodr\u0119bniona logika dla Tab\u00f3w
    private void handleCalendarSelection() {
        dateSelectorDialog.open();
    }

    private void createDailyLogic() {
        if (timesheetPanel.getStartTime() == null) {
            if (isMobile) {
                new FailNotification(getTranslation("timesheetForm.startTimeError"));
            }
            timesheetPanel.setStartTimePickerInvalid(true);
            return;
        } else {
            timesheetPanel.setStartTimePickerInvalid(false);
        }

        var createDailyEntryDTO = CreateDailyEntryDTO.builder()
                .employeeId(authenticationResolver
                        .getPrincipal()
                        .getEmployeeId()) // to sie zmieni kiedy manager bedzie mial mozliwosc jednostkowego
                // utworzenia dailyentry dla danego pracownika przez managera
                .entryDate(selectedDate)
                .actionByEmployeeId(authenticationResolver.getPrincipal().getEmployeeId())
                .startTime(timesheetPanel.getStartTime())
                .endTime(timesheetPanel.getEndTime())
                .overTime(timesheetPanel.getOvertime())
                .jobPositionDTO(timesheetPanel.getJobPosition())
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

        Instant startTime = timesheetPanel.getStartTime();
        Instant endTime = timesheetPanel.getEndTime();
        BigDecimal formOvertime = timesheetPanel.getOvertime();
        BigDecimal entryOvertime = dailyEntry.overTime();
        JobPositionDTO jobPosition = timesheetPanel.getJobPosition();

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
     * Sprawdza, czy warto\u015Bci nadgodzin r\u00f3\u017Cni\u0105 si\u0119, traktuj\u0105c poprawnie przypadki null i BigDecimal.
     * * Ta logika zapewnia, \u017Ce zmiana z warto\u015Bci dodatniej na ZERO jest uznawana za zmian\u0119.
     * * @param entryOvertime Nadgodziny z aktualnego DTO (entryDaily.overTime()).
     * @param formOvertime Nadgodziny z formularza (timesheetForm.getOvertime()).
     * @return true, je\u015Bli warto\u015Bci s\u0105 r\u00f3\u017Cne (uwzgl\u0119dniaj\u0105c null/0); false w przeciwnym razie.
     */
    private boolean isOvertimeChanged(BigDecimal entryOvertime, BigDecimal formOvertime) {
        if (entryOvertime == null && formOvertime == null) {
            return false;
        }

        if (entryOvertime == null || formOvertime == null) {
            return true;
        }

        // C. \u017Badna nie jest null, por\u00f3wnujemy warto\u015Bci liczbowe (0 vs 0 -> NIE jest zmian\u0105, 2 vs 0
        // -> JEST zmian\u0105)
        // BigDecimal.compareTo() jest prawid\u0142owym sposobem por\u0142wnywania warto\u015Bci BigDecimals.
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
                // todo : doda\u0107 komunikat o MO\u017BLIWEJ przyczynie niepowodzenia z powodu modyfikacji wpisu w
                //  mi\u0119dzyczasie oraz doda\u0107 podstawowe informacje kt\u00f3re pomog\u0105 to zweryfikowa\u0107
                // takie jak czas
                //  ostatniej modyfikacji (albo nawet przeliczyc i poda\u0107 czas w minutach od ostatniej modyfikacji)
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

    private void addDailyActivityFormListeners() {
        listeners.add(openCreateNotePanel());

        listeners.add(createNote());

        listeners.add(closeCreateNotePanel());

        listeners.add(openReadNotePanel());
    }

    private Registration openReadNotePanel() {
        return dailyActivityPanel.addReadNotesListener(event -> {
            readNotesPanel.setVisible(true);
        });
    }

    private Registration createNote() {
        return createDailyNotePanel.addCreateNoteListener(event -> {
            createDailyNotePanel.setVisible(false);
            dailyActivityPanel.setVisible(true);
            updateDependsOnSelectedDate(selectedDate);
        });
    }

    private Registration openCreateNotePanel() {
        return dailyActivityPanel.addCreateNoteListener(event -> {
            createDailyNotePanel.setVisible(true);
            dailyActivityPanel.setVisible(false);
        });
    }

    private Registration closeCreateNotePanel() {
        return createDailyNotePanel.addCloseListener(event -> {
            createDailyNotePanel.setVisible(false);
            dailyActivityPanel.setVisible(true);
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
        return dailyModificationPanel.addApproveDailyEventListener(event -> {
            approveDailyEntry();
        });
    }

    private Registration confirmAttendanceListener() {
        return dailyModificationPanel.addConfirmAttendanceEventListener(event -> {
            updateAttendance(PRESENT);
        });
    }

    private Registration changeAttendanceListener() {
        return dailyModificationPanel.addChangeAttendanceEventListener(event -> {
            updateAttendance(event.getStatus());
        });
    }

    private Registration changeTimesheetListener() {
        return dailyModificationPanel.addChangeTimesheetEventListener(event -> {
            updateDailyEntryInformation();
        });
    }

    private Registration createDailyListener() {
        return dailyModificationPanel.addCreateDailyEventListener(event -> {
            createDailyLogic();
        });
    }
}
