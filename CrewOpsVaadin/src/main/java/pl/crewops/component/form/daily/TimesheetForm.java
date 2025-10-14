package pl.crewops.component.form.daily;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.timepicker.TimePicker;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import pl.crewops.enums.DateState;
import pl.crewops.enums.OvertimeInterval;
import pl.crewops.enums.OvertimeInterval.OvertimeValue;
import pl.crewops.model.dto.dailyEntry.DailyEntryDTO;
import pl.crewops.util.contract.DateSensitive;
import pl.crewops.view.DailyView;

public class TimesheetForm extends FormLayout implements DateSensitive {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM (EEE)");

    // todo: i18n
    private final Span headerTextLabel = new Span("Czas Pracy");
    private final Icon helpIcon = VaadinIcon.INFO_CIRCLE.create();

    private final TimePicker from = new TimePicker("Od");
    private final Select<LocalDate> dateFromSelect = new Select<>();

    private final TimePicker to = new TimePicker("Do");
    private final Select<LocalDate> dateToSelect = new Select<>();

    private final Select<OvertimeInterval> overtime = new Select<>();
    private final VerticalLayout hoursSummary = initialHourSummaryContent();

    private LocalDate selectedDate = LocalDate.now();

    public TimesheetForm() {

        var fromLayout = createDateTimeLayout(from, dateFromSelect);
        var toLayout = createDateTimeLayout(to, dateToSelect);

        var overtimeLayout = createOvertimeLayout(overtime, hoursSummary);

        configureValueFields();
        configureOvertime();

        var spacer = new Div();
        spacer.setHeight("200px");

        var mainContainer = configuredMainContainer();

        mainContainer.add(configuredHeader(), fromLayout, toLayout, overtimeLayout, spacer);

        add(mainContainer);
    }

    public void setDailyEntry(DailyEntryDTO dailyEntryDTO) {

        if (dailyEntryDTO == null) {
            from.setValue(null);
            to.setValue(null);
            updateDependsOnDate(selectedDate);

            return;
        }

        from.setValue(LocalTime.ofInstant(dailyEntryDTO.startTime(), ZoneId.systemDefault()));
        to.setValue(LocalTime.ofInstant(dailyEntryDTO.endTime(), ZoneId.systemDefault()));

        if (dailyEntryDTO.startTime().isAfter(dailyEntryDTO.endTime())) {
            dateFromSelect.setValue(dailyEntryDTO.entryDate().plusDays(1L));
        }

        updateHoursSummary();
    }

    public Instant getStartTime() {
        LocalTime timeFrom = from.getValue();

        if (timeFrom == null) {
            return null;
        }

        LocalDateTime dateTimeFrom = LocalDateTime.of(this.selectedDate, timeFrom);

        ZoneId zoneId = ZoneId.systemDefault();

        return dateTimeFrom.atZone(zoneId).toInstant();
    }

    public Instant getEndTime() {
        LocalTime timeTo = to.getValue();
        LocalDate dateTo = dateToSelect.getValue();

        if (timeTo == null || dateTo == null) {
            return null;
        }

        LocalDateTime dateTimeTo = LocalDateTime.of(dateTo, timeTo);

        ZoneId zoneId = ZoneId.systemDefault();

        return dateTimeTo.atZone(zoneId).toInstant();
    }

    /**
     * Zwraca wybraną wartość nadgodzin (z pola 'overtime') w godzinach jako BigDecimal.
     *
     * UWAGA: Dla OvertimeInterval.ALL metoda ZWRACA AKTUALNIE OBLICZONY CZAS PRACY.
     * Jeśli czas pracy nie jest ustawiony, zwraca 0.
     *
     * @return Liczba godzin nadgodzin jako BigDecimal.
     */
    public BigDecimal getOvertime() {
        OvertimeInterval selectedOvertime = overtime.getValue();

        if (selectedOvertime == null || selectedOvertime == OvertimeInterval.H00_00) {
            return BigDecimal.ZERO;
        }

        if (selectedOvertime == OvertimeInterval.ALL) {
            long totalMinutes = calculateWorkDurationMinutes();

            if (totalMinutes <= 0) {
                return BigDecimal.ZERO;
            }

            return new BigDecimal(totalMinutes).divide(new BigDecimal(60), 2, RoundingMode.HALF_UP);
        }

        OvertimeInterval.OvertimeValue ov = selectedOvertime.getValue();
        long minutes = ov.hours() * 60L + ov.minutes();

        return new BigDecimal(minutes).divide(new BigDecimal(60), 2, RoundingMode.HALF_UP);
    }

    private void configureOvertime() {
        // todo: i18n
        overtime.setLabel("W tym nadgodzin");
        overtime.setItemLabelGenerator(interval -> {
            if (interval == OvertimeInterval.ALL) {
                // todo: i18n - "Cały czas pracy"
                return "Całkowity czas pracy";
            } else {
                return String.format(
                        "%d:%02d",
                        interval.getValue().hours(), interval.getValue().minutes());
            }
        });
        overtime.setItems(OvertimeInterval.H00_00);
        overtime.setValue(OvertimeInterval.H00_00);
    }

    /**
     * Konwertuje całkowitą liczbę minut na format HH:mm.
     * @param totalMinutes Całkowita liczba minut.
     * @return Sformatowany ciąg znaków w postaci "HH:mm".
     */
    private String formatMinutesToHHMM(long totalMinutes) {
        if (totalMinutes < 0) {
            return "0:00";
        }
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        return String.format("%d:%02d", hours, minutes);
    }

    private void updateHoursSummary() {
        // 1. Obliczenie łącznego przepracowanego czasu pracy
        long totalDurationMinutes = calculateWorkDurationMinutes();

        OvertimeInterval selectedOvertime = overtime.getValue();
        long actualOvertimeMinutes = 0;

        if (selectedOvertime != null) {
            if (selectedOvertime == OvertimeInterval.ALL) {
                // Jeśli wybrano "Całkowity czas pracy", nadgodziny = łączny czas pracy.
                actualOvertimeMinutes = totalDurationMinutes;
            } else {
                // Standardowe obliczenia dla H00_00...H24_00
                OvertimeValue ov = selectedOvertime.getValue();
                actualOvertimeMinutes = ov.hours() * 60L + ov.minutes();
            }
        }

        actualOvertimeMinutes = Math.max(0, actualOvertimeMinutes);

        long normalWorkMinutes = totalDurationMinutes - actualOvertimeMinutes;
        normalWorkMinutes = Math.max(0, normalWorkMinutes);

        String totalTimeFormatted = formatMinutesToHHMM(totalDurationMinutes);
        String overtimeFormatted = formatMinutesToHHMM(actualOvertimeMinutes);
        String normalWorkFormatted = formatMinutesToHHMM(normalWorkMinutes);

        hoursSummary.removeAll();

        // todo: i18n
        Span normalSpan = new Span(String.format("Normalny czas pracy: %s", normalWorkFormatted));
        Span overtimeSpan = new Span(String.format("Nadgodziny: %s", overtimeFormatted));
        Span totalSpan = new Span(String.format("Łączny czas pracy: %s", totalTimeFormatted));

        if (actualOvertimeMinutes > 0) {
            overtimeSpan.getStyle().set("color", "var(--lumo-error-text-color)");
            overtimeSpan.getStyle().set("font-weight", "bold");
        }

        // Dodanie stylu do łącznego czasu pracy (dla wizualnego wyróżnienia sumy)
        totalSpan.getStyle().set("font-weight", "bold");

        hoursSummary.add(normalSpan, overtimeSpan, totalSpan);
    }

    /**
     * Filtruje dostępne opcje OvertimeInterval na podstawie maksymalnego czasu pracy.
     * @param maxMinutes Maksymalna dozwolona liczba minut nadgodzin (całkowity czas pracy).
     */
    private void filterOvertimeOptions(long maxMinutes) {
        List<OvertimeInterval> filteredItems = Arrays.stream(OvertimeInterval.values())
                .filter(interval -> {
                    OvertimeValue ov = interval.getValue();
                    long intervalMinutes = ov.hours() * 60L + ov.minutes();
                    return intervalMinutes <= maxMinutes;
                })
                .collect(Collectors.toList());

        if (filteredItems.isEmpty()) {
            overtime.setItems(OvertimeInterval.H00_00);
        } else {
            overtime.setItems(filteredItems);
        }

        overtime.setValue(OvertimeInterval.H00_00);
    }

    private HorizontalLayout createOvertimeLayout(Select<?> overtimeField, VerticalLayout summaryLayout) {
        var layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setSpacing(true);
        layout.setAlignItems(FlexComponent.Alignment.END);

        overtimeField.setWidth("50%");
        summaryLayout.setWidth("50%");

        layout.add(overtimeField, summaryLayout);
        return layout;
    }

    @Override
    public void updateDependsOnDate(LocalDate localDate) {
        this.selectedDate = localDate;
        updateDateFieldsOptions();

        DateState state = DateState.fromLocalDate(localDate);

        switch (state) {
            case PAST -> {
                headerTextLabel.setText("Przepracowany Czas Pracy");
            }
            case TODAY -> {
                headerTextLabel.setText("Czas Pracy");
            }
            case FUTURE -> {
                headerTextLabel.setText("Planowany Czas Pracy");
            }
        }
    }

    private HorizontalLayout createDateTimeLayout(TimePicker timePicker, Select<LocalDate> dateSelect) {
        timePicker.setStep(Duration.ofMinutes(15));
        timePicker.setWidth("50%");
        dateSelect.setWidth("50%");
        var layout = new HorizontalLayout(timePicker, dateSelect);
        layout.setAlignItems(FlexComponent.Alignment.END);
        layout.setWidthFull();
        layout.setSpacing(true);
        return layout;
    }

    private void updateDateFieldsOptions() {
        if (selectedDate == null) {
            return;
        }

        LocalDate tomorrow = selectedDate.plusDays(1);

        dateFromSelect.setItemLabelGenerator(date -> date.format(DATE_FORMATTER));
        dateToSelect.setItemLabelGenerator(date -> {
            if (date.isEqual(selectedDate)) {
                return date.format(DATE_FORMATTER);
            } else {
                // todo: i18n
                return date.format(DATE_FORMATTER) + " (Nast. dzień/Nocna zmiana)";
            }
        });

        dateFromSelect.setItems(List.of(selectedDate));
        dateFromSelect.setValue(selectedDate);
        dateFromSelect.setReadOnly(true);

        dateToSelect.setItems(List.of(selectedDate, tomorrow));

        if (dateToSelect.getValue() == null
                || dateToSelect.getValue().isBefore(selectedDate)
                || dateToSelect.getValue().isAfter(tomorrow)) {
            dateToSelect.setValue(selectedDate);
        }
    }

    private static VerticalLayout configuredMainContainer() {
        var mainContainer = new VerticalLayout();
        mainContainer.getStyle().set("border", "1px solid #ccc");
        mainContainer.getStyle().set("border-radius", "4px");
        mainContainer.getStyle().set("padding", "10px");
        mainContainer.setMaxHeight(DailyView.FORMS_HEIGHT);
        mainContainer.setMaxWidth(DailyView.FORMS_WIDTH);
        return mainContainer;
    }

    private void configureValueFields() {
        var updateListener = (Runnable) () -> {
            long totalMinutes = calculateWorkDurationMinutes();
            filterOvertimeOptions(totalMinutes);
            updateHoursSummary();
        };

        from.addValueChangeListener(event -> updateListener.run());
        to.addValueChangeListener(event -> updateListener.run());
        dateToSelect.addValueChangeListener(event -> updateListener.run());

        overtime.addValueChangeListener(event -> {
            updateHoursSummary();
        });

        updateDateFieldsOptions();
        updateListener.run();
    }

    private HorizontalLayout configuredHeader() {
        headerTextLabel.getStyle().set("font-weight", "bold");
        headerTextLabel.getStyle().set("font-size", "1.1em");

        helpIcon.setColor("var(--lumo-contrast-50pct)");
        helpIcon.getStyle().set("cursor", "pointer");
        Tooltip.forComponent(helpIcon).withText(getHelpText()).withPosition(Tooltip.TooltipPosition.BOTTOM_END);

        var headerLayout = new HorizontalLayout();
        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        headerLayout.add(headerTextLabel, helpIcon);

        return headerLayout;
    }

    private VerticalLayout initialHourSummaryContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(false);

        layout.getStyle().set("border", "1px solid var(--lumo-contrast-10pct)");
        layout.getStyle().set("border-radius", "4px");
        layout.getStyle().set("padding", "8px");
        layout.getStyle().set("min-height", "3em");

        // todo: i18n
        layout.add(new Span("Przepracowane: 0:00"), new Span("Nadgodziny: 0:00"));

        return layout;
    }

    private long calculateWorkDurationMinutes() {
        LocalTime timeFrom = from.getValue();
        LocalTime timeTo = to.getValue();
        LocalDate dateTo = dateToSelect.getValue();

        if (timeFrom != null && timeTo != null && dateTo != null && selectedDate != null) {
            var dateTimeFrom = LocalDateTime.of(selectedDate, timeFrom);
            var dateTimeTo = LocalDateTime.of(dateTo, timeTo);

            if (dateTimeFrom.isBefore(dateTimeTo)) {
                Duration duration = Duration.between(dateTimeFrom, dateTimeTo);
                return duration.toMinutes();
            }
        }
        return -2; // It's required to return less than -1
        // to work properly with OvertimeInterval.ALL that has .value() equals -1
    }

    private boolean validateWorkTime() {
        LocalTime timeFrom = from.getValue();
        LocalTime timeTo = to.getValue();
        LocalDate dateTo = dateToSelect.getValue();

        if (timeFrom == null || timeTo == null || dateTo == null || selectedDate == null) {
            return false;
        }

        var dateTimeFrom = LocalDateTime.of(selectedDate, timeFrom);
        var dateTimeTo = LocalDateTime.of(dateTo, timeTo);

        return dateTimeFrom.isBefore(dateTimeTo);
    }

    private String getHelpText() {
        return "Zasady wprowadzania czasu:\n\n" + "1. Czas pracy (Od/Do) to cały przepracowany czas danego dnia.\n"
                + "2. Nadgodziny to ilość godzin (np. 2.5) zawarta w zadeklarowanym czasie pracy.\n"
                + "3. Jeśli cały czas przepracowany danego dnia był nadgodzinami, to ilość nadgodzin musi być równe zadeklarowanemu czasowi pracy.";
    }
}
