package pl.crewops.component.form;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
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
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import pl.crewops.component.dialog.credentialsDialog.UpdatePasswordDialog;
import pl.crewops.component.dialog.credentialsDialog.UpdateUsernameDialog;
import pl.crewops.component.notification.FailNotification;
import pl.crewops.component.notification.SuccessNotification;
import pl.crewops.enums.AuthUserOptions;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.ProfileFormModel;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.model.dto.employee.UpdateEmployeeDTO;
import pl.crewops.model.dto.option.AuthUserOptionDTO;
import pl.crewops.view.HomeView;

public class ProfileForm extends FormLayout {
    private final CoreAPI coreAPI;
    private final ProfileFormModel profileFormModel;

    private final TextField firstName = new TextField();
    private final TextField lastName = new TextField();
    private final TextField phoneNumber = new TextField();
    private final Checkbox smsConfirmation = new Checkbox();
    private final TextField email = new TextField();
    private final Checkbox emailConfirmation = new Checkbox();

    private final Button updateProfile = new Button();
    private final Button updateUsername = new Button();
    private final Button updatePassword = new Button();

    private final Binder<ProfileFormModel> binder = new Binder<>(ProfileFormModel.class);

    public ProfileForm(ProfileFormModel profileFormModel, CoreAPI coreAPI) {
        addClassName("profile-form");
        this.coreAPI = coreAPI;
        this.profileFormModel = profileFormModel;

        try {
            profileFormModel.setOptions(coreAPI.getOptionsByEmployeeId(profileFormModel.getEmployeeId()));
        } catch (NotAuthenticatedException e) {
            new FailNotification(e.getMessage());
            UI.getCurrent().navigate(HomeView.class);
        }

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
        var updateUsernameContainer = getUpdateUsernameContainer();
        var updatePasswordContainer = getUpdatePasswordContainer();

        Optional<AuthUserOptionDTO> alreadyUsernameModification = profileFormModel.getOptions().stream()
                .filter(option -> option.name().equals(AuthUserOptions.ALREADY_ONCE_USERNAME_MODIFICATION.name()))
                .findFirst();

        // this strange condition is caused of that tested values have no relations to auth_user_option yet. That can be
        // simplified
        // after fix that in changelog insertions of tested values
        // todo update changelogs /\
        if (alreadyUsernameModification.isPresent()) {
            if (alreadyUsernameModification.get().enabled()) {
                verticalLayout.add(firstName, lastName, updatableDataContainer, updatePasswordContainer);
            } else {
                verticalLayout.add(
                        firstName, lastName, updatableDataContainer, updateUsernameContainer, updatePasswordContainer);
            }
        } else {
            verticalLayout.add(
                    firstName, lastName, updatableDataContainer, updateUsernameContainer, updatePasswordContainer);
        }

        return verticalLayout;
    }

    private Component updatableDataContainer() {
        var container = new VerticalLayout();
        container.setPadding(true);
        container.setSpacing(true);

        //        var disclaimer = new Paragraph(getTranslation("profileForm.phoneNumberDisclaimer"));
        //        disclaimer
        //                .getStyle()
        //                .set("font-size", "var(--lumo-font-size-s)")
        //                .set("color", "var(--lumo-secondary-text-color)")
        //                .set("margin", "0");

        for (AuthUserOptionDTO option : profileFormModel.getOptions()) {
            switch (option.name()) {
                case "AGREE_RECEIVE_SMS_NOTIFICATION" -> smsConfirmation.setValue(option.enabled());
                case "AGREE_RECEIVE_EMAIL_NOTIFICATION" -> emailConfirmation.setValue(option.enabled());
            }
        }

        //        container.add(disclaimer, phoneNumber, smsConfirmation, email, emailConfirmation, updateProfile);
        container.add(phoneNumber, email, emailConfirmation, updateProfile);

        container.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.START);

        return container;
    }

    private VerticalLayout getUpdateUsernameContainer() {
        var container = new VerticalLayout();
        container
                .getStyle()
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "8px")
                .set("padding", "1rem")
                .set("margin-top", "1rem");

        var disclaimer = new Paragraph(getTranslation("profileForm.updateUsernameDisclaimer"));
        disclaimer
                .getStyle()
                .set("font-size", "var(--lumo-font-size-s)")
                .set("color", "var(--lumo-secondary-text-color)")
                .set("margin", "0");

        Span infoText = new Span(getTranslation("profileForm.updateUsernameInfo"));

        updateUsername.addThemeVariants(ButtonVariant.LUMO_WARNING);
        updateUsername.setWidth("140px");

        updateUsername.addClickListener(event -> {
            new UpdateUsernameDialog(profileFormModel, coreAPI).open();
        });

        container.add(disclaimer, updateUsername, infoText);
        return container;
    }

    private VerticalLayout getUpdatePasswordContainer() {
        var container = new VerticalLayout();
        container
                .getStyle()
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "8px")
                .set("padding", "1rem")
                .set("margin-top", "1rem");

        var disclaimer = new Paragraph(getTranslation("profileForm.updatePasswordDisclaimer"));
        disclaimer
                .getStyle()
                .set("font-size", "var(--lumo-font-size-s)")
                .set("color", "var(--lumo-secondary-text-color)")
                .set("margin", "0");

        Span infoText = new Span(getTranslation("profileForm.updatePasswordInfo"));

        updatePassword.addThemeVariants(ButtonVariant.LUMO_WARNING);
        updatePassword.setWidth("140px");

        updatePassword.addClickListener(event -> {
            new UpdatePasswordDialog(profileFormModel, coreAPI).open();
        });

        container.add(disclaimer, updatePassword, infoText);
        return container;
    }

    private void localize() {
        firstName.setLabel(getTranslation("employeeForm.firstName"));
        lastName.setLabel(getTranslation("employeeForm.lastName"));
        phoneNumber.setLabel(getTranslation("employeeForm.phoneNumber"));
        smsConfirmation.setLabel(getTranslation("profileForm.smsConfirmation"));
        emailConfirmation.setLabel(getTranslation("profileForm.emailConfirmation"));
        email.setLabel(getTranslation("employeeForm.email"));

        updateProfile.setText(getTranslation("employeeForm.update"));
        updateUsername.setText(getTranslation("profileForm.edit"));
        updatePassword.setText(getTranslation("profileForm.edit"));
    }

    private void update() {
        var selectedOptions = collectSelectedOptions();

        var updateEmployeeDTO = UpdateEmployeeDTO.builder()
                .employeeId(binder.getBean().getEmployeeId())
                .phoneNumber(phoneNumber.getValue())
                .email(email.getValue())
                .options(selectedOptions)
                .build();

        try {
            Optional<EmployeeDTO> employeeDTO = coreAPI.updateEmployeeSelfProfile(updateEmployeeDTO);

            if (employeeDTO.isPresent()) {
                if (employeeDTO.get().phoneNumber() != null) {
                    binder.getBean().setPhoneNumber(employeeDTO.get().phoneNumber());
                }
                if (employeeDTO.get().email() != null) {
                    binder.getBean().setEmail(employeeDTO.get().email());
                }
                profileFormModel.setOptions(coreAPI.getOptionsByEmployeeId(profileFormModel.getEmployeeId()));
                new SuccessNotification(getTranslation("profileForm.successUpdate"));
            } else {
                new FailNotification(getTranslation("profileForm.failedUpdate"));
                phoneNumber.setValue(binder.getBean().getPhoneNumber());
                email.setValue(binder.getBean().getEmail());
            }
        } catch (NotAuthenticatedException e) {
            new FailNotification(e.getMessage()).open();
        }
    }

    private Set<AuthUserOptionDTO> collectSelectedOptions() {

        var smsAgreement = AuthUserOptionDTO.builder()
                .employeeId(profileFormModel.getEmployeeId())
                .optionId(AuthUserOptions.AGREE_RECEIVE_SMS_NOTIFICATION.getId())
                .name(AuthUserOptions.AGREE_RECEIVE_SMS_NOTIFICATION.name())
                .enabled(smsConfirmation.getValue())
                .build();

        var emailAgreement = AuthUserOptionDTO.builder()
                .employeeId(profileFormModel.getEmployeeId())
                .optionId(AuthUserOptions.AGREE_RECEIVE_EMAIL_NOTIFICATION.getId())
                .name(AuthUserOptions.AGREE_RECEIVE_EMAIL_NOTIFICATION.name())
                .enabled(emailConfirmation.getValue())
                .build();

        return new HashSet<>(Set.of(smsAgreement, emailAgreement));
    }
}
