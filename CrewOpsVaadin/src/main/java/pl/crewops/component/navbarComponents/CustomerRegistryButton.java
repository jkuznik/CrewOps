package pl.crewops.component.navbarComponents;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import pl.crewops.component.dialog.CompanyCreatorDialog;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.util.SpringContextBridge;

public class CustomerRegistryButton extends Div {

    public CustomerRegistryButton() {
        CoreAPI coreAPI = SpringContextBridge.getBean(CoreAPI.class);

        var registry = new Button(getTranslation("customerRegistryButton.registry"));
        registry.addClickListener(e -> {
            new CompanyCreatorDialog(coreAPI);
        });
        add(registry);
    }
}
