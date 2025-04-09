package pl.crewops.view.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.accordion.Accordion;
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
    private final TextField firstName = new TextField("First name");
    private final TextField lastName = new TextField("Last name");
    private final DatePicker birthDate = new DatePicker("Birth date");
    private final TextField phoneNumber = new TextField("Phone number");
    private final TextField department = new TextField("Department");

    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final Button close = new Button("Cancel");

    private final QualificationAccordion qualifications;
    Accordion vehicles = new Accordion();

    private final Binder<EmployeeFormModel> employeeDTOBinder = new BeanValidationBinder<>(EmployeeFormModel.class);

    public EmployeeForm(CoreAPI coreAPI) {
        addClassName("contact-form");

        qualifications = new QualificationAccordion(coreAPI);

        employeeDTOBinder.bindInstanceFields(this);

        add(firstName, lastName, birthDate, phoneNumber, department, qualifications, vehicles, createButtonsLayout());
    }

    public void completeFormData(EmployeeDTO employeeDTO) {
        firstName.setValue(employeeDTO.firstName());
        lastName.setValue(employeeDTO.lastName());
        birthDate.setValue(employeeDTO.birthDate());
        phoneNumber.setValue(employeeDTO.phoneNumber());
        department.setValue(employeeDTO.department());
        qualifications.config(employeeDTO.qualifications());
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, employeeDTOBinder.getBean())));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        employeeDTOBinder.addStatusChangeListener(e -> save.setEnabled(employeeDTOBinder.isValid()));
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if (employeeDTOBinder.writeBeanIfValid(employeeDTOBinder.getBean())) {
            fireEvent(new SaveEvent(this, employeeDTOBinder.getBean()));
        }
    }

    public void setEmployee(EmployeeFormModel employee) {
        employeeDTOBinder.setBean(employee);
        if (employee != null) {
            qualifications.config(employee.getQualifications());
        }
    }

    // Events
    public abstract static class EmployeeFormEvent extends ComponentEvent<EmployeeForm> {
        private EmployeeFormModel employee;

        protected EmployeeFormEvent(EmployeeForm source, EmployeeFormModel employee) {
            super(source, false);
            this.employee = employee;
        }

        public EmployeeFormModel getEmployee() {
            return employee;
        }
    }

    public static class SaveEvent extends EmployeeFormEvent {
        SaveEvent(EmployeeForm source, EmployeeFormModel employee) {
            super(source, employee);
        }
    }

    public static class DeleteEvent extends EmployeeFormEvent {
        DeleteEvent(EmployeeForm source, EmployeeFormModel employee) {
            super(source, employee);
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
}
