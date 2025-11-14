package pl.crewops.ui.component.custom;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * Niestandardowy, klikalny komponent (Div) działający jako przycisk "Dodaj nowy".
 * Posiada przerywane obramowanie, duży znak plus oraz animacje na najechanie.
 * Div domyślnie implementuje ClickNotifier, dlatego nie jest on powtarzany w deklaracji klasy.
 */
@CssImport("./styles/component/add-button-panel.css") // Przywracamy import CSS, aby style działały
public class AddButtonPanel extends Div {

    public AddButtonPanel() {
        setSizeFull();

        // Dodanie dużego plusa do środka
        Icon plusIcon = VaadinIcon.PLUS.create();
        // Zmieniono na XXXLARGE, aby pasowało do standardów Lumo
        plusIcon.addClassNames(LumoUtility.TextColor.PRIMARY, LumoUtility.FontSize.XXXLARGE);

        // Ułożenie zawartości (plus oraz opcjonalny opis)
        Div content = new Div(plusIcon);
        content.addClassNames(
                LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN,
                LumoUtility.AlignItems.CENTER,
                LumoUtility.JustifyContent.CENTER,
                LumoUtility.Height.FULL);

        addClassNames(LumoUtility.TextAlignment.CENTER, LumoUtility.Padding.MEDIUM);
        add(content);

        // Aplikacja niestandardowych stylów z panel.css
        addClassName("custom-add-panel");

        // --- Ustawienia dla wyglądu klikalnego przycisku (zaciągnięte z SchedulePanel) ---

        getStyle().set("cursor", "pointer");
        getStyle().set("transition", "0.2s transform, 0.2s box-shadow");

        // Dodanie interakcji na najechanie/kliknięcie dla lepszej informacji zwrotnej
        getElement().addEventListener("mouseover", e -> getStyle().set("transform", "translateY(-2px)"));
        getElement().addEventListener("mouseout", e -> getStyle().set("transform", "translateY(0)"));
        getElement().addEventListener("mousedown", e -> getStyle().set("transform", "scale(0.98)"));
        getElement().addEventListener("mouseup", e -> getStyle().set("transform", "translateY(-2px)"));
    }

    /**
     * Publiczna metoda do ustawiania rozmiaru, zwraca siebie do łańcuchowego wywoływania metod.
     */
    public AddButtonPanel setSize(String width, String height) {
        setWidth(width);
        setHeight(height);
        return this;
    }
}
