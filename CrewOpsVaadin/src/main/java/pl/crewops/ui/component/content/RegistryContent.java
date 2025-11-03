package pl.crewops.ui.component.content;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import pl.crewops.ui.component.dialog.CompanyCreatorPanel;

public class RegistryContent extends VerticalLayout {

    private final CompanyCreatorPanel companyCreatorPanel = new CompanyCreatorPanel();

    private final Button register = new Button();

    public RegistryContent() {
        setWidthFull();
        setPadding(false);
        setSpacing(false);

        VerticalLayout contentLayout = new VerticalLayout();
        contentLayout.setWidthFull();
        contentLayout.setPadding(false);
        contentLayout.setSpacing(true);
        contentLayout.setAlignItems(Alignment.CENTER);

        H2 info = new H2(getTranslation("registryContent.info"));

        configureRegistryButton();

        contentLayout.add(info, new Hr(), register, companyCreatorPanel);
        companyCreatorPanel.setVisible(false);
        companyCreatorPanel.setSizeUndefined();

        add(contentLayout);
    }

    private void configureRegistryButton() {
        register.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        register.setText(getTranslation("registryContent.registerButton"));
        register.getStyle().set("font-size", "1.5rem").set("padding", "1rem 2rem");

        register.addClickListener(event -> companyCreatorPanel.setRegistrationFormVisibleTrue());
    }
}
