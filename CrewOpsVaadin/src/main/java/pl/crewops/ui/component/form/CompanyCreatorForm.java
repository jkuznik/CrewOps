package pl.crewops.ui.component.form;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import java.util.Optional;
import pl.crewops.ui.component.dialog.CompanyCreatorPanel;

public class CompanyCreatorForm extends VerticalLayout {
    // company
    private final TextField companyName = new TextField();
    private final TextField companyEmail = new TextField();
    private final TextField companyTaxId = new TextField();
    // address
    private final TextField postalCode = new TextField();
    private final TextField city = new TextField();
    private final TextField street = new TextField();
    private final TextField localNumber = new TextField();

    private final Binder<CompanyCreatorPanel.CompanyInformation> binder =
            new Binder<>(CompanyCreatorPanel.CompanyInformation.class);

    public CompanyCreatorForm() {
        addClassName("company-creator-form");
        localize();

        configureBinder();

        var companyForm = new FormLayout();
        companyForm.add(companyName, companyEmail, companyTaxId, postalCode, localNumber, city, street);

        add(companyForm);
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

    public Optional<CompanyCreatorPanel.CompanyInformation> getCompanyInformation() {
        if (!binder.validate().hasErrors()) {
            return Optional.of(CompanyCreatorPanel.CompanyInformation.builder()
                    .companyName(companyName.getValue())
                    .companyEmail(companyEmail.getValue())
                    .companyTaxId(companyTaxId.getValue())
                    .postalCode(postalCode.getValue())
                    .city(city.getValue())
                    .street(street.getValue())
                    .localNumber(localNumber.getValue())
                    .build());
        } else {
            return Optional.empty();
        }
    }
}
