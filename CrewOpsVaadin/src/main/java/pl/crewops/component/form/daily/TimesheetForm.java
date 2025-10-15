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
import com.vaadin.flow.server.VaadinSession;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import pl.crewops.enums.DateState;
import pl.crewops.enums.OvertimeInterval;
import pl.crewops.enums.OvertimeInterval.OvertimeValue;
import pl.crewops.model.dto.dailyEntry.DailyEntryDTO;
import pl.crewops.util.contract.DateSensitive;
import pl.crewops.view.DailyView;

public class TimesheetForm extends FormLayout implements DateSensitive {

    private final Span headerTextLabel = new Span();
    private final Icon helpIcon = VaadinIcon.INFO_CIRCLE.create();

    private final TimePicker from = new TimePicker();
    private final Span dateFromSpan = new Span();

    private final TimePicker to = new TimePicker();
    private final Span dateToSpan = new Span();

    private final Select<OvertimeInterval> overtime = new Select<>();

    private final Span normalSpan = new Span();
    private final Span overtimeSpan = new Span();
    private final Span totalSpan = new Span();

    private LocalDate selectedDate = LocalDate.now();

    private LocalDate calculatedDateTo = LocalDate.now();

    public TimesheetForm() {
        localize();

        var fromLayout = createDateTimeLayout(from, dateFromSpan);
        var toLayout = createDateTimeLayout(to, dateToSpan);
        var overtimeLayout = createOvertimeLayout(overtime, configuredHoursSummaryLayout());

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

        OvertimeInterval overtimeValue = getOvertimeIntervalByHours(dailyEntryDTO.overTime());
        overtime.setValue(overtimeValue);

        updateHoursSummary();
    }

    public Instant getStartTime() {
        LocalTime timeFrom = from.getValue();

        if (timeFrom == null || selectedDate == null) {
            return null;
        }

        LocalDateTime dateTimeFrom = LocalDateTime.of(selectedDate, timeFrom);
        ZoneId zoneId = ZoneId.systemDefault();

        return dateTimeFrom.atZone(zoneId).toInstant();
    }

    public Instant getEndTime() {
        LocalTime timeTo = to.getValue();

        if (timeTo == null || calculatedDateTo == null) {
            return null;
        }

        LocalDateTime dateTimeTo = LocalDateTime.of(calculatedDateTo, timeTo);
        ZoneId zoneId = ZoneId.systemDefault();

        return dateTimeTo.atZone(zoneId).toInstant();
    }

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
        overtime.setLabel(getTranslation("timesheetForm.overtime.label"));
        overtime.setItemLabelGenerator(interval -> {
            if (interval == OvertimeInterval.ALL) {
                return getTranslation("timesheetForm.overtime.allTime");
            } else {
                return String.format(
                        "%d:%02d",
                        interval.getValue().hours(), interval.getValue().minutes());
            }
        });
        overtime.setItems(OvertimeInterval.H00_00);
        overtime.setValue(OvertimeInterval.H00_00);
    }

    private OvertimeInterval getOvertimeIntervalByHours(BigDecimal hours) {
        if (hours == null || hours.compareTo(BigDecimal.ZERO) <= 0) {
            return OvertimeInterval.H00_00;
        }
        long totalMinutes = hours.multiply(new BigDecimal(60))
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();

        return Arrays.stream(OvertimeInterval.values())
                .filter(interval -> interval != OvertimeInterval.ALL)
                .filter(interval -> {
                    OvertimeInterval.OvertimeValue ov = interval.getValue();
                    long intervalMinutes = ov.hours() * 60L + ov.minutes();
                    return intervalMinutes == totalMinutes;
                })
                .findFirst()
                .orElse(OvertimeInterval.H00_00);
    }

    private String formatMinutesToHHMM(long totalMinutes) {
        if (totalMinutes < 0) {
            return "0:00";
        }
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        return String.format("%d:%02d", hours, minutes);
    }

    private void updateHoursSummary() {
        updateDateSpans();

        long totalDurationMinutes = calculateWorkDurationMinutes();

        OvertimeInterval selectedOvertime = overtime.getValue();
        long actualOvertimeMinutes = 0;

        if (selectedOvertime != null) {
            if (selectedOvertime == OvertimeInterval.ALL) {
                actualOvertimeMinutes = totalDurationMinutes;
            } else {
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

        normalSpan.setText(getTranslation("timesheetForm.summary.normalWork", normalWorkFormatted));
        overtimeSpan.setText(getTranslation("timesheetForm.summary.overtime", overtimeFormatted));
        totalSpan.setText(getTranslation("timesheetForm.summary.totalTime", totalTimeFormatted));

        if (actualOvertimeMinutes > 0) {
            overtimeSpan.getStyle().set("color", "var(--lumo-error-text-color)");
            overtimeSpan.getStyle().set("font-weight", "bold");
        }

        totalSpan.getStyle().set("font-weight", "bold");
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

    @Override
    public void updateDependsOnDate(LocalDate localDate) {
        this.selectedDate = localDate;
        // Aktualizacja spanów i obliczonych dat na podstawie nowej selectedDate
        updateDateSpans();

        DateState state = DateState.fromLocalDate(localDate);

        switch (state) {
            case PAST -> {
                headerTextLabel.setText(getTranslation("timesheetForm.headerText.past"));
            }
            case TODAY -> {
                headerTextLabel.setText(getTranslation("timesheetForm.headerText"));
            }
            case FUTURE -> {
                headerTextLabel.setText(getTranslation("timesheetForm.headerText.future"));
            }
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

    private void configureValueFields() {
        var updateListener = (Runnable) () -> {
            updateDateSpans();

            long totalMinutes = calculateWorkDurationMinutes();
            filterOvertimeOptions(totalMinutes);
            updateHoursSummary();
        };

        from.addValueChangeListener(event -> {
            updateListener.run();
        });
        to.addValueChangeListener(event -> updateListener.run());

        overtime.addValueChangeListener(event -> {
            updateHoursSummary();
        });

        updateListener.run();
    }

    private VerticalLayout configuredHoursSummaryLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(false);

        layout.getStyle().set("border", "1px solid var(--lumo-contrast-10pct)");
        layout.getStyle().set("border-radius", "4px");
        layout.getStyle().set("padding", "8px");
        layout.getStyle().set("min-height", "3em");

        layout.add(normalSpan, overtimeSpan, totalSpan);

        return layout;
    }

    private HorizontalLayout createDateTimeLayout(TimePicker timePicker, Span dateSpan) {
        timePicker.setStep(Duration.ofMinutes(15));
        timePicker.setWidth("50%");

        dateSpan.setWidth("50%");
        dateSpan.getStyle().set("padding", "0.5em 0.5em 0.5em 0.5em");
        dateSpan.getStyle().set("border", "1px solid var(--lumo-contrast-30pct)");
        dateSpan.getStyle().set("border-radius", "var(--lumo-border-radius-m)");
        dateSpan.getStyle().set("line-height", "var(--lumo-line-height-m)");
        dateSpan.getStyle().set("align-self", "flex-end");

        var layout = new HorizontalLayout(timePicker, dateSpan);
        layout.setAlignItems(FlexComponent.Alignment.END);
        layout.setWidthFull();
        layout.setSpacing(true);
        return layout;
    }

    private void updateDateSpans() {
        if (selectedDate == null) {
            return;
        }

        Locale currentLocale = VaadinSession.getCurrent().getLocale();
        DateTimeFormatter fullDateFormatter = DateTimeFormatter.ofPattern("dd.MM EEEE", currentLocale);

        dateFromSpan.setText(selectedDate.format(fullDateFormatter));

        LocalTime timeFrom = from.getValue();
        LocalTime timeTo = to.getValue();

        if (timeFrom != null && timeTo != null && timeTo.isBefore(timeFrom)) {
            calculatedDateTo = selectedDate.plusDays(1);
        } else {
            calculatedDateTo = selectedDate;
        }

        dateToSpan.setText(calculatedDateTo.format(fullDateFormatter));
    }

    private long calculateWorkDurationMinutes() {
        LocalTime timeFrom = from.getValue();
        LocalTime timeTo = to.getValue();

        LocalDate dateTo = calculatedDateTo;

        if (timeFrom != null && timeTo != null && dateTo != null && selectedDate != null) {
            var dateTimeFrom = LocalDateTime.of(selectedDate, timeFrom);
            var dateTimeTo = LocalDateTime.of(dateTo, timeTo);

            if (dateTimeFrom.isBefore(dateTimeTo)) {
                Duration duration = Duration.between(dateTimeFrom, dateTimeTo);
                return duration.toMinutes();
            }
        }
        // It's required to return less than -1
        // to work properly with OvertimeInterval.ALL that has .value() equals -1
        return -2;
    }

    private void localize() {
        from.setLabel(getTranslation("timesheetForm.from"));
        to.setLabel(getTranslation("timesheetForm.to"));
    }

    private String getHelpText() {
        return getTranslation("timesheetForm.rules.title") + "\n\n"
                + getTranslation("timesheetForm.rules.1") + "\n"
                + getTranslation("timesheetForm.rules.2") + "\n"
                + getTranslation("timesheetForm.rules.3");
    }
}
