package pl.crewops.ui.component.form;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.RegexpValidator;
import com.vaadin.flow.shared.Registration;
import java.util.*;
import pl.crewops.model.EmployeeFormModel;
import pl.crewops.ui.component.accordion.DepartmentAccordion;
import pl.crewops.ui.component.accordion.MachineAccordion;
import pl.crewops.ui.component.accordion.QualificationAccordion;
import pl.crewops.ui.component.accordion.RoleAccordion;

@CssImport("./styles/component/combo-box.css")
public class EmployeeForm extends FormLayout {
    private final TextField firstName = new TextField();
    private final TextField lastName = new TextField();
    private final TextField phoneNumber = new TextField();
    private final TextField email = new TextField();

    private final DepartmentAccordion departments;
    private final QualificationAccordion qualifications;
    private final MachineAccordion machines;
    private final RoleAccordion roleAccordion;

    private final Button save = new Button();
    private final Button update = new Button();
    private final Button delete = new Button();
    private final Button close = new Button();

    private final Binder<EmployeeFormModel> binder = new Binder<>(EmployeeFormModel.class);

    private boolean isEmailRequired = false;

    private final Validator<String> emailRequiredValidator = (value, context) -> {
        if (isEmailRequired && (value == null || value.trim().isEmpty())) {
            return ValidationResult.error(getTranslation("employeeForm.email.required"));
        }
        if (value != null && !value.isEmpty()) {
            EmailValidator emailValidator = new EmailValidator(getTranslation("employeeForm.email.invalid"));
            return emailValidator.apply(value, context);
        }
        return ValidationResult.ok();
    };

    private final Validator<String> optionalPhoneNumberValidator = (value, context) -> {
        if (value == null || value.trim().isEmpty()) {
            return ValidationResult.ok();
        }

        final String PHONE_REGEX = "^\\+?[0-9\\s]{6,20}$";
        RegexpValidator regexpValidator =
                new RegexpValidator(getTranslation("employeeForm.phoneNumber.invalid"), PHONE_REGEX);

        return regexpValidator.apply(value, context);
    };

    public EmployeeForm() {
        addClassName("employee-form");

        this.departments = getConfiguredDepartmentsMultiSelectedComboBox();
        this.qualifications = getConfiguredQualificationsAccordion();
        this.machines = getConfiguredMachinesAccordion();
        this.roleAccordion = getConfiguredRoleAccordion();

        localize();

        binder.forField(firstName)
                .withValidator(
                        value -> value != null && !value.trim().isEmpty(),
                        getTranslation("employeeForm.firstName.required")) // Wymagane
                .withValidator(
                        value -> value == null || value.length() >= 2,
                        getTranslation("employeeForm.firstName.tooShort"))
                .bind(EmployeeFormModel::getFirstName, EmployeeFormModel::setFirstName);

        binder.forField(lastName)
                .withValidator(
                        value -> value != null && !value.trim().isEmpty(),
                        getTranslation("employeeForm.lastName.required")) // Wymagane
                .withValidator(
                        value -> value == null || value.length() >= 2, getTranslation("employeeForm.lastName.tooShort"))
                .bind(EmployeeFormModel::getLastName, EmployeeFormModel::setLastName);

        binder.forField(phoneNumber)
                .withValidator(optionalPhoneNumberValidator)
                .bind(EmployeeFormModel::getPhoneNumber, EmployeeFormModel::setPhoneNumber);

        binder.forField(email)
                .withValidator(emailRequiredValidator)
                .bind(EmployeeFormModel::getEmail, EmployeeFormModel::setEmail);

        add(
                firstName,
                lastName,
                phoneNumber,
                email,
                createButtonsLayout(),
                departments,
                qualifications,
                machines,
                roleAccordion);

        setEmailRequired(false);
    }

    /**
     * Ustawia, czy pole email jest wymagane.
     * Użycie tej metody wymusza ponowną walidację pola email.
     * @param required true, jeśli email jest wymagany; false, jeśli opcjonalny.
     */
    public void setEmailRequired(boolean required) {
        this.isEmailRequired = required;
        email.setRequiredIndicatorVisible(required);
        binder.validate();
    }

    private DepartmentAccordion getConfiguredDepartmentsMultiSelectedComboBox() {
        final var departmentAccordion = new DepartmentAccordion();
        departmentAccordion.addUpdateDepartmentListener(event -> {
            var employeeFormModel = EmployeeFormModel.toEmployeeFormModel(event.getEmployeeDTO());
            setBinderValue(employeeFormModel);
            validateAndUpdate();
        });

        return departmentAccordion;
    }

    private QualificationAccordion getConfiguredQualificationsAccordion() {
        final var qualificationAccordion = new QualificationAccordion();
        qualificationAccordion.addUpdateQualificationsListener(event -> {
            var employeeFormModel = EmployeeFormModel.toEmployeeFormModel(event.getEmployeeDTO());
            setBinderValue(employeeFormModel);
            validateAndUpdate();
        });

        return qualificationAccordion;
    }

    private MachineAccordion getConfiguredMachinesAccordion() {
        final var machineAccordion = new MachineAccordion();
        machineAccordion.addUpdateMachineListener(event -> {
            var employeeFormModel = EmployeeFormModel.toEmployeeFormModel(event.getEmployeeDTO());
            setBinderValue(employeeFormModel);
            validateAndUpdate();
        });

        return machineAccordion;
    }

    private RoleAccordion getConfiguredRoleAccordion() {
        final var roleAccordion = new RoleAccordion();

        roleAccordion.addUpdateEvenListener(event -> {
            setBinderValue(event.getEmployeeFormModel());
            validateAndUpdate();
        });

        return roleAccordion;
    }

    private void localize() {
        firstName.setLabel(getTranslation("employeeForm.firstName"));
        firstName.setRequiredIndicatorVisible(true);
        lastName.setLabel(getTranslation("employeeForm.lastName"));
        lastName.setRequiredIndicatorVisible(true);
        phoneNumber.setLabel(getTranslation("employeeForm.phoneNumber"));
        phoneNumber.setRequiredIndicatorVisible(false);
        email.setLabel(getTranslation("employeeForm.email"));

        save.setText(getTranslation("employeeForm.save"));
        update.setText(getTranslation("employeeForm.update"));
        delete.setText(getTranslation("employeeForm.delete"));
        close.setText(getTranslation("employeeForm.close"));
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        update.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.getElement().getStyle().set("background-color", "#FFA500");
        delete.getElement().getStyle().set("color", "#333333");
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        update.addClickListener(event -> validateAndUpdate());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, update, delete, close);
    }

    public void setFormModeSave() {
        save.setVisible(true);
        update.setVisible(false);
        delete.setVisible(false);
        departments.setVisible(false);
        qualifications.setVisible(false);
        machines.setVisible(false);
        roleAccordion.setVisible(false);

        firstName.setReadOnly(false);
        firstName.setEnabled(true);
        lastName.setReadOnly(false);
        lastName.setEnabled(true);
    }

    public void setFormModeUpdate() {
        save.setVisible(false);
        update.setVisible(true);
        delete.setVisible(true);
        departments.setVisible(true);
        qualifications.setVisible(true);
        machines.setVisible(true);
        roleAccordion.setVisible(true);

        firstName.setReadOnly(true);
        firstName.setEnabled(false);
        lastName.setReadOnly(true);
        lastName.setEnabled(false);
    }

    private void validateAndSave() {
        var employeeFormModel = new EmployeeFormModel();

        if (binder.writeBeanIfValid(employeeFormModel)) {
            fireEvent(new SaveEvent(this, employeeFormModel));
        }
    }

    private void validateAndUpdate() {
        if (binder.validate().isOk()) {
            fireEvent(new UpdateEvent(this, binder.getBean()));
        }
    }

    public void setBinderValue(EmployeeFormModel employeeFormModel) {
        binder.setBean(employeeFormModel);

        if (employeeFormModel != null) {
            departments.setValues(employeeFormModel);
            qualifications.setValues(employeeFormModel);
            machines.setValues(employeeFormModel);
            roleAccordion.setValues(employeeFormModel);
        }
    }

    // Events
    public abstract static class EmployeeFormEvent extends ComponentEvent<EmployeeForm> {

        private final EmployeeFormModel employeeFormModel;

        protected EmployeeFormEvent(EmployeeForm source, EmployeeFormModel employeeFormModel) {
            super(source, false);
            this.employeeFormModel = employeeFormModel;
        }

        public EmployeeFormModel getEmployee() {
            return employeeFormModel;
        }
    }

    public static class SaveEvent extends EmployeeFormEvent {
        SaveEvent(EmployeeForm source, EmployeeFormModel employeeFormModel) {
            super(source, employeeFormModel);
        }
    }

    public static class UpdateEvent extends EmployeeFormEvent {
        UpdateEvent(EmployeeForm source, EmployeeFormModel employeeFormModel) {
            super(source, employeeFormModel);
        }
    }

    public static class DeleteEvent extends EmployeeFormEvent {
        DeleteEvent(EmployeeForm source, EmployeeFormModel employeeFormModel) {
            super(source, employeeFormModel);
        }
    }

    public static class CloseEvent extends EmployeeFormEvent {
        CloseEvent(EmployeeForm source) {
            super(source, null);
        }
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addUpdateListener(ComponentEventListener<UpdateEvent> listener) {
        return addListener(UpdateEvent.class, listener);
    }

    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        return addListener(DeleteEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }
}
