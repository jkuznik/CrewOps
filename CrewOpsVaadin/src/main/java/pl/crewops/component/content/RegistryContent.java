package pl.crewops.component.content;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import pl.crewops.component.dialog.CompanyCreatorDialog;

public class RegistryContent extends VerticalLayout {

    private final Button register = new Button();

    public RegistryContent() {
        setWidthFull();
        setPadding(false);
        setSpacing(false);

        FlexLayout mainLayout = new FlexLayout();
        mainLayout.setWidthFull();
        mainLayout
                .getStyle()
                .set("gap", "20px")
                .set("align-items", "start")
                .set("padding", "20px")
                .set("overflow-x", "hidden")
                .set("box-sizing", "border-box")
                .set("flex-wrap", "wrap")
                .set("max-width", "100vw");

        VerticalLayout textLayout = new VerticalLayout();
        textLayout.setPadding(false);
        textLayout.setSpacing(true);
        textLayout.getStyle().set("flex", "1 1 300px").set("max-width", "100%").set("overflow-wrap", "break-word");

        H2 info = new H2(getTranslation("registryContent.info") + " dont registered yet? registy now");

        configureRegistryButton();
        mainLayout.add(info, register);

        add(mainLayout);
    }

    private void configureRegistryButton() {
        register.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        register.setText(getTranslation("registryContent.registerButton"));
        register.addClickListener(event -> {
            new CompanyCreatorDialog().open();
        });
    }
}
