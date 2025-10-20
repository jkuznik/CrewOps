package pl.crewops.component.custom;

import static pl.crewops.view.DailyView.FORMS_BORDER_PX;

import com.vaadin.componentfactory.timeline.Timeline;
import com.vaadin.componentfactory.timeline.model.Item;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.TextField;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import pl.crewops.enums.DailyAttendanceStatus;
import pl.crewops.model.dto.dailyEntry.DailyEntryDTO;

public class DailyTimeline extends HorizontalLayout {

    private static final String TIMELINE_WIDTH_PX = "1200px";
    private static final int TIMELINE_CONTAINER_HEIGHT_PX = 200;

    private final Timeline timeline = new Timeline();
    private final Span statusDisplay = new Span();
    private final Icon helpIcon = VaadinIcon.INFO_CIRCLE.create();
    private final TextField jobPosition = new TextField();

    private DailyEntryDTO dailyEntry;

    public DailyTimeline(DailyEntryDTO dailyEntry) {
        this.dailyEntry = dailyEntry;
        configureStyles();

        add(configuredTimeline(), configuredAttendanceContainer());
    }

    private void configureStyles() {

        setMinWidth(TIMELINE_WIDTH_PX);
        setMaxWidth(TIMELINE_WIDTH_PX);
        setMaxHeight(TIMELINE_CONTAINER_HEIGHT_PX + "px");
        getStyle().remove("overflow-x");
        getStyle().remove("overflow-y");
        getStyle().set("border", FORMS_BORDER_PX + " solid #ccc");
        getStyle().set("border-radius", "4px");
    }

    private Component configuredAttendanceContainer() {
        var horizontalLayout = new HorizontalLayout();

        horizontalLayout.setPadding(true);
        horizontalLayout.setJustifyContentMode(HorizontalLayout.JustifyContentMode.END);

        statusDisplay.getStyle().set("font-weight", "bold");
        statusDisplay.getStyle().set("padding", "var(--lumo-space-s)");
        statusDisplay.getStyle().set("line-height", "1.5");

        helpIcon.setColor("var(--lumo-contrast-50pct)");
        helpIcon.getStyle().set("cursor", "pointer");

        Tooltip.forComponent(helpIcon).withText(getHelpText()).withPosition(Tooltip.TooltipPosition.BOTTOM_END);

        horizontalLayout.add(statusDisplay, helpIcon);

        var container = new VerticalLayout();

        if (dailyEntry != null && dailyEntry.jobPosition() != null) {
            jobPosition.setValue(dailyEntry.jobPosition().name());
        }

        container.add(horizontalLayout, jobPosition);

        return container;
    }

    public void setAttendanceStatus(DailyAttendanceStatus status) {

        statusDisplay.getStyle().remove("color");

        String translatedLabel = getTranslatedLabel(status);
        statusDisplay.setText(translatedLabel);
        statusDisplay.getStyle().set("font-weight", "bold");

        switch (status) {
            case PRESENT -> {
                // LUMO_SUCCESS green like
                statusDisplay.getStyle().set("color", "#10D965");
            }
            case VACATION -> {
                // Blue
                statusDisplay.getStyle().set("color", "#007bff");
            }
            case SICK_LEAVE -> {
                statusDisplay.getStyle().set("color", "orange");
            }
            case ABSENT -> {
                statusDisplay.getStyle().set("color", "red");
            }
            case OTHER -> {
                statusDisplay.getStyle().set("color", "gray");
            }
            default -> {
                statusDisplay.getStyle().set("color", "black");
            }
        }
    }

    private Timeline configuredTimeline() {
        LocalDate today = LocalDate.now();
        timeline.setTimelineRange(LocalDateTime.of(today, LocalTime.MIN), LocalDateTime.of(today, LocalTime.MAX));

        timeline.setWidthFull();
        timeline.setHeight(TIMELINE_CONTAINER_HEIGHT_PX - 5 + "px");
        timeline.setMoveable(true);
        timeline.setZoomable(true);
        timeline.setShowCurentTime(true);

        return timeline;
    }

    public void updateTimeline(DailyEntryDTO dailyEntry, LocalDate date) {
        if (dailyEntry == null) {
            statusDisplay.setText("");
            jobPosition.setValue("");
            timeline.setItems(List.of());
            timeline.setTimelineRange(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
            return;
        }

        setAttendanceStatus(dailyEntry.attendance());
        if (dailyEntry.jobPosition() != null) {
            jobPosition.setValue(dailyEntry.jobPosition().name());
        } else {
            jobPosition.setValue("");
        }
        var from = dailyEntry.entryDate();
        final ZoneId ZONE_ID = ZoneId.systemDefault();

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
            items.add(createOvertimeItem(dailyEntry));
        }

        timeline.setItems(items);
    }

    // todo: i18n
    private Item createOvertimeItem(DailyEntryDTO dailyEntry) {
        if (dailyEntry.endTime() == null
                || dailyEntry.overTime() == null
                || dailyEntry.overTime().compareTo(BigDecimal.ZERO) <= 0) {
            return null; // brak danych do wygenerowania overtime
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

    private String getTranslatedLabel(DailyAttendanceStatus item) {
        switch (item) {
            case PRESENT -> {
                return getTranslation("dailyTimeline.present");
            }
            case VACATION -> {
                return getTranslation("dailyTimeline.vacation");
            }
            case SICK_LEAVE -> {
                return getTranslation("dailyTimeline.sickLeave");
            }
            case OTHER -> {
                return getTranslation("dailyTimeline.other");
            }
            case ABSENT -> {
                return getTranslation("dailyTimeline.absent");
            }
            default -> {
                return "";
            }
        }
    }

    private String getHelpText() {
        return getTranslation("dailyTimeline.helpText.title") + "\n"
                + getTranslation("dailyTimeline.helpText.1") + "\n"
                + getTranslation("dailyTimeline.helpText.2") + "\n"
                + getTranslation("dailyTimeline.helpText.3") + "\n"
                + getTranslation("dailyTimeline.helpText.4") + "\n"
                + getTranslation("dailyTimeline.helpText.5") + "\n"
                + getTranslation("dailyTimeline.helpText.6");
    }
}
