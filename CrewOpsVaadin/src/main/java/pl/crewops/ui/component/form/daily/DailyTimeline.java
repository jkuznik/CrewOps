package pl.crewops.ui.component.form.daily; // Zmieniamy pakiet na form/daily dla spójności

import com.vaadin.componentfactory.timeline.Timeline;
import com.vaadin.componentfactory.timeline.model.Item;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import pl.crewops.model.dto.dailyEntry.DailyEntryDTO;
import pl.crewops.ui.component.custom.PanelCustom;
import pl.crewops.ui.component.custom.TimelineCustom;

public class DailyTimeline extends PanelCustom {

    public static final int TIMELINE_CONTAINER_HEIGHT_PX = 200;

    private final Timeline timeline = new TimelineCustom();

    public DailyTimeline(DailyEntryDTO dailyEntry) {

        var panelContent = configuredPanelContent();
        setContent(panelContent);
    }

    private Component configuredPanelContent() {
        var mainLayout = new HorizontalLayout(configuredTimeline());
        mainLayout.setWidthFull();
        mainLayout.setMaxHeight(TIMELINE_CONTAINER_HEIGHT_PX + "px");

        return mainLayout;
    }

    private Timeline configuredTimeline() {
        LocalDate today = LocalDate.now();
        timeline.setTimelineRange(LocalDateTime.of(today, LocalTime.MIN), LocalDateTime.of(today, LocalTime.MAX));

        return timeline;
    }

    public void updateTimeline(DailyEntryDTO dailyEntry, LocalDate date) {
        if (dailyEntry == null) {
            setSummaryText(getTranslation("dailyTimeline.header", date.toString()));
            timeline.setItems(List.of());
            timeline.setTimelineRange(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
            return;
        }

        var from = dailyEntry.entryDate();
        setSummaryText(getTranslation("dailyTimeline.header", from.toString()));

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
