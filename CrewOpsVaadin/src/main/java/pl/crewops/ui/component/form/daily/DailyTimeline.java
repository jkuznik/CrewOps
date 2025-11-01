package pl.crewops.ui.component.form.daily; // Zmieniamy pakiet na form/daily dla spójności

import com.vaadin.componentfactory.timeline.Timeline;
import com.vaadin.componentfactory.timeline.model.Item;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import pl.crewops.enums.DailyAttendanceStatus;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.dto.dailyEntry.DailyEntryDTO;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;
import pl.crewops.ui.component.custom.ComboBoxCustom;
import pl.crewops.ui.component.custom.PanelCustom;
import pl.crewops.ui.component.custom.TimelineCustom;
import pl.crewops.ui.component.notification.NotAuthenticatedNotification;
import pl.crewops.util.SpringContextBridge;

public class DailyTimeline extends PanelCustom {

    public static final int TIMELINE_CONTAINER_HEIGHT_PX = 200;

    private final Timeline timeline = new TimelineCustom();

    private final Span attendanceHeaderTextLabel = new Span();
    private final Span statusDisplay = new Span();
    private final ComboBoxCustom<JobPositionDTO> jobPosition = new ComboBoxCustom<>();

    private DailyEntryDTO dailyEntry;

    public DailyTimeline(DailyEntryDTO dailyEntry) {

        this.dailyEntry = dailyEntry;

        var panelContent = configuredPanelContent();
        setContent(panelContent);

        if (dailyEntry != null) {
            setAttendanceStatus(dailyEntry.attendance());
        } else {
            setAttendanceStatus(DailyAttendanceStatus.NULL);
        }
    }

    private Component configuredPanelContent() {
        var mainLayout = new HorizontalLayout(configuredTimeline(), configuredAttendanceContainer());
        mainLayout.setWidthFull();
        mainLayout.setSpacing(true);
        mainLayout.getStyle().set("padding", "10px");
        mainLayout.setMaxHeight(TIMELINE_CONTAINER_HEIGHT_PX + "px");

        return mainLayout;
    }

    private void updateJobPositionComboBox() {
        var coreAPI = SpringContextBridge.getBean(CoreAPI.class);

        try {
            List<JobPositionDTO> allJobPositions = coreAPI.getAllJobPositions();
            jobPosition.setItems(allJobPositions);
        } catch (NotAuthenticatedException e) {
            new NotAuthenticatedNotification(e.getMessage());
        }
    }

    private Component configuredAttendanceContainer() {

        var container = new VerticalLayout();
        container.setSizeUndefined();
        container.setPadding(true);
        container.setSpacing(false);

        attendanceHeaderTextLabel.setText(getTranslation("dailyTimeline.attendanceStatusHeader"));
        statusDisplay.getStyle().set("font-weight", "bold");
        statusDisplay.getStyle().set("line-height", "1.5");

        jobPosition.setLabel(getTranslation("dailyTimeline.jobPositionLabel"));
        updateJobPositionComboBox();

        jobPosition.setItemLabelGenerator(jobPosition -> {
            if (jobPosition.machine() != null) {
                return jobPosition.name() + " (" + jobPosition.machine().registerNumber() + ")";
            } else {
                return jobPosition.name();
            }
        });

        if (dailyEntry != null && dailyEntry.jobPosition() != null) {
            jobPosition.setValue(dailyEntry.jobPosition());
        }

        container.add(attendanceHeaderTextLabel, statusDisplay, jobPosition);

        return container;
    }

    public JobPositionDTO getJobPosition() {
        return jobPosition.getValue();
    }

    public void setAttendanceStatus(DailyAttendanceStatus status) {

        statusDisplay.getStyle().remove("color");
        statusDisplay.getStyle().set("font-weight", "bold");

        switch (status) {
            case PRESENT -> {
                statusDisplay.getStyle().set("color", "#10D965"); // LUMO_SUCCESS green like
                statusDisplay.setText(getTranslation("dailyTimeline.present"));
            }
            case VACATION -> {
                statusDisplay.getStyle().set("color", "#007bff"); // Blue
                statusDisplay.setText(getTranslation("dailyTimeline.vacation"));
            }
            case SICK_LEAVE -> {
                statusDisplay.getStyle().set("color", "#007bff"); // Blue
                statusDisplay.setText(getTranslation("dailyTimeline.sickLeave"));
            }
            case ABSENT -> {
                statusDisplay.getStyle().set("color", "red");
                statusDisplay.setText(getTranslation("dailyTimeline.absent"));
            }
            case OTHER -> {
                statusDisplay.getStyle().set("color", "gray");
                statusDisplay.setText(getTranslation("dailyTimeline.other"));
            }
            case NULL -> {
                statusDisplay.getStyle().set("color", "gray");
                statusDisplay.setText(getTranslation("dailyTimeline.noEntry"));
            }
        }
    }

    private Timeline configuredTimeline() {
        LocalDate today = LocalDate.now();
        timeline.setTimelineRange(LocalDateTime.of(today, LocalTime.MIN), LocalDateTime.of(today, LocalTime.MAX));

        return timeline;
    }

    public void updateTimeline(DailyEntryDTO dailyEntry, LocalDate date) {
        if (dailyEntry == null) {
            setSummaryText(getTranslation("dailyTimeline.header", date.toString()));
            jobPosition.setValue(null);
            timeline.setItems(List.of());
            timeline.setTimelineRange(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
            setAttendanceStatus(DailyAttendanceStatus.NULL);
            return;
        }

        var from = dailyEntry.entryDate();
        setSummaryText(getTranslation("dailyTimeline.header", from.toString()));

        final ZoneId ZONE_ID = ZoneId.systemDefault();

        setAttendanceStatus(dailyEntry.attendance());

        if (dailyEntry.jobPosition() != null) {
            jobPosition.setValue(dailyEntry.jobPosition());
        } else {
            jobPosition.setValue(null);
        }

        if (dailyEntry.endTime() != null) {
            LocalDateTime to = LocalDateTime.ofInstant(dailyEntry.endTime(), ZONE_ID);
            timeline.setTimelineRange(
                    LocalDateTime.of(from, LocalTime.MIN), LocalDateTime.of(to.toLocalDate(), LocalTime.MAX));
        } else {
            timeline.setTimelineRange(LocalDateTime.of(from, LocalTime.MIN), LocalDateTime.of(from, LocalTime.MAX));
        }

        var items = new ArrayList<Item>();

        if (dailyEntry.startTime() != null) {
            LocalDateTime workStart = LocalDateTime.ofInstant(dailyEntry.startTime(), ZONE_ID);

            if (dailyEntry.endTime() != null) {
                LocalDateTime workEnd = LocalDateTime.ofInstant(dailyEntry.endTime(), ZONE_ID);

                var workItem = new Item(workStart, workEnd, "Praca");
                workItem.setId(UUID.randomUUID().toString());
                workItem.setClassName("timeline-item-default");
                workItem.setEditable(false);
                workItem.setUpdateTime(false);

                items.add(workItem);
            } else {
                LocalDateTime softEnd = workStart.plusHours(4);

                var ongoingItem = new Item(workStart, softEnd, "Praca");
                ongoingItem.setId(UUID.randomUUID().toString());
                ongoingItem.setClassName("timeline-item-ongoing");
                ongoingItem.setEditable(false);
                ongoingItem.setUpdateTime(false);

                items.add(ongoingItem);
            }
        }

        Item overtimeItem = createOvertimeItem(dailyEntry);
        if (overtimeItem != null) {
            items.add(overtimeItem);
        }

        timeline.setItems(items);
    }

    // todo: i18n
    private Item createOvertimeItem(DailyEntryDTO dailyEntry) {
        if (dailyEntry.endTime() == null
                || dailyEntry.overTime() == null
                || dailyEntry.overTime().compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        final ZoneId ZONE_ID = ZoneId.systemDefault();

        LocalDateTime end = LocalDateTime.ofInstant(dailyEntry.endTime(), ZONE_ID);

        BigDecimal overtimeHours = dailyEntry.overTime();
        long overtimeMinutes = overtimeHours.multiply(BigDecimal.valueOf(60)).longValue();
        LocalDateTime start = end.minusMinutes(overtimeMinutes);

        Item overtimeItem = new Item(start, end, "Praca - Nadgodziny");
        overtimeItem.setId("overtime"); // id możesz generować dynamicznie jeśli jest kilka
        overtimeItem.setClassName("timeline-item-overtime");

        overtimeItem.setEditable(false);
        overtimeItem.setUpdateTime(false);

        return overtimeItem;
    }
}
