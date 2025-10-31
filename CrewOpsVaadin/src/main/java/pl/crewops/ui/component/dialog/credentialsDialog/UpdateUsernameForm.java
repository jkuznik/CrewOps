package pl.crewops.ui.component.dialog.credentialsDialog;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;
import pl.crewops.model.ProfileFormModel;

class UpdateUsernameForm extends FormLayout {
    private final TextField username = new TextField();
    private final TextField repeatUsername = new TextField();
    private final PasswordField currentPassword = new PasswordField();

    private final Button update = new Button();
    private final Button close = new Button();

    private final Binder<UpdateCredentialsData> binder = new Binder<>(UpdateCredentialsData.class);

    public UpdateUsernameForm(ProfileFormModel profileFormModel) {
        addClassName("updateCredentialsForm");

        localize();
        configureBinder(profileFormModel);
        configureButtons();

        VerticalLayout layout = new VerticalLayout(username, repeatUsername, currentPassword, update, close);
        layout.setSpacing(true);
        layout.setPadding(true);
        add(layout);
    }

    private void localize() {
        username.setLabel(getTranslation("updateCredentialsForm.username"));
        repeatUsername.setLabel(getTranslation("updateCredentialsForm.repeatUsername"));
        currentPassword.setLabel(getTranslation("updateCredentialsForm.currentPassword"));

        update.setText(getTranslation("employeeForm.update"));
        close.setText(getTranslation("employeeForm.close"));
    }

    private void configureBinder(ProfileFormModel profileFormModel) {
        binder.forField(username)
                .withValidator(
                        value -> value == null || value.isEmpty() || (value.length() >= 3 && value.length() <= 50),
                        getTranslation("updateCredentialsForm.username.invalid"))
                .bind(UpdateCredentialsData::getUsername, UpdateCredentialsData::setUsername);

        binder.forField(repeatUsername)
                .withValidator(
                        value -> value == null || value.isEmpty() || value.equals(username.getValue()),
                        getTranslation("updateCredentialsForm.username.repeatMismatch"))
                .bind(UpdateCredentialsData::getRepeatUsername, UpdateCredentialsData::setRepeatUsername);

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
                        .username(username.getValue())
                        .repeatUsername(repeatUsername.getValue())
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

    public abstract class UpdateUsernameFormEvent extends ComponentEvent<UpdateUsernameForm> {
        public UpdateUsernameFormEvent(UpdateUsernameForm source) {
            super(source, false);
        }
    }

    public class UpdateEvent extends UpdateUsernameFormEvent {
        @Getter
        private final UpdateCredentialsData updateCredentialsData;

        public UpdateEvent(UpdateUsernameForm source, UpdateCredentialsData updateCredentialsData) {
            super(source);
            this.updateCredentialsData = updateCredentialsData;
        }
    }

    public class CloseEvent extends UpdateUsernameFormEvent {
        public CloseEvent(UpdateUsernameForm source) {
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
