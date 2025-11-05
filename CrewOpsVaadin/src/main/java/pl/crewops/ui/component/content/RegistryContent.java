package pl.crewops.ui.component.content;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import pl.crewops.ui.component.dialog.CompanyCreatorPanel;

@CssImport("./styles/registry-content.css")
public class RegistryContent extends VerticalLayout {

    private final CompanyCreatorPanel companyCreatorPanel = new CompanyCreatorPanel();

    private final Div splitLayout = new Div();
    private final HorizontalLayout contentOverlay = new HorizontalLayout();

    private final VerticalLayout companyColumn = new VerticalLayout();
    private final VerticalLayout individualColumn = new VerticalLayout();

    private final Button registerCompany = new Button();
    private final Button registerIndividual = new Button();

    public RegistryContent() {
        addClassName("registry-content-main");
        setPadding(false);
        setSpacing(false);
        setSizeFull();

        // 🔹 Kluczowe linie – wymuszają pełny ekran w kontekście Vaadin layoutów
        getStyle().set("flex-grow", "1");
        getStyle().set("min-height", "100vh");

        // 🔹 Powiększamy obszar renderowania (żeby tło nie było przycięte)
        splitLayout.setWidth("110vw");
        splitLayout.setHeight("110vh");
        splitLayout.getStyle().set("position", "relative").set("left", "-5vw").set("top", "-5vh");

        // Główne tło i efekt
        splitLayout.addClassName("split-layout");

        // Warstwa z treścią
        contentOverlay.addClassName("content-overlay");
        contentOverlay.setWidthFull();
        contentOverlay.setHeightFull();
        contentOverlay.setJustifyContentMode(JustifyContentMode.BETWEEN);

        // Kolumny
        companyColumn.addClassName("column");
        individualColumn.addClassName("column");

        configureColumn(
                registerCompany, companyColumn, "registryContent.infoIndividual", "registryContent.registerButton1");
        configureColumn(
                registerIndividual, individualColumn, "registryContent.infoCompany", "registryContent.registerButton");

        contentOverlay.add(companyColumn, individualColumn);
        splitLayout.add(contentOverlay);

        add(splitLayout);
        setFlexGrow(1, splitLayout);

        // Hover efekty (dodawane do splitLayout)
        configureHoverEvents();
    }

    private void configureColumn(Button button, VerticalLayout column, String headerKey, String buttonKey) {
        H2 title = new H2(getTranslation(headerKey));
        Hr hr = new Hr();
        button.setText(getTranslation(buttonKey));
        button.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        button.getStyle().set("font-size", "1.5rem").set("padding", "1rem 2rem");

        column.setAlignItems(Alignment.CENTER);
        column.setJustifyContentMode(JustifyContentMode.CENTER);
        column.add(title, hr, button);
    }

    private void configureHoverEvents() {
        companyColumn.getElement().addEventListener("mouseenter", e -> splitLayout.addClassName("hover-company"));
        companyColumn.getElement().addEventListener("mouseleave", e -> splitLayout.removeClassName("hover-company"));

        individualColumn.getElement().addEventListener("mouseenter", e -> splitLayout.addClassName("hover-individual"));
        individualColumn
                .getElement()
                .addEventListener("mouseleave", e -> splitLayout.removeClassName("hover-individual"));
    }
}
