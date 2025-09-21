package pl.crewops.component.form;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import pl.crewops.model.ProfileFormModel;

public class ProfileForm extends FormLayout {
    private final TextField firstName = new TextField();
    private final TextField lastName = new TextField();
    private final DatePicker birthDate = new DatePicker();
    private final TextField phoneNumber = new TextField();
    private final EmailField email = new EmailField();

    private final Button update = new Button();
    private final Button credentialsModification = new Button();

    private final Binder<ProfileFormModel> binder = new Binder<>(ProfileFormModel.class);

    public ProfileForm(ProfileFormModel profileFormModel) {
        addClassName("profile-form");

        localize();

        configureBinder(profileFormModel);

        var configuredForm = getConfiguredForm();

        add(configuredForm);
    }

    private void configureBinder(ProfileFormModel profileFormModel) {
        binder.bindInstanceFields(this);
        binder.setBean(profileFormModel);
    }

    private VerticalLayout getConfiguredForm() {
        final var verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setSpacing(true);
        verticalLayout.setPadding(true);

        firstName.setEnabled(false);
        lastName.setEnabled(false);
        birthDate.setEnabled(false);

        update.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        update.setWidth("140px");
        update.addClickListener(event -> {
            new Notification("update event").open();
        });

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
            new Dialog("kredki").open();
        });

        updateCredentialsContainer.add(infoText, credentialsModification);

        verticalLayout.add(firstName, lastName, birthDate, phoneNumber, email, update, updateCredentialsContainer);

        return verticalLayout;
    }

    private void localize() {
        firstName.setLabel(getTranslation("employeeForm.firstName"));
        lastName.setLabel(getTranslation("employeeForm.lastName"));
        birthDate.setLabel(getTranslation("employeeForm.birthDate"));
        phoneNumber.setLabel(getTranslation("employeeForm.phoneNumber"));
        email.setLabel(getTranslation("employeeForm.email"));

        update.setText(getTranslation("employeeForm.update"));
        credentialsModification.setText(getTranslation("employeeForm.update"));
    }
}
