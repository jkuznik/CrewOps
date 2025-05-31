package pl.crewops.view.component.form;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.shared.Registration;
import java.util.Arrays;
import pl.crewops.model.EmployeeFormModel;
import pl.crewops.model.auth.RoleType;
import pl.crewops.view.component.accordion.QualificationAccordion;
import pl.crewops.view.component.accordion.VehicleAccordion;

public class EmployeeForm extends FormLayout {
    private final TextField firstName = new TextField("First name");
    private final TextField lastName = new TextField("Last name");
    private final DatePicker birthDate = new DatePicker("Birth date");
    private final TextField phoneNumber = new TextField("Phone number");
    private final TextField department = new TextField("Department");
    private final CheckboxGroup<RoleType> roles = new CheckboxGroup<>("Roles");
    private final QualificationAccordion qualifications = new QualificationAccordion();
    private final VehicleAccordion vehicles = new VehicleAccordion();

    private final Button save = new Button("Save");
    private final Button update = new Button("Update");
    private final Button delete = new Button("Delete");
    private final Button close = new Button("Cancel");

    private final Binder<EmployeeFormModel> binder = new BeanValidationBinder<>(EmployeeFormModel.class);

    public EmployeeForm() {
        addClassName("employee-form");

        localize();

        roles.setItems(Arrays.stream(RoleType.values())
                .filter(role -> role != RoleType.ADMIN)
                .toList());
        roles.setRenderer(
                new TextRenderer<>(role -> role.name().replace("_", " ").toLowerCase()));

        binder.bindInstanceFields(this);

        add(
                firstName,
                lastName,
                birthDate,
                phoneNumber,
                department,
                roles,
                qualifications,
                vehicles,
                createButtonsLayout());
    }

    private void localize() {
        firstName.setLabel(getTranslation("employeeForm.firstName"));
        lastName.setLabel(getTranslation("employeeForm.lastName"));
        birthDate.setLabel(getTranslation("employeeForm.birthDate"));
        phoneNumber.setLabel(getTranslation("employeeForm.phoneNumber"));
        department.setLabel(getTranslation("employeeForm.department"));
        roles.setLabel(getTranslation("employeeForm.roles"));

        save.setText(getTranslation("employeeForm.save"));
        update.setText(getTranslation("employeeForm.update"));
        delete.setText(getTranslation("employeeForm.delete"));
        close.setText(getTranslation("employeeForm.close"));
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        update.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
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
        qualifications.setVisible(false);
        vehicles.setVisible(false);

        firstName.setReadOnly(false);
        firstName.setEnabled(true);
        lastName.setReadOnly(false);
        lastName.setEnabled(true);
        birthDate.setReadOnly(false);
        birthDate.setEnabled(true);
    }

    public void setFormModeUpdate() {
        save.setVisible(false);
        update.setVisible(true);
        delete.setVisible(true);
        qualifications.setVisible(true);
        vehicles.setVisible(true);

        firstName.setReadOnly(true);
        firstName.setEnabled(false);
        lastName.setReadOnly(true);
        lastName.setEnabled(false);
        birthDate.setReadOnly(true);
        birthDate.setEnabled(false);
    }

    private void validateAndSave() {
        var employeeFormModel = EmployeeFormModel.builder()
                .firstName(firstName.getValue())
                .lastName(lastName.getValue())
                .birthDate(birthDate.getValue())
                .phoneNumber(phoneNumber.getValue())
                .department(department.getValue())
                .roles(roles.getValue())
                .build();
        binder.setBean(employeeFormModel);
        if (binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }

    private void validateAndUpdate() {
        if (binder.isValid()) {
            fireEvent(new UpdateEvent(this, binder.getBean()));
        }
    }

    public void setEmployee(EmployeeFormModel employeeFormModel) {
        binder.setBean(employeeFormModel);
        if (employeeFormModel != null) {
            qualifications.getValues(employeeFormModel.getQualificationsSet());
            vehicles.getValues(employeeFormModel.getVehiclesSet());
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
