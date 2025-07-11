package pl.crewops.view.component.navbarComponents;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.view.component.notification.formNotification.CompanyCreatorNotification;

public class CompanyCreator extends Div {
    // TODO: i18n
    public CompanyCreator(CoreAPI coreAPI) {
        var createButton = new Button("Create Company");
        createButton.addClickListener(e -> {
            CompanyCreatorNotification companyCreatorNotification = new CompanyCreatorNotification(coreAPI);
        });

        add(createButton);
    }
}
