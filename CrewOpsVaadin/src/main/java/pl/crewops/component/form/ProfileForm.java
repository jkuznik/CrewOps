package pl.crewops.component.form;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import java.util.Optional;
import pl.crewops.component.dialog.credentialsDialog.UpdateCredentialsDialog;
import pl.crewops.component.notification.FailNotification;
import pl.crewops.component.notification.SuccessNotification;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.ProfileFormModel;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.model.dto.employee.UpdateEmployeeDTO;

public class ProfileForm extends FormLayout {
    private final CoreAPI coreAPI;
    private final ProfileFormModel profileFormModel;

    private final TextField firstName = new TextField();
    private final TextField lastName = new TextField();
    private final TextField phoneNumber = new TextField();
    private final Checkbox smsConfirmation = new Checkbox();
    private final TextField email = new TextField();

    private final Button updateProfile = new Button();
    private final Button credentialsModification = new Button();

    private final Binder<ProfileFormModel> binder = new Binder<>(ProfileFormModel.class);

    public ProfileForm(ProfileFormModel profileFormModel, CoreAPI coreAPI) {
        addClassName("profile-form");
        this.coreAPI = coreAPI;
        this.profileFormModel = profileFormModel;

        localize();

        configureBinder();

        var configuredForm = getConfiguredForm();

        add(configuredForm);
    }

    private void configureBinder() {
        binder.bindInstanceFields(this);

        binder.forField(email)
                .withValidator(
                        value -> value == null
                                || value.isEmpty()
                                || !new EmailValidator(getTranslation("profileForm.email.invalid"))
                                        .apply(value, null)
                                        .isError(),
                        getTranslation("profileForm.email.invalid"))
                .bind(ProfileFormModel::getEmail, ProfileFormModel::setEmail);

        binder.setBean(profileFormModel);
    }

    private VerticalLayout getConfiguredForm() {
        final var verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setSpacing(true);
        verticalLayout.setPadding(true);

        firstName.setEnabled(false);
        lastName.setEnabled(false);

        updateProfile.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        updateProfile.setWidth("140px");
        updateProfile.addClickListener(event -> {
            if (!binder.validate().hasErrors()) {
                update();
            }
        });

        var updatableDataContainer = updatableDataContainer();
        var updateCredentialsContainer = getUpdateCredentialsContainer();

        verticalLayout.add(firstName, lastName, updatableDataContainer, updateCredentialsContainer);

        return verticalLayout;
    }

    private Component updatableDataContainer() {
        var container = new VerticalLayout();
        container.setPadding(true);
        container.setSpacing(true);

        var disclaimer = new Paragraph(getTranslation("profileForm.phoneNumberDisclaimer"));
        disclaimer
                .getStyle()
                .set("font-size", "var(--lumo-font-size-s)")
                .set("color", "var(--lumo-secondary-text-color)")
                .set("margin", "0");

        container.add(disclaimer, phoneNumber, smsConfirmation, email, updateProfile);

        container.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.START);

        return container;
    }

    private VerticalLayout getUpdateCredentialsContainer() {
        var updateCredentialsContainer = new VerticalLayout();
        updateCredentialsContainer
                .getStyle()
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "8px")
                .set("padding", "1rem")
                .set("margin-top", "1rem");

        Span infoText = new Span(getTranslation("profileForm.infoText"));

        credentialsModification.addThemeVariants(ButtonVariant.LUMO_WARNING);
        credentialsModification.setWidth("140px");

        credentialsModification.addClickListener(event -> {
            new UpdateCredentialsDialog(profileFormModel, coreAPI).open();
        });

        updateCredentialsContainer.add(infoText, credentialsModification);
        return updateCredentialsContainer;
    }

    private void localize() {
        firstName.setLabel(getTranslation("employeeForm.firstName"));
        lastName.setLabel(getTranslation("employeeForm.lastName"));
        phoneNumber.setLabel(getTranslation("employeeForm.phoneNumber"));
        // todo i18n
        smsConfirmation.setLabel("SMS - this feature currently is in progress");
        email.setLabel(getTranslation("employeeForm.email"));

        updateProfile.setText(getTranslation("employeeForm.update"));
        credentialsModification.setText(getTranslation("employeeForm.update"));
    }

    private void update() {
        var updateEmployeeDTO = UpdateEmployeeDTO.builder()
                .employeeId(binder.getBean().getEmployeeId())
                .phoneNumber(phoneNumber.getValue())
                .email(email.getValue())
                .build();

        try {
            Optional<EmployeeDTO> employeeDTO = coreAPI.updateEmployee(updateEmployeeDTO);

            if (employeeDTO.isPresent()) {
                if (employeeDTO.get().phoneNumber() != null) {
                    binder.getBean().setPhoneNumber(employeeDTO.get().phoneNumber());
                }
                if (employeeDTO.get().email() != null) {
                    binder.getBean().setEmail(employeeDTO.get().email());
                }
                // todo i18n and customize
                new SuccessNotification("Employee updated successfully");
            } else {
                new FailNotification("Something went wrong...");
                phoneNumber.setValue(binder.getBean().getPhoneNumber());
                email.setValue(binder.getBean().getEmail());
            }
        } catch (NotAuthenticatedException e) {
            new FailNotification(e.getMessage()).open();
        }
    }
}
