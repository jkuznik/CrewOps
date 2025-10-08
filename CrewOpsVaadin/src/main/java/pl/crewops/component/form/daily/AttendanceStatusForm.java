package pl.crewops.component.form.daily;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.shared.Tooltip;

public class AttendanceStatusForm extends FormLayout {

    private static final String STATUS_PRESENT = "Obecny w pracy";
    private static final String STATUS_SICK_LEAVE = "Zwolnienie (L4 / Opieka)";
    private static final String STATUS_VACATION = "Urlop wypoczynkowy / Wolne";
    private static final String STATUS_OTHER = "Inne (Szkolenie, Delegacja)";
    private static final String STATUS_ABSENT = "Nieobecny (nieusprawiedliwiona)";

    private final Span headerText = new Span("Status obecności");
    private final RadioButtonGroup<String> statusSelection = new RadioButtonGroup<>();
    private final Icon helpIcon = VaadinIcon.INFO_CIRCLE.create();
    private final Button save = new Button("Zapisz");

    public AttendanceStatusForm() {

        headerText.getStyle().set("font-weight", "bold");
        headerText.getStyle().set("font-size", "1.1em");

        var verticalLayout = new VerticalLayout();
        verticalLayout.getStyle().set("border", "1px solid #ccc");
        verticalLayout.getStyle().set("border-radius", "4px");
        verticalLayout.getStyle().set("padding", "10px");

        statusSelection.setLabel("");
        statusSelection.setItems(STATUS_PRESENT, STATUS_SICK_LEAVE, STATUS_VACATION, STATUS_OTHER, STATUS_ABSENT);
        statusSelection.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);

        var spacer = new Div();
        spacer.setHeight("300px");

        var horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        horizontalLayout.setJustifyContentMode(HorizontalLayout.JustifyContentMode.END);

        helpIcon.setColor("var(--lumo-contrast-50pct)");
        helpIcon.getStyle().set("cursor", "pointer");

        Tooltip.forComponent(helpIcon).withText(getHelpText()).withPosition(Tooltip.TooltipPosition.BOTTOM_END);

        save.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        horizontalLayout.add(helpIcon, save);

        verticalLayout.setMaxHeight("400px");
        verticalLayout.setMaxWidth("300px");
        verticalLayout.add(headerText, statusSelection, spacer, horizontalLayout);

        add(verticalLayout);
    }

    private String getHelpText() {
        return "Instrukcje dotyczące zgłaszania dziennego statusu:\n\n"
                + "1. **Obecny w pracy:** Jesteś w pracy i wykonujesz obowiązki.\n"
                + "2. **Zwolnienie (L4 / Opieka):** Zwolnienie lekarskie, opieka nad dzieckiem/inną osobą.\n"
                + "3. **Urlop wypoczynkowy / Wolne:** Zaplanowany urlop wypoczynkowy lub dzień wolny.\n"
                + "4. **Inne:** Akceptowana podróż służbowa, szkolenie, dyżur, itp.\n"
                + "5. **Nieobecny:** Nieusprawiedliwiona lub nieautoryzowana nieobecność.";
    }
}
