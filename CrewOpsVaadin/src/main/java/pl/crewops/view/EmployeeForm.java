package pl.crewops.view;

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
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.view.component.QualificationAccordion;

@SpringComponent
public class EmployeeForm extends FormLayout {
    private final CoreAPI coreAPI;

    TextField firstName = new TextField("First name");
    TextField lastName = new TextField("Last name");
    DatePicker birthDate = new DatePicker("Birth date");
    TextField phoneNumber = new TextField("Phone number");
    TextField department = new TextField("Department");
    QualificationAccordion qualifications;
    Accordion vehicles = new Accordion();

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");
    Binder<EmployeeDTO> binder = new BeanValidationBinder<>(EmployeeDTO.class);

    public EmployeeForm(CoreAPI coreAPI) {
        addClassName("contact-form");

        this.coreAPI = coreAPI;
        qualifications = new QualificationAccordion(coreAPI);

        binder.bindInstanceFields(this);

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

        save.addClickListener(event -> validateAndSave()); // <1>
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean()))); // <2>
        close.addClickListener(event -> fireEvent(new CloseEvent(this))); // <3>

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid())); // <4>
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean())); // <6>
        }
    }

    public void setEmployee(EmployeeDTO employeeDTO) {
        binder.readBean(employeeDTO);
        if (employeeDTO != null) {
            qualifications.config(employeeDTO.qualifications());
        }
    }

    // Events
    public abstract static class EmployeeFormEvent extends ComponentEvent<EmployeeForm> {
        private EmployeeDTO employeeDTO;

        protected EmployeeFormEvent(EmployeeForm source, EmployeeDTO employeeDTO) {
            super(source, false);
            this.employeeDTO = employeeDTO;
        }

        public EmployeeDTO getContact() {
            return employeeDTO;
        }
    }

    public static class SaveEvent extends EmployeeFormEvent {
        SaveEvent(EmployeeForm source, EmployeeDTO employeeDTO) {
            super(source, employeeDTO);
        }
    }

    public static class DeleteEvent extends EmployeeFormEvent {
        DeleteEvent(EmployeeForm source, EmployeeDTO employeeDTO) {
            super(source, employeeDTO);
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
