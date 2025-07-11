package pl.crewops.view.component.notification.formNotification;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.view.component.form.EmployeeForm;

public class CompanyCreatorNotification extends Notification {

    public CompanyCreatorNotification(CoreAPI coreAPI) {
        addClassName("company-creator-notification");
        addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        setPosition(Notification.Position.MIDDLE);
        setDuration(0);

        var companyCreatorForm = new CompanyCreatorForm();

        add(companyCreatorForm);
        open();
    }

    private static class CompanyCreatorForm extends FormLayout {
        // company
        private final TextField companyName = new TextField();
        private final TextField companyEmail = new TextField();
        // address
        private final TextField postalCode = new TextField();
        private final TextField city = new TextField();
        private final TextField street = new TextField();
        private final TextField localNumber = new TextField();
        // employee
        private final EmployeeForm employeeForm = new EmployeeForm();

        public CompanyCreatorForm() {
            addClassName("company-creator-form");

            localize();

            employeeForm.setFormModeSave();
            add(companyName, companyEmail, postalCode, city, street, localNumber, employeeForm);
        }

        private void localize() {
            companyName.setLabel(getTranslation("companyCreatorForm.companyName"));
            companyEmail.setLabel(getTranslation("companyCreatorForm.companyEmail"));
            postalCode.setLabel(getTranslation("companyCreatorForm.postalCode"));
            city.setLabel(getTranslation("companyCreatorForm.city"));
            street.setLabel(getTranslation("companyCreatorForm.street"));
            localNumber.setLabel(getTranslation("companyCreatorForm.localNumber"));
        }
    }
}
