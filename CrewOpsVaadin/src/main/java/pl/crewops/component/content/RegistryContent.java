package pl.crewops.component.content;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import pl.crewops.component.dialog.CompanyCreatorDialog;

public class RegistryContent extends VerticalLayout {

    private final Button register = new Button();

    public RegistryContent() {
        setWidthFull();
        setPadding(false);
        setSpacing(false);

        VerticalLayout contentLayout = new VerticalLayout();
        contentLayout.setWidthFull();
        contentLayout.setPadding(false);
        contentLayout.setSpacing(true);
        contentLayout.setAlignItems(Alignment.CENTER); // center everything horizontally

        H2 info = new H2(getTranslation("registryContent.info"));
        contentLayout.add(info);

        configureRegistryButton();
        contentLayout.add(register);

        // Customize button size
        register.getStyle()
                .set("font-size", "1.5rem") // bigger text
                .set("padding", "1rem 2rem"); // bigger height & width

        add(contentLayout);
    }

    private void configureRegistryButton() {
        register.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        register.setText(getTranslation("registryContent.registerButton"));
        register.addClickListener(event -> new CompanyCreatorDialog().open());
    }
}
