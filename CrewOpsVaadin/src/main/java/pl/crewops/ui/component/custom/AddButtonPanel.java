package pl.crewops.ui.component.custom;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
// KLUCZOWA ZMIANA: Import VerticalLayout

/**
 * Niestandardowy, klikalny komponent (Div) działający jako przycisk "Dodaj nowy".
 * Posiada przerywane obramowanie, duży znak plus (jako tło CSS) oraz animacje na najechanie.
 * Div domyślnie implementuje ClickNotifier, dlatego nie jest on powtarzany w deklaracji klasy.
 */
@CssImport("./styles/component/add-button-panel.css")
public class AddButtonPanel extends Div {

    public AddButtonPanel() {
        setSizeFull();

        addClassName("custom-add-panel");

        getStyle().set("cursor", "pointer");
        getStyle().set("transition", "0.2s transform, 0.2s box-shadow");

        // Dodanie interakcji na najechanie/kliknięcie dla lepszej informacji zwrotnej
        getElement().addEventListener("mouseover", e -> getStyle().set("transform", "translateY(-2px)"));
        getElement().addEventListener("mouseout", e -> getStyle().set("transform", "translateY(0)"));
        getElement().addEventListener("mousedown", e -> getStyle().set("transform", "scale(0.98)"));
        getElement().addEventListener("mouseup", e -> getStyle().set("transform", "translateY(-2px)"));
    }
}
