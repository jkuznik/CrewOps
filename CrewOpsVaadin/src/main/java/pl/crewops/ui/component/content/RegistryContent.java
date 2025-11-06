package pl.crewops.ui.component.content;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
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
        individualColumn.addClassName("column");

        companyColumn.addClassName("column");

        configureColumn(registerIndividual, individualColumn, "registryContent.infoCompany");
        configureColumn(registerCompany, companyColumn, "registryContent.infoIndividual");

        configureButtons();
        companyCreatorPanel.setVisible(false);

        contentOverlay.add(companyColumn, individualColumn);
        splitLayout.add(contentOverlay);

        add(splitLayout, companyCreatorPanel);
        setFlexGrow(1, splitLayout);

        // Hover efekty (dodawane do splitLayout)
        configureHoverEvents();
    }

    private void configureColumn(Button button, VerticalLayout column, String headerKey) {
        H2 title = new H2(getTranslation(headerKey));
        button.setText(getTranslation("registryContent.registerButton"));

        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        button.addClassName("glass-button");

        // 🚨 KOREKTA ROZMIARU W JAVIE: Zwiększamy rozmiar czcionki i padding
        button.getStyle()
                .set("font-size", "1.75rem")
                .set("padding", "1.25rem 3rem"); // Zwiększamy padding dla rozmiaru XL

        column.setAlignItems(Alignment.CENTER);
        column.setJustifyContentMode(JustifyContentMode.CENTER);

        column.add(title, button);
    }

    private void configureHoverEvents() {
        companyColumn.getElement().addEventListener("mouseenter", e -> splitLayout.addClassName("hover-company"));
        companyColumn.getElement().addEventListener("mouseleave", e -> splitLayout.removeClassName("hover-company"));

        individualColumn.getElement().addEventListener("mouseenter", e -> splitLayout.addClassName("hover-individual"));
        individualColumn
                .getElement()
                .addEventListener("mouseleave", e -> splitLayout.removeClassName("hover-individual"));
    }

    private void configureButtons() {
        registerIndividual.addClickListener(event -> {
            companyCreatorPanel.setIndividualRegistrationMode();
            companyCreatorPanel.setVisible(true);
        });

        registerCompany.addClickListener(event -> {
            companyCreatorPanel.setCompanyRegistrationMode();
            companyCreatorPanel.setVisible(true);
        });
    }
}
