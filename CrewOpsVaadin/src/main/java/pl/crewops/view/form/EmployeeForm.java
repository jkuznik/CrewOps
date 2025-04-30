package pl.crewops.view.form;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.view.component.QualificationAccordion;
import pl.crewops.view.component.VehicleAccordion;
import pl.crewops.view.form.model.EmployeeFormModel;

@SpringComponent
public class EmployeeForm extends FormLayout {
    TextField firstName = new TextField("First name");
    TextField lastName = new TextField("Last name");
    DatePicker birthDate = new DatePicker("Birth date");
    TextField phoneNumber = new TextField("Phone number");
    TextField department = new TextField("Department");
    QualificationAccordion qualifications;
    VehicleAccordion vehicles;

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");
    Binder<EmployeeDTO> binder = new BeanValidationBinder<>(EmployeeDTO.class);

    // TODO: temporary I left this solution. Improve binding values in the future
    EmployeeFormModel model = new EmployeeFormModel();

    public EmployeeForm(CoreAPI coreAPI) {
        addClassName("employee-form");

        qualifications = new QualificationAccordion(coreAPI);
        vehicles = new VehicleAccordion(coreAPI);

        binder.bindInstanceFields(this);

        add(firstName, lastName, birthDate, phoneNumber, department, qualifications, vehicles, createButtonsLayout());
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, model)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(modelValidation(model)));
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if (modelValidation(model)) {
            fireEvent(new SaveEvent(this, model));
        }
    }

    public void setEmployee(EmployeeDTO employeeDTO) {
        binder.readBean(employeeDTO);
        if (employeeDTO != null) {
            setModelValues(employeeDTO);
            qualifications.setConfig(employeeDTO.qualifications());
            vehicles.setConfig(employeeDTO.vehicles());
        }
    }

    // Events
    public abstract static class EmployeeFormEvent extends ComponentEvent<EmployeeForm> {

        private EmployeeFormModel employeeFormModel;

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

    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        return addListener(DeleteEvent.class, listener);
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }

    // TODO: add logic to inform user which text field are not valid in case of false return/ or improve binder feature

    private void setModelValues(EmployeeDTO employeeDTO) {
        model.setId(employeeDTO.id());
        model.setFirstName(firstName.getValue());
        model.setLastName(lastName.getValue());
        model.setBirthDate(birthDate.getValue());
        model.setPhoneNumber(phoneNumber.getValue());
        model.setDepartment(department.getValue());
    }

    private boolean modelValidation(EmployeeFormModel employeeFormModel) {
        employeeFormModel.setFirstName(firstName.getValue());
        employeeFormModel.setLastName(lastName.getValue());
        employeeFormModel.setBirthDate(birthDate.getValue());
        employeeFormModel.setPhoneNumber(phoneNumber.getValue());
        employeeFormModel.setDepartment(department.getValue());

        if (employeeFormModel.getFirstName().isEmpty()) {
            return false;
        }
        if (employeeFormModel.getLastName().isEmpty()) {
            return false;
        }
        if (employeeFormModel.getBirthDate() == null) {
            return false;
        }
        if (employeeFormModel.getPhoneNumber().length() > 15) {
            return false;
        }
        if (employeeFormModel.getDepartment().isEmpty()) {
            return false;
        }
        return true;
    }
}
