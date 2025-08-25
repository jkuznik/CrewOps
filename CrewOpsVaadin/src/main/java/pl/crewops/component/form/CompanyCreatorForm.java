package pl.crewops.component.form;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;
import pl.crewops.component.dialog.CompanyCreatorDialog;

public class CompanyCreatorForm extends FormLayout {
    // company
    private final TextField companyName = new TextField();
    private final TextField companyEmail = new TextField();
    private final TextField companyTaxId = new TextField();
    // address
    private final TextField postalCode = new TextField();
    private final TextField city = new TextField();
    private final TextField street = new TextField();
    private final TextField localNumber = new TextField();

    public CompanyCreatorForm() {
        addClassName("company-creator-form");

        var employeeForm = new EmployeeForm();
        localize();

        employeeForm.setFormModeSave();
        employeeForm.addSaveListener(event -> fireEvent(new SaveEvent(this, getCompanyInformationFromEvent(event))));
        employeeForm.addCloseListener(event -> fireEvent(new CloseEvent(this)));
        add(companyName, companyEmail, companyTaxId, postalCode, city, street, localNumber, employeeForm);
    }

    private void localize() {
        companyName.setLabel(getTranslation("companyCreatorForm.companyName"));
        companyEmail.setLabel(getTranslation("companyCreatorForm.companyEmail"));
        companyTaxId.setLabel(getTranslation("companyCreatorForm.companyTaxId"));
        postalCode.setLabel(getTranslation("companyCreatorForm.postalCode"));
        city.setLabel(getTranslation("companyCreatorForm.city"));
        street.setLabel(getTranslation("companyCreatorForm.street"));
        localNumber.setLabel(getTranslation("companyCreatorForm.localNumber"));
    }

    private CompanyCreatorDialog.CompanyInformation getCompanyInformationFromEvent(EmployeeForm.SaveEvent event) {
        return new CompanyCreatorDialog.CompanyInformation(
                companyName.getValue(),
                companyEmail.getValue(),
                companyTaxId.getValue(),
                postalCode.getValue(),
                city.getValue(),
                street.getValue(),
                localNumber.getValue(),
                event.getEmployee());
    }

    public abstract static class CompanyCreatorFormEvent extends ComponentEvent<CompanyCreatorForm> {
        public CompanyCreatorFormEvent(CompanyCreatorForm source) {
            super(source, false);
        }
    }

    public static class SaveEvent extends CompanyCreatorFormEvent {
        @Getter
        private final CompanyCreatorDialog.CompanyInformation companyInformation;

        public SaveEvent(CompanyCreatorForm source, CompanyCreatorDialog.CompanyInformation companyInformation) {
            super(source);
            this.companyInformation = companyInformation;
        }
    }

    public static class CloseEvent extends CompanyCreatorFormEvent {
        public CloseEvent(CompanyCreatorForm source) {
            super(source);
        }
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }
}
