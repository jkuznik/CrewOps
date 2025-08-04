package pl.crewops.view.component.navbarComponents;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.view.component.notification.CompanyCreatorNotification;

public class CustomerRegistryButton extends Div {
    public CustomerRegistryButton(CoreAPI coreAPI) {
        var registry = new Button(getTranslation("customerRegistryButton.registry"));
        registry.addClickListener(e -> {
            new CompanyCreatorNotification(coreAPI);
        });
        add(registry);
    }
}
