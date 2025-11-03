package pl.crewops.ui.component.form;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;
import pl.crewops.ui.component.dialog.CompanyCreatorPanel;

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
    // binder
    private final Binder<CompanyCreatorPanel.CompanyInformation> binder =
            new Binder<>(CompanyCreatorPanel.CompanyInformation.class);

    public CompanyCreatorForm() {
        addClassName("company-creator-form");

        var employeeForm = new EmployeeForm();
        localize();

        configureBinder();

        employeeForm.setFormModeSave();
        employeeForm.addSaveListener(event -> fireEvent(new SaveEvent(this, getCompanyInformationFromEvent(event))));
        employeeForm.addCloseListener(event -> fireEvent(new CloseEvent(this)));
        add(companyName, companyEmail, companyTaxId, postalCode, city, street, localNumber, employeeForm);
    }

    public boolean validate() {
        return binder.isValid();
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

    private void configureBinder() {
        binder.forField(companyName)
                .asRequired(getTranslation("companyCreatorForm.validation.companyName"))
                .withValidator(
                        name -> name != null && name.length() >= 1 && name.length() <= 63,
                        getTranslation("companyCreatorForm.companyName.size", 1, 63))
                .bind(
                        CompanyCreatorPanel.CompanyInformation::getCompanyName,
                        CompanyCreatorPanel.CompanyInformation::setCompanyName);

        binder.forField(companyEmail)
                .asRequired(getTranslation("companyCreatorForm.validation.companyEmail"))
                .withValidator(
                        email -> email != null && email.contains("@"),
                        getTranslation("companyCreatorForm.companyEmail.invalid"))
                .bind(
                        CompanyCreatorPanel.CompanyInformation::getCompanyEmail,
                        CompanyCreatorPanel.CompanyInformation::setCompanyEmail);

        binder.forField(companyTaxId)
                .asRequired(getTranslation("companyCreatorForm.validation.companyTaxId"))
                .bind(
                        CompanyCreatorPanel.CompanyInformation::getCompanyTaxId,
                        CompanyCreatorPanel.CompanyInformation::setCompanyTaxId);

        binder.forField(postalCode)
                .asRequired(getTranslation("companyCreatorForm.validation.postalCode"))
                .bind(
                        CompanyCreatorPanel.CompanyInformation::getPostalCode,
                        CompanyCreatorPanel.CompanyInformation::setPostalCode);

        binder.forField(city)
                .asRequired(getTranslation("companyCreatorForm.validation.city"))
                .bind(CompanyCreatorPanel.CompanyInformation::getCity, CompanyCreatorPanel.CompanyInformation::setCity);

        binder.forField(street)
                .asRequired(getTranslation("companyCreatorForm.validation.street"))
                .bind(
                        CompanyCreatorPanel.CompanyInformation::getStreet,
                        CompanyCreatorPanel.CompanyInformation::setStreet);

        binder.forField(localNumber)
                .asRequired(getTranslation("companyCreatorForm.validation.localNumber"))
                .bind(
                        CompanyCreatorPanel.CompanyInformation::getLocalNumber,
                        CompanyCreatorPanel.CompanyInformation::setLocalNumber);

        binder.validate();
    }

    private CompanyCreatorPanel.CompanyInformation getCompanyInformationFromEvent(EmployeeForm.SaveEvent event) {
        return CompanyCreatorPanel.CompanyInformation.builder()
                .companyName(companyName.getValue())
                .companyEmail(companyEmail.getValue())
                .companyTaxId(companyTaxId.getValue())
                .postalCode(postalCode.getValue())
                .city(city.getValue())
                .street(street.getValue())
                .localNumber(localNumber.getValue())
                .initialEmployeeInfo(event.getEmployee())
                .build();
    }

    public abstract static class CompanyCreatorFormEvent extends ComponentEvent<CompanyCreatorForm> {
        public CompanyCreatorFormEvent(CompanyCreatorForm source) {
            super(source, false);
        }
    }

    public static class SaveEvent extends CompanyCreatorFormEvent {
        @Getter
        private final CompanyCreatorPanel.CompanyInformation companyInformation;

        public SaveEvent(CompanyCreatorForm source, CompanyCreatorPanel.CompanyInformation companyInformation) {
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
