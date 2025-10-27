package pl.crewops.ui.component.navbarComponents;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import pl.crewops.ui.component.dialog.CompanyCreatorDialog;

public class CustomerRegistryButton extends Div {

    public CustomerRegistryButton() {
        var registry = new Button(getTranslation("customerRegistryButton.registry"));
        registry.addClickListener(e -> {
            new CompanyCreatorDialog();
        });
        add(registry);
    }
}
