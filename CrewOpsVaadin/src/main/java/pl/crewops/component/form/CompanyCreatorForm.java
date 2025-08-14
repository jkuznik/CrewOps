package pl.crewops.component.form;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import java.util.function.Consumer;
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

    private Consumer<CompanyCreatorDialog.CompanyInformation> saveButtonListener;
    private Consumer<Void> closeButtonListener;

    public CompanyCreatorForm() {
        addClassName("company-creator-form");

        // employee
        var employeeForm = new EmployeeForm();
        localize();

        employeeForm.setFormModeSave();
        employeeForm.addSaveListener(this::saveEmployee);
        employeeForm.addCloseListener(this::closeButton);
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

    private void saveEmployee(EmployeeForm.SaveEvent event) {
        if (saveButtonListener != null) {

            saveButtonListener.accept(new CompanyCreatorDialog.CompanyInformation(
                    companyName.getValue(),
                    companyEmail.getValue(),
                    companyTaxId.getValue(),
                    postalCode.getValue(),
                    city.getValue(),
                    street.getValue(),
                    localNumber.getValue(),
                    event.getEmployee()));
        }
    }

    private void closeButton(EmployeeForm.CloseEvent event) {
        if (closeButtonListener != null) {
            closeButtonListener.accept(null);
        }
    }

    public void addCloseButtonListener(Consumer<Void> closeButtonListener) {
        this.closeButtonListener = closeButtonListener;
    }

    public void addSaveButtonListener(Consumer<CompanyCreatorDialog.CompanyInformation> listener) {
        this.saveButtonListener = listener;
    }
}
