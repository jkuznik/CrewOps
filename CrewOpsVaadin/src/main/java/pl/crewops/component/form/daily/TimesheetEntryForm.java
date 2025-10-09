package pl.crewops.component.form.daily;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.timepicker.TimePicker;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime; // Dodano import dla obliczeń
import java.time.format.DateTimeFormatter;
import java.util.List;
import pl.crewops.enums.DateState; // Upewnij się, że ten import jest poprawny
import pl.crewops.view.DailyView;

public class TimesheetEntryForm extends FormLayout {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM (EEE)");

    // todo: i18n
    private final Span headerTextLabel = new Span("Czas Pracy");
    private final Icon helpIcon = VaadinIcon.INFO_CIRCLE.create();

    private final TimePicker from = new TimePicker("Od");
    private final TimePicker to = new TimePicker("Do");
    private final Select<LocalDate> dateFromSelect = new Select<>();
    private final Select<LocalDate> dateToSelect = new Select<>();

    private final NumberField overtime = new NumberField("Nadgodziny");

    private final VerticalLayout hoursSummary = createHoursSummaryLayout();

    private final Button confirmPresence = createPresenceButton();

    private LocalDate selectedDate = LocalDate.now();

    public TimesheetEntryForm() {

        var fromLayout = createDateTimeLayout(from, dateFromSelect);
        var toLayout = createDateTimeLayout(to, dateToSelect);

        var overtimeLayout = createOvertimeLayout(overtime, hoursSummary);

        configureValueFields();

        confirmPresence.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        var spacer = new Div();
        spacer.setHeight("200px");

        var mainContainer = getConfiguredMainContainer();

        mainContainer.add(configuredHeaderLayout(), fromLayout, toLayout, overtimeLayout, spacer, confirmPresence);

        add(mainContainer);
    }

    private VerticalLayout createHoursSummaryLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(false);

        layout.getStyle().set("border", "1px solid var(--lumo-contrast-10pct)");
        layout.getStyle().set("border-radius", "4px");
        layout.getStyle().set("padding", "8px");
        layout.getStyle().set("min-height", "3em");

        layout.add(new Span("Przepracowane: -"), new Span("Nadgodziny: -"));

        return layout;
    }

    private void updateHoursSummary() {
        LocalTime timeFrom = from.getValue();
        LocalTime timeTo = to.getValue();
        LocalDate dateTo = dateToSelect.getValue();
        Double overtimeValue = overtime.getValue();

        double totalHours = 0.0;
        if (timeFrom != null && timeTo != null && dateTo != null && selectedDate != null) {
            // Zakładamy, że data startu jest zawsze selectedDate
            java.time.LocalDateTime dateTimeFrom = java.time.LocalDateTime.of(selectedDate, timeFrom);
            java.time.LocalDateTime dateTimeTo = java.time.LocalDateTime.of(dateTo, timeTo);

            if (dateTimeFrom.isBefore(dateTimeTo)) {
                Duration duration = Duration.between(dateTimeFrom, dateTimeTo);
                totalHours = duration.toMinutes() / 60.0;
            }
        }

        // 2. Pobranie nadgodzin
        double actualOvertime = overtimeValue != null ? overtimeValue : 0.0;

        // 3. Aktualizacja komunikatów
        hoursSummary.removeAll();
        // todo: i18n
        Span totalSpan = new Span(String.format("Przepracowane: %.2f h", totalHours));
        Span overtimeSpan = new Span(String.format("Nadgodziny: %.2f h", actualOvertime));

        if (actualOvertime > 0) {
            overtimeSpan.getStyle().set("color", "var(--lumo-error-text-color)");
            overtimeSpan.getStyle().set("font-weight", "bold");
        }

        hoursSummary.add(totalSpan, overtimeSpan);
    }

    private Button createPresenceButton() {
        // todo: i18n
        Button button = new Button("Potwierdź Obecność");
        button.setIcon(new Icon(VaadinIcon.CHECK_CIRCLE));
        return button;
    }

    // Układ dla Nadgodzin i Podsumowania
    private HorizontalLayout createOvertimeLayout(NumberField overtimeField, VerticalLayout summaryLayout) {
        var layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setSpacing(true);
        layout.setAlignItems(FlexComponent.Alignment.END);

        overtimeField.setWidth("50%");
        summaryLayout.setWidth("50%");

        layout.add(overtimeField, summaryLayout);
        return layout;
    }

    public void updateDependsOnDate(LocalDate localDate) {
        this.selectedDate = localDate;
        updateDateFieldsOptions();

        DateState state = DateState.fromLocalDate(localDate);

        switch (state) {
            case PAST -> {
                headerTextLabel.setText("Przepracowany Czas Pracy");
                confirmPresence.setVisible(false);
            }
            case TODAY -> {
                headerTextLabel.setText("Czas Pracy");
                confirmPresence.setVisible(true);
            }
            case FUTURE -> {
                headerTextLabel.setText("Planowany Czas Pracy");
                confirmPresence.setVisible(false);
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

        confirmPresence.setVisible(false);
    }

    private static VerticalLayout getConfiguredMainContainer() {
        var mainContainer = new VerticalLayout();
        mainContainer.getStyle().set("border", "1px solid #ccc");
        mainContainer.getStyle().set("border-radius", "4px");
        mainContainer.getStyle().set("padding", "10px");
        mainContainer.setMaxHeight("400px");
        mainContainer.setMaxWidth(DailyView.FORMS_WIDTH);
        return mainContainer;
    }

    private void configureValueFields() {

        from.addValueChangeListener(event -> {
            updateHoursSummary();
            if (validateWorkTime()) {
                confirmPresence.setVisible(true);
            }
        });

        to.addValueChangeListener(event -> {
            updateHoursSummary();
            if (validateWorkTime()) {
                confirmPresence.setVisible(true);
            }
        });

        dateToSelect.addValueChangeListener(event -> {
            updateHoursSummary();
            if (validateWorkTime()) {
                confirmPresence.setVisible(true);
            }
        });

        overtime.addValueChangeListener(event -> {
            updateHoursSummary();
            confirmPresence.setVisible(true);
        });

        updateDateFieldsOptions();
    }

    private HorizontalLayout configuredHeaderLayout() {
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

    private boolean validateWorkTime() {
        return true;
    }

    private String getHelpText() {
        return "Zasady wprowadzania czasu:\n\n" + "1. Czas pracy (Od/Do) to cały przepracowany czas.\n"
                + "2. Nadgodziny to Ilość godzin (np. 2.5) zawarta w zadeklarowanym czasie Od/Do.\n"
                + "3. Jeśli cały czas Od/Do był nadgodzinami, Nadgodziny muszą być równe zadeklarowanemu czasowi.";
    }

    public LocalDate getDateFromValue() {
        return dateFromSelect.getValue();
    }

    public LocalDate getDateToValue() {
        return dateToSelect.getValue();
    }

    // Publiczny getter dla przycisku, aby można było dodać listener z widoku
    public Button getConfirmPresenceButton() {
        return confirmPresence;
    }
}
