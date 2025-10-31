package pl.crewops.ui.component.dialog.credentialsDialog;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;
import lombok.Setter;
import pl.crewops.model.ProfileFormModel;

@Getter
@Setter
class UpdatePasswordForm extends FormLayout {
    private final PasswordField password = new PasswordField();
    private final PasswordField repeatPassword = new PasswordField();
    private final PasswordField currentPassword = new PasswordField();

    private final Button update = new Button();
    private final Button close = new Button();

    private final Binder<UpdateCredentialsData> binder = new Binder<>(UpdateCredentialsData.class);

    public UpdatePasswordForm(ProfileFormModel profileFormModel) {
        addClassName("updateCredentialsForm");

        localize();
        configureBinder(profileFormModel);
        configureButtons();

        VerticalLayout layout = new VerticalLayout(password, repeatPassword, currentPassword, update, close);
        layout.setSpacing(true);
        layout.setPadding(true);
        add(layout);
    }

    private void localize() {
        password.setLabel(getTranslation("updateCredentialsForm.password"));
        repeatPassword.setLabel(getTranslation("updateCredentialsForm.repeatPassword"));
        currentPassword.setLabel(getTranslation("updateCredentialsForm.currentPassword"));

        update.setText(getTranslation("employeeForm.update"));
        close.setText(getTranslation("employeeForm.close"));
    }

    private void configureBinder(ProfileFormModel profileFormModel) {
        binder.forField(password)
                .withValidator(
                        value -> value == null || value.isEmpty() || value.length() >= 6,
                        getTranslation("updateCredentialsForm.password.invalid"))
                .bind(UpdateCredentialsData::getPassword, UpdateCredentialsData::setPassword);

        binder.forField(repeatPassword)
                .withValidator(
                        value -> value == null || value.isEmpty() || value.equals(password.getValue()),
                        getTranslation("updateCredentialsForm.password.repeatMismatch"))
                .bind(UpdateCredentialsData::getRepeatPassword, UpdateCredentialsData::setRepeatPassword);

        binder.forField(currentPassword)
                .withValidator(
                        value -> !value.isEmpty(), getTranslation("updateCredentialsForm.currentPassword.invalid"))
                .bind(UpdateCredentialsData::getCurrentPassword, UpdateCredentialsData::setCurrentPassword);

        binder.setBean(UpdateCredentialsData.builder()
                .username(profileFormModel.getUsername())
                .repeatUsername(profileFormModel.getUsername())
                .password(null)
                .repeatPassword(null)
                .currentPassword(null)
                .build());
    }

    private void configureButtons() {
        update.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        update.addClickListener(event -> {
            if (binder.validate().isOk()) {
                var updateCredentialsData = UpdateCredentialsData.builder()
                        .password(password.getValue())
                        .repeatPassword(repeatPassword.getValue())
                        .currentPassword(currentPassword.getValue())
                        .build();
                fireEvent(new UpdateEvent(this, updateCredentialsData));
            }
        });

        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.addClickListener(event -> {
            fireEvent(new CloseEvent(this));
        });
    }

    public abstract class UpdatePasswordFormEvent extends ComponentEvent<UpdatePasswordForm> {
        public UpdatePasswordFormEvent(UpdatePasswordForm source) {
            super(source, false);
        }
    }

    public class UpdateEvent extends UpdatePasswordFormEvent {
        @Getter
        private final UpdateCredentialsData updateCredentialsData;

        public UpdateEvent(UpdatePasswordForm source, UpdateCredentialsData updateCredentialsData) {
            super(source);
            this.updateCredentialsData = updateCredentialsData;
        }
    }

    public class CloseEvent extends UpdatePasswordFormEvent {
        public CloseEvent(UpdatePasswordForm source) {
            super(source);
        }
    }

    public Registration addUpdateListener(ComponentEventListener<UpdateEvent> listener) {
        return addListener(UpdateEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }
}
