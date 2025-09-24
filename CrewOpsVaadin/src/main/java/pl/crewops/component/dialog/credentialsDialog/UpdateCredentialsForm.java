package pl.crewops.component.dialog.credentialsDialog;

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
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.crewops.model.ProfileFormModel;

@Getter
@Setter
public class UpdateCredentialsForm extends FormLayout {

    private final TextField username = new TextField();
    private final TextField repeatUsername = new TextField();
    private final PasswordField password = new PasswordField();
    private final PasswordField repeatPassword = new PasswordField();
    private final PasswordField currentPassword = new PasswordField();

    private final Button update = new Button();
    private final Button close = new Button();

    private final Binder<UpdateCredentialsData> binder = new Binder<>(UpdateCredentialsData.class);

    public UpdateCredentialsForm(ProfileFormModel profileFormModel) {
        addClassName("updateCredentialsForm");

        localize();
        configureBinder(profileFormModel);
        configureButtons();

        VerticalLayout layout =
                new VerticalLayout(username, repeatUsername, password, repeatPassword, currentPassword, update, close);
        layout.setSpacing(true);
        layout.setPadding(true);
        add(layout);
    }

    private void localize() {
        username.setLabel(getTranslation("updateCredentialsForm.username"));
        repeatUsername.setLabel(getTranslation("updateCredentialsForm.repeatUsername"));
        password.setLabel(getTranslation("updateCredentialsForm.password"));
        repeatPassword.setLabel(getTranslation("updateCredentialsForm.repeatPassword"));
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
                        value -> value == null || value.isEmpty() || value.length() >= 6,
                        getTranslation("updateCredentialsForm.currentPassword.invalid"))
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
            var updateCredentialsData = UpdateCredentialsData.builder()
                    .username(username.getValue())
                    .repeatUsername(repeatUsername.getValue())
                    .password(password.getValue())
                    .repeatPassword(repeatPassword.getValue())
                    .currentPassword(currentPassword.getValue())
                    .build();
            fireEvent(new UpdateEvent(this, updateCredentialsData));
        });

        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.addClickListener(event -> {
            fireEvent(new CloseEvent(this));
        });
    }

    public abstract class UpdateCredentialsFormEvent extends ComponentEvent<UpdateCredentialsForm> {
        public UpdateCredentialsFormEvent(UpdateCredentialsForm source) {
            super(source, false);
        }
    }

    public class UpdateEvent extends UpdateCredentialsFormEvent {
        @Getter
        private final UpdateCredentialsData updateCredentialsData;

        public UpdateEvent(UpdateCredentialsForm source, UpdateCredentialsData updateCredentialsData) {
            super(source);
            this.updateCredentialsData = updateCredentialsData;
        }
    }

    public class CloseEvent extends UpdateCredentialsFormEvent {
        public CloseEvent(UpdateCredentialsForm source) {
            super(source);
        }
    }

    public Registration addUpdateListener(ComponentEventListener<UpdateEvent> listener) {
        return addListener(UpdateEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }

    @Getter
    @Setter
    @Builder
    public static class UpdateCredentialsData {
        private String username;
        private String repeatUsername;
        private String password;
        private String repeatPassword;
        private String currentPassword;
    }
}
