package pl.crewops.view.component;

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
import pl.crewops.dto.employee.EmployeeFormModel;
import pl.crewops.infrastructure.core.CoreAPI;

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

        save.addClickListener(event -> validateAndSave()); // <1>
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, model))); // <2>
        close.addClickListener(event -> fireEvent(new CloseEvent(this))); // <3>

        binder.addStatusChangeListener(e -> save.setEnabled(modelValidation(model))); // <4>
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if (modelValidation(model)) {
            fireEvent(new SaveEvent(this, model)); // <6>
        }
    }

    public void setEmployee(EmployeeDTO employeeDTO) {
        binder.readBean(employeeDTO);
        if (employeeDTO != null) {
            model.setFirstName(firstName.getValue());
            model.setLastName(lastName.getValue());
            model.setBirthDate(birthDate.getValue());
            model.setPhoneNumber(phoneNumber.getValue());
            model.setDepartment(department.getValue());
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

    // TODO: add logic to inform user which text field are not valid in case of false return
    private boolean modelValidation(EmployeeFormModel model) {
        model.setFirstName(firstName.getValue());
        model.setLastName(lastName.getValue());
        model.setBirthDate(birthDate.getValue());
        model.setPhoneNumber(phoneNumber.getValue());
        model.setDepartment(department.getValue());

        if (model.getFirstName().isEmpty()) {
            return false;
        }
        if (model.getLastName().isEmpty()) {
            return false;
        }
        if (model.getBirthDate() == null) {
            return false;
        }
        if (model.getPhoneNumber().length() > 15) {
            return false;
        }
        if (model.getDepartment().isEmpty()) {
            return false;
        }
        return true;
    }
}
