package pl.crewops.component.form.daily;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.shared.Tooltip;

public class AttendanceStatusForm extends FormLayout {

    private static final String STATUS_PRESENT = "Obecny";
    private static final String STATUS_VACATION = "Urlop";
    private static final String STATUS_SICK_LEAVE = "Zwolnienie";
    private static final String STATUS_OTHER = "Inne";
    private static final String STATUS_ABSENT = "Nieobecny";

    private final RadioButtonGroup<String> statusSelection = new RadioButtonGroup<>();
    private final Icon helpIcon = VaadinIcon.INFO_CIRCLE.create();

    public AttendanceStatusForm() {

        statusSelection.setItems(STATUS_PRESENT, STATUS_VACATION, STATUS_SICK_LEAVE, STATUS_OTHER, STATUS_ABSENT);
        statusSelection.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);

        var horizontalLayout = new HorizontalLayout();
        horizontalLayout.setPadding(true);
        horizontalLayout.setJustifyContentMode(HorizontalLayout.JustifyContentMode.END);

        helpIcon.setColor("var(--lumo-contrast-50pct)");
        helpIcon.getStyle().set("cursor", "pointer");

        Tooltip.forComponent(helpIcon).withText(getHelpText()).withPosition(Tooltip.TooltipPosition.BOTTOM_END);

        horizontalLayout.add(statusSelection, helpIcon);

        add(horizontalLayout);
    }

    private String getHelpText() {
        return "1. **Obecny :** Jesteś / byłeś w pracy danego dnia.\n"
                + "2. **Urlop :** Zaplanowany urlop wypoczynkowy / okolicznościowy / bezpłatny  lub dzień wolny.\n"
                + "3. **Zwolnienie :** Zwolnienie lekarskie, opieka nad dzieckiem/inną osobą.\n"
                + "4. **Inne :** Akceptowana podróż służbowa, szkolenie, dyżur, medycyna pracy, itp.\n"
                + "5. **Nieobecny :** Nieusprawiedliwiona lub nieautoryzowana nieobecność.";
    }
}
