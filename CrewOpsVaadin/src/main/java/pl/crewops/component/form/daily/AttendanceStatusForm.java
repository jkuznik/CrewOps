package pl.crewops.component.form.daily;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.shared.Tooltip;
import pl.crewops.enums.DailyAttendanceStatus;

public class AttendanceStatusForm extends FormLayout {

    private final RadioButtonGroup<DailyAttendanceStatus> statusSelection = new RadioButtonGroup<>();
    private final Icon helpIcon = VaadinIcon.INFO_CIRCLE.create();

    public AttendanceStatusForm() {

        configureStatusSelection();

        var horizontalLayout = new HorizontalLayout();
        horizontalLayout.setPadding(true);
        horizontalLayout.setJustifyContentMode(HorizontalLayout.JustifyContentMode.END);

        helpIcon.setColor("var(--lumo-contrast-50pct)");
        helpIcon.getStyle().set("cursor", "pointer");

        Tooltip.forComponent(helpIcon).withText(getHelpText()).withPosition(Tooltip.TooltipPosition.BOTTOM_END);

        horizontalLayout.add(statusSelection, helpIcon);

        add(horizontalLayout);
    }

    private void configureStatusSelection() {
        statusSelection.setItems(DailyAttendanceStatus.values());
        statusSelection.setEnabled(false);
        statusSelection.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);

        // todo : i18n
        statusSelection.setItemLabelGenerator(item -> {
            switch (item) {
                case PRESENT -> {
                    return getTranslation("Obecny");
                }
                case VACATION -> {
                    return getTranslation("Urlop");
                }
                case SICK_LEAVE -> {
                    return getTranslation("Zwolnienie");
                }
                case OTHER -> {
                    return getTranslation("Inne");
                }
                case ABSENT -> {
                    return getTranslation("Nieobecny");
                }
                default -> {
                    return "";
                }
            }
        });
    }

    private String getHelpText() {
        return "1. **Obecny :** Jesteś / byłeś w pracy danego dnia.\n"
                + "2. **Urlop :** Zaplanowany urlop wypoczynkowy / okolicznościowy / bezpłatny  lub dzień wolny.\n"
                + "3. **Zwolnienie :** Zwolnienie lekarskie, opieka nad dzieckiem/inną osobą.\n"
                + "4. **Inne :** Akceptowana podróż służbowa, szkolenie, dyżur, medycyna pracy, itp.\n"
                + "5. **Nieobecny :** Nieusprawiedliwiona lub nieautoryzowana nieobecność.";
    }
}
