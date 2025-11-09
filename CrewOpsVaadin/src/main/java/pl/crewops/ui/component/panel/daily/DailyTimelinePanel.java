package pl.crewops.ui.component.panel.daily;

import com.vaadin.componentfactory.timeline.Timeline;
import com.vaadin.componentfactory.timeline.model.Item;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import pl.crewops.model.dto.dailyEntry.DailyEntryAuditDTO;
import pl.crewops.model.dto.dailyEntry.DailyEntryDTO;
import pl.crewops.ui.component.custom.PanelCustom;
import pl.crewops.ui.component.custom.TimelineCustom;

public class DailyTimelinePanel extends PanelCustom {

    // Zwiększamy wysokość kontenera, aby zmieścić pasek narzędzi z przyciskiem audytu
    public static final int TIMELINE_CONTAINER_HEIGHT_PX = 240;

    private final Timeline timeline = new TimelineCustom();
    private final Button auditToggleButton = new Button();

    // Zmienne stanu do zarządzania widokiem
    private DailyEntryDTO currentDailyEntry;
    private LocalDate currentDate;
    private boolean showAuditEvents = false;

    public DailyTimelinePanel(DailyEntryDTO dailyEntry) {
        var panelContent = configuredPanelContent();
        setContent(panelContent);
        // Ustawienie początkowego stanu na podstawie danych
        this.currentDailyEntry = dailyEntry;
        this.currentDate = dailyEntry != null ? dailyEntry.entryDate() : LocalDate.now();
        updateTimelineItemsAndRange();
    }

    private Component configuredPanelContent() {

        // Konfiguracja przycisku audytu (i18n)
        auditToggleButton.setText(getTranslation("dailyTimeline.toggleAudit"));
        auditToggleButton.setIcon(VaadinIcon.FILE_TREE_SUB.create());
        auditToggleButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);

        // Listener przełączający stan i styl przycisku
        auditToggleButton.addClickListener(event -> {
            boolean newState = !showAuditEvents;
            setShowAuditEvents(newState); // Wywołuje updateTimelineItemsAndRange()

            // Zmiana stylu przycisku
            if (newState) {
                auditToggleButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            } else {
                auditToggleButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
            }
        });

        // Pasek na przycisk audytu (wyrównany do prawej)
        var headerLayout = new HorizontalLayout(auditToggleButton);
        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        headerLayout.setPadding(false);
        headerLayout.setSpacing(false);

        // Główny kontener: Przycisk + Oś czasu
        var contentLayout = new VerticalLayout(headerLayout, configuredTimeline());
        contentLayout.setWidthFull();
        contentLayout.setMaxHeight(TIMELINE_CONTAINER_HEIGHT_PX + "px");
        contentLayout.setSpacing(false);
        contentLayout.setPadding(false);

        return contentLayout;
    }

    private Timeline configuredTimeline() {
        LocalDate today = LocalDate.now();
        timeline.setTimelineRange(LocalDateTime.of(today, LocalTime.MIN), LocalDateTime.of(today, LocalTime.MAX));

        return timeline;
    }

    /**
     * Główna metoda aktualizująca dane i zakres osi czasu.
     */
    private void updateTimelineItemsAndRange() {
        if (currentDailyEntry == null) {
            setSummaryText(getTranslation("dailyTimeline.header", currentDate.toString()));
            timeline.setItems(List.of());
            timeline.setTimelineRange(
                    LocalDateTime.of(currentDate, LocalTime.MIN), LocalDateTime.of(currentDate, LocalTime.MAX));
            return;
        }

        var from = currentDailyEntry.entryDate();
        setSummaryText(getTranslation("dailyTimeline.header", from.toString()));

        final ZoneId ZONE_ID = ZoneId.systemDefault();
        var items = new ArrayList<Item>();

        // 1. Dodanie elementów PRACY i NADGODZIN (zawsze widoczne)
        items.addAll(createWorkAndOvertimeItems(currentDailyEntry));

        // 2. Opcjonalne dodanie elementów AUDYTU
        if (showAuditEvents) {
            items.addAll(createAuditTimelineItems(currentDailyEntry));
        }

        // 3. Ustalenie zakresu czasowego (Range)
        LocalDateTime rangeStart = LocalDateTime.of(from, LocalTime.MIN);
        LocalDateTime rangeEnd = LocalDateTime.of(from, LocalTime.MAX);

        // Rozszerzenie zakresu do końca dnia pracy (jeśli jest endTime)
        if (currentDailyEntry.endTime() != null) {
            LocalDateTime workEnd = LocalDateTime.ofInstant(currentDailyEntry.endTime(), ZONE_ID);
            rangeEnd = LocalDateTime.of(workEnd.toLocalDate(), LocalTime.MAX);
        }

        // Rozszerzenie zakresu, jeśli audyt jest widoczny i wpływa na daty
        if (showAuditEvents
                && currentDailyEntry.auditEvents() != null
                && !currentDailyEntry.auditEvents().isEmpty()) {

            var allAuditTimes = currentDailyEntry.auditEvents().stream()
                    .map(DailyEntryAuditDTO::createdAt)
                    .filter(Objects::nonNull)
                    .toList();

            if (!allAuditTimes.isEmpty()) {

                Instant minAuditTime =
                        allAuditTimes.stream().min(Instant::compareTo).get();
                LocalDateTime auditStart = LocalDateTime.ofInstant(minAuditTime, ZONE_ID);

                if (auditStart.toLocalDate().isBefore(rangeStart.toLocalDate())) {
                    rangeStart = LocalDateTime.of(auditStart.toLocalDate(), LocalTime.MIN);
                }

                Instant maxAuditTime =
                        allAuditTimes.stream().max(Instant::compareTo).get();
                LocalDateTime auditEnd = LocalDateTime.ofInstant(maxAuditTime, ZONE_ID);

                if (auditEnd.toLocalDate().isAfter(rangeEnd.toLocalDate())) {
                    rangeEnd = LocalDateTime.of(auditEnd.toLocalDate(), LocalTime.MAX);
                }
            }
        }

        timeline.setTimelineRange(rangeStart, rangeEnd);
        timeline.setItems(items);
    }

    /**
     * Wydzielona metoda do tworzenia elementów czasu pracy i nadgodzin.
     */
    private List<Item> createWorkAndOvertimeItems(DailyEntryDTO dailyEntry) {
        var items = new ArrayList<Item>();
        final ZoneId ZONE_ID = ZoneId.systemDefault();

        if (dailyEntry.startTime() != null) {
            LocalDateTime workStart = LocalDateTime.ofInstant(dailyEntry.startTime(), ZONE_ID);

            if (dailyEntry.endTime() != null) {
                LocalDateTime workEnd = LocalDateTime.ofInstant(dailyEntry.endTime(), ZONE_ID);

                var workItem = new Item(workStart, workEnd, "Praca"); // todo: i18n
                workItem.setId(UUID.randomUUID().toString());
                workItem.setClassName("timeline-item-default");
                workItem.setEditable(false);
                workItem.setUpdateTime(false);

                items.add(workItem);
            } else {
                LocalDateTime softEnd = workStart.plusHours(4);

                var ongoingItem = new Item(workStart, softEnd, "Praca"); // todo: i18n
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

        return items;
    }

    /**
     * Tworzy elementy Timeline (Item) dla zdarzeń audytowych Daily Entry,
     * mapując typ audytu na klasę CSS.
     */
    private List<Item> createAuditTimelineItems(DailyEntryDTO dailyEntry) {
        if (dailyEntry.auditEvents() == null || dailyEntry.auditEvents().isEmpty()) {
            return List.of();
        }

        final ZoneId ZONE_ID = ZoneId.systemDefault();
        var auditItems = new ArrayList<Item>();

        for (DailyEntryAuditDTO audit : dailyEntry.auditEvents()) {

            if (audit.createdAt() == null) {
                continue;
            }

            LocalDateTime eventStart = LocalDateTime.ofInstant(audit.createdAt(), ZONE_ID);
            LocalDateTime eventEnd = eventStart.plusMinutes(1);

            // todo: i18n
            String titleKey = "dailyAudit.eventType." + audit.eventType().name();
            String title = getTranslation(titleKey, audit.eventType().toString());

            StringBuilder labelBuilder = new StringBuilder(title);

            if (audit.comment() != null && !audit.comment().isBlank()) {
                String summaryComment =
                        audit.comment().length() > 30 ? audit.comment().substring(0, 30) + "..." : audit.comment();
                labelBuilder.append(" (").append(summaryComment).append(")");
            }

            var auditItem = new Item(eventStart, eventEnd, labelBuilder.toString());

            auditItem.setId("audit-" + audit.id().toString());

            // === LOGIKA MAPOWANIA ENUM NA KLASĘ CSS ===
            String auditClassName = "timeline-item-audit-default";

            switch (audit.eventType()) {
                case INFORMATION_MODIFIED:
                    auditClassName = "timeline-item-audit-info";
                    break;
                case ATTENDANCE_STATUS_CHANGED:
                    auditClassName = "timeline-item-audit-warning";
                    break;
                case SAFETY_NOTE_ADDED:
                    auditClassName = "timeline-item-audit-error";
                    break;
                case REPORT_STATUS_CHANGED:
                    auditClassName = "timeline-item-audit-process";
                    break;
                case OVERTIME_MODIFIED:
                    auditClassName = "timeline-item-audit-financial";
                    break;
                case ENTRY_STATUS_CHANGED:
                    auditClassName = "timeline-item-audit-success";
                    break;
            }

            auditItem.setClassName(auditClassName);
            // ===========================================

            auditItem.setEditable(false);
            auditItem.setUpdateTime(false);

            auditItems.add(auditItem);
        }

        return auditItems;
    }

    // Metoda przyjmująca nowe dane i wywołująca odświeżanie
    public void updateTimeline(DailyEntryDTO dailyEntry, LocalDate date) {
        this.currentDailyEntry = dailyEntry;
        this.currentDate = date;
        updateTimelineItemsAndRange();
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

        Item overtimeItem = new Item(start, end, "Praca - Nadgodziny"); // todo: i18n
        overtimeItem.setId("overtime");
        overtimeItem.setClassName("timeline-item-overtime");

        overtimeItem.setEditable(false);
        overtimeItem.setUpdateTime(false);

        return overtimeItem;
    }

    /**
     * Przełącza widoczność zdarzeń audytowych na osi czasu i odświeża widok.
     */
    public void setShowAuditEvents(boolean showAuditEvents) {
        if (this.showAuditEvents != showAuditEvents) {
            this.showAuditEvents = showAuditEvents;
            updateTimelineItemsAndRange(); // Ponowne przeliczenie elementów i zakresu
        }
    }

    public boolean getShowAuditEvents() {
        return showAuditEvents;
    }
}
