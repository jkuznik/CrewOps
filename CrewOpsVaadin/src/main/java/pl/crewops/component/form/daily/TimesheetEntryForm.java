package pl.crewops.component.form.daily;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon; // Dodano
import com.vaadin.flow.component.icon.VaadinIcon; // Dodano
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.timepicker.TimePicker;

public class TimesheetEntryForm extends FormLayout {

    // todo: i18n
    private final Span text = new Span("Czas pracy");
    private final TimePicker from = new TimePicker("Od");
    private final TimePicker to = new TimePicker("Do");
    private final NumberField overtime = new NumberField("Nadgodziny");

    // Dodajemy ikonę do wyświetlania pomocy
    private final Icon helpIcon = VaadinIcon.INFO_CIRCLE.create();

    private final Button update = new Button("Update");

    public TimesheetEntryForm() {

        setMaxWidth("450px");
        getStyle().set("margin", "0 auto");

        var verticalLayout = new VerticalLayout();
        verticalLayout.setWidthFull();
        verticalLayout.getStyle().set("border", "1px solid #ccc");
        verticalLayout.getStyle().set("border-radius", "4px");
        verticalLayout.getStyle().set("padding", "10px");

        verticalLayout.setSpacing(true);
        verticalLayout.setPadding(false);

        // Konfiguracja ikony pomocy (helpIcon)
        // 1. Styl: Ustawienie koloru na dyskretny (Lumo contrast)
        helpIcon.setColor("var(--lumo-contrast-50pct)");
        helpIcon.getStyle().set("cursor", "pointer"); // Zmieniamy kursor, by sugerował interakcję

        // 2. Dodanie Tooltipa (kluczowy element)
        Tooltip.forComponent(helpIcon)
                .withText(getHelpText()) // Wyświetlana treść
                .withPosition(Tooltip.TooltipPosition.BOTTOM_END); // Pozycja wyświetlania (opcjonalnie)

        var horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        horizontalLayout.setSpacing(true);
        horizontalLayout.setPadding(false);

        horizontalLayout.setJustifyContentMode(HorizontalLayout.JustifyContentMode.END);

        update.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        horizontalLayout.add(helpIcon, update);

        verticalLayout.setAlignItems(VerticalLayout.Alignment.STRETCH);

        verticalLayout.add(text, from, to, overtime, horizontalLayout);
        add(verticalLayout);
    }

    /**
     * Generuje treść dla Tooltipa. Tę treść później zinternacjonalizujemy (i18n).
     */
    private String getHelpText() {
        // W przyszłości użyjesz getTranslation("timesheet.rules")
        return "Zasady wprowadzania czasu:\n\n" + "1. Czas pracy (Od/Do) to cały przepracowany czas.\n"
                + "2. Nadgodziny to Ilość godzin (np. 2.5) zawarta w zadeklarowanym czasie Od/Do.\n"
                + "3. Jeśli cały czas Od/Do był nadgodzinami, Nadgodziny muszą być równe zadeklarowanemu czasowi.";
    }
}
