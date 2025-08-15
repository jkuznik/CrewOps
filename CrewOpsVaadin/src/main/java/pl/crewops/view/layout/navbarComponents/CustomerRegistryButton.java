package pl.crewops.view.layout.navbarComponents;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import pl.crewops.component.dialog.CompanyCreatorDialog;
import pl.crewops.infrastructure.core.CoreAPI;

public class CustomerRegistryButton extends Div {

    public CustomerRegistryButton(CoreAPI coreAPI) {
        var registry = new Button(getTranslation("customerRegistryButton.registry"));
        registry.addClickListener(e -> {
            new CompanyCreatorDialog(coreAPI);
        });
        add(registry);
    }
}
