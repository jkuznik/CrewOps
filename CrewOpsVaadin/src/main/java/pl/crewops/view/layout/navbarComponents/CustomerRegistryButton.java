package pl.crewops.view.layout.navbarComponents;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import pl.crewops.component.dialog.CompanyCreatorDialog;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.util.RoleResolver;

public class CustomerRegistryButton extends Div {

    public CustomerRegistryButton(CoreAPI coreAPI, RoleResolver roleResolver) {
        var registry = new Button(getTranslation("customerRegistryButton.registry"));
        registry.addClickListener(e -> {
            new CompanyCreatorDialog(coreAPI, roleResolver);
        });
        add(registry);
    }
}
