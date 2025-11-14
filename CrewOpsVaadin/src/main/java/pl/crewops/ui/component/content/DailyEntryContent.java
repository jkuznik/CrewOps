package pl.crewops.ui.component.content;

import static pl.crewops.enums.DailyAttendanceStatus.OTHER;
import static pl.crewops.enums.DailyAttendanceStatus.PRESENT;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
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
import pl.crewops.ui.component.notification.FailNotification;
import pl.crewops.ui.component.notification.NotAuthenticatedNotification;
import pl.crewops.ui.component.notification.SuccessNotification;
import pl.crewops.ui.component.panel.daily.*;
import pl.crewops.util.AuthenticationResolver;
import pl.crewops.util.BrowserResolver;

public class DailyEntryContent extends VerticalLayout {

    private final CoreAPI coreAPI;
    private final AuthenticationResolver authenticationResolver;

    private final DailyTimelinePanel timelinePanel = new DailyTimelinePanel();
    private final TimesheetPanel timesheetPanel = new TimesheetPanel();

    private final DailyActivityPanel dailyActivityPanel;
    private final CreateDailyNotePanel createDailyNotePanel = new CreateDailyNotePanel();
    private final DailyModificationPanel dailyModificationPanel;
    private final ReadNotesPanel readNotesPanel = new ReadNotesPanel();

    private DailyEntryDTO dailyEntryDTO = null;
    private LocalDate selectedDate = LocalDate.now();

    public DailyEntryContent(CoreAPI coreAPI, AuthenticationResolver authenticationResolver) {
        setSizeFull();
        this.coreAPI = coreAPI;
        this.authenticationResolver = authenticationResolver;
        this.dailyActivityPanel = new DailyActivityPanel(authenticationResolver);
        this.dailyModificationPanel = new DailyModificationPanel(authenticationResolver);

        add(getSelectedDayContentDependsOnDevice());
    }

    public void updateDependsOnSelectedDate(LocalDate date) {
        selectedDate = date;
        try {
            dailyEntryDTO = coreAPI.findDailyEntryByEmployeeIdAndDate(
                            authenticationResolver.getPrincipal().getEmployeeId(), date)
                    .orElse(null);

            if (dailyEntryDTO != null) {
                timelinePanel.updateTimeline(dailyEntryDTO, null);
                timesheetPanel.setDailyEntry(dailyEntryDTO);
                dailyActivityPanel.setDailyEntry(dailyEntryDTO);
                dailyModificationPanel.setDailyEntry(dailyEntryDTO);
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

            List<NoteDTO> selectedDateNotes = coreAPI.getAllPublicAndPrincipalPrivateNotesByDate(fetchNotesRequest);

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

    public void setReadNotesVisible(boolean visible) {
        readNotesPanel.setVisible(visible);
    }

    private VerticalLayout getSelectedDayContentDependsOnDevice() {

        createDailyNotePanel.setVisible(false);
        readNotesPanel.setVisible(false);

        if (BrowserResolver.isMobile()) {
            var verticalLayout = new VerticalLayout();
            verticalLayout.setSizeFull();
            verticalLayout.setSpacing(true);
            verticalLayout.setPadding(true);

            verticalLayout.add(
                    timelinePanel, timesheetPanel, createDailyNotePanel, dailyActivityPanel, dailyModificationPanel);

            return verticalLayout;
        } else {
            final String PANEL_HEIGHT = "540px";
            final String PANEL_WIDTH = "540px";

            var horizontalLayout = new HorizontalLayout();
            horizontalLayout.setSizeFull();
            horizontalLayout.setSpacing(true);
            horizontalLayout.setPadding(true);

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
            panelRows.setSizeFull();
            panelRows.setSpacing(true);
            panelRows.setPadding(true);

            panelRows.add(timelinePanel, horizontalLayout, readNotesPanel);

            return panelRows;
        }
    }

    public Registration approveDaily() {
        return dailyModificationPanel.addApproveDailyEventListener(event -> {
            approveDailyEntry();
        });
    }

    public Registration confirmAttendanceListener() {
        return dailyModificationPanel.addConfirmAttendanceEventListener(event -> {
            updateAttendance(PRESENT);
        });
    }

    public Registration changeAttendanceListener() {
        return dailyModificationPanel.addChangeAttendanceEventListener(event -> {
            updateAttendance(event.getStatus());
        });
    }

    public Registration changeTimesheetListener() {
        return dailyModificationPanel.addChangeTimesheetEventListener(event -> {
            updateDailyEntryInformation();
        });
    }

    public Registration createDailyListener() {
        return dailyModificationPanel.addCreateDailyEventListener(event -> {
            createDailyLogic();
        });
    }

    public Registration openReadNotePanel() {
        return dailyActivityPanel.addReadNotesListener(event -> {
            readNotesPanel.setVisible(true);
        });
    }

    public Registration createNote() {
        return createDailyNotePanel.addCreateNoteListener(event -> {
            createDailyNotePanel.setVisible(false);
            dailyActivityPanel.setVisible(true);
            updateDependsOnSelectedDate(selectedDate);
        });
    }

    public Registration openCreateNotePanel() {
        return dailyActivityPanel.addCreateNoteListener(event -> {
            createDailyNotePanel.setVisible(true);
            dailyActivityPanel.setVisible(false);
        });
    }

    public Registration closeCreateNotePanel() {
        return createDailyNotePanel.addCloseListener(event -> {
            createDailyNotePanel.setVisible(false);
            dailyActivityPanel.setVisible(true);
        });
    }

    private boolean isOvertimeChanged(BigDecimal entryOvertime, BigDecimal formOvertime) {
        if (entryOvertime == null && formOvertime == null) {
            return false;
        }

        if (entryOvertime == null || formOvertime == null) {
            return true;
        }

        return entryOvertime.compareTo(formOvertime) != 0;
    }

    private void createDailyLogic() {
        if (timesheetPanel.getStartTime() == null) {
            if (BrowserResolver.isMobile()) {
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
        dailyEntryIsNullFallback();

        UUID myselfId = dailyEntryDTO.employeeId();

        Instant startTime = timesheetPanel.getStartTime();
        Instant endTime = timesheetPanel.getEndTime();
        BigDecimal formOvertime = timesheetPanel.getOvertime();
        BigDecimal entryOvertime = dailyEntryDTO.overTime();
        JobPositionDTO jobPosition = timesheetPanel.getJobPosition();

        boolean changed = !Objects.equals(startTime, dailyEntryDTO.startTime())
                || !Objects.equals(endTime, dailyEntryDTO.endTime())
                || !Objects.equals(dailyEntryDTO.jobPosition(), jobPosition);

        if (!changed) {
            changed = isOvertimeChanged(entryOvertime, formOvertime);
        }

        if (!changed) {
            return;
        }

        var updateCommand = new UpdateDailyEntryCommand.UpdateDailyEntryInformation(
                myselfId, dailyEntryDTO.entryDate(), myselfId, startTime, endTime, formOvertime, jobPosition, "");

        sharedUpdateDailyEntryLogic(updateCommand);
    }

    private void updateAttendance(DailyAttendanceStatus dailyAttendanceStatus) {
        dailyEntryIsNullFallback();

        if (dailyEntryDTO.attendance().equals(dailyAttendanceStatus)) {
            return;
        }
        UUID myselfId = dailyEntryDTO.employeeId();

        var updateCommand = new UpdateDailyEntryCommand.UpdateAttendance(
                myselfId, dailyEntryDTO.entryDate(), myselfId, dailyAttendanceStatus, "");

        sharedUpdateDailyEntryLogic(updateCommand);
    }

    private void sharedUpdateDailyEntryLogic(UpdateDailyEntryCommand updateCommand) {
        try {
            var optionalDailyEntryDTO = coreAPI.updateDailyEntrySelfPermission(updateCommand);
            if (optionalDailyEntryDTO.isPresent()) {
                dailyEntryDTO = optionalDailyEntryDTO.get();
                updateDependsOnSelectedDate(optionalDailyEntryDTO.get().entryDate());
                new SuccessNotification(getTranslation("dailyView.updateTimesheetSuccess"));
            } else {
                new FailNotification(getTranslation("dailyView.failNotification"));
            }
        } catch (NotAuthenticatedException e) {
            new NotAuthenticatedNotification(e.getMessage());
        }
    }

    private void approveDailyEntry() {
        var updateCommand = new UpdateDailyEntryCommand.ApproveEntry(
                dailyEntryDTO.employeeId(),
                dailyEntryDTO.entryDate(),
                dailyEntryDTO.startTime(),
                dailyEntryDTO.endTime(),
                dailyEntryDTO.overTime(),
                dailyEntryDTO.status(),
                authenticationResolver.getPrincipal().getEmployeeId(),
                DailyEntryStatus.APPROVED,
                "");

        try {
            var optionalDailyEntryDTO = coreAPI.approveDailyEntry(updateCommand);
            if (optionalDailyEntryDTO.isPresent()) {
                dailyEntryDTO = optionalDailyEntryDTO.get();
                updateDependsOnSelectedDate(optionalDailyEntryDTO.get().entryDate());
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

    private void dailyEntryIsNullFallback() {
        if (dailyEntryDTO == null) {
            new FailNotification(getTranslation("dailyView.failNotification"));
            UI.getCurrent().refreshCurrentRoute(true);
        }
    }
}
