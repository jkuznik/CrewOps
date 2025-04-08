package pl.crewops.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.infrastructure.core.CoreClient;
import pl.crewops.view.component.QualificationAccordion;

public class EmployeeForm extends FormLayout {
    private final CoreClient coreClient;

    TextField firstName = new TextField("First name");
    TextField lastName = new TextField("Last name");
    TextField birthDate = new TextField("Birth date");
    TextField phoneNumber = new TextField("Phone number");
    TextField department = new TextField("Department");
    QualificationAccordion qualifications;
    Accordion vehicles = new Accordion();
    //    ComboBox<Status> status = new ComboBox<>("Status");
    //    ComboBox<Company> company = new ComboBox<>("Company");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");
    // Other fields omitted
    //    Binder<Contact> binder = new BeanValidationBinder<>(Contact.class);

    public EmployeeForm(CoreClient coreClient, EmployeeDTO employeeDTO) {
        this.coreClient = coreClient;
        qualifications = new QualificationAccordion(coreClient);
        qualifications.config(employeeDTO.qualifications());
        addClassName("contact-form");
        //        qualificationsAccordion(employeeDTO.qualifications());
        //        binder.bindInstanceFields(this);
        //
        //        company.setItems(companies);
        //        company.setItemLabelGenerator(Company::getName);
        //        status.setItems(statuses);
        //        status.setItemLabelGenerator(Status::getName);

        add(firstName, lastName, birthDate, phoneNumber, department, qualifications, vehicles, createButtonsLayout());
    }

    private void completeFormData(EmployeeDTO employeeDTO) {
        firstName.setValue(employeeDTO.firstName());
        lastName.setValue(employeeDTO.lastName());
        birthDate.setValue(employeeDTO.birthDate().toString());
        phoneNumber.setValue(employeeDTO.phoneNumber());
        department.setValue(employeeDTO.department());
    }

    private void qualificationsAccordion(Set<UUID> employeeQualifications) {
        //        Span name = new Span("Sophia Williams");
        //        Span email = new Span("sophia.williams@company.com");
        //        Span phone = new Span("(501) 555-9128");

        List<Span> spans = new ArrayList<>();

        //        employeeQualifications.forEach(employeeQualification -> {
        //            spans.add(new Span(employeeQualification.toString()));
        //        })
        //
        //        VerticalLayout personalInformationLayout = new VerticalLayout(name,
        //                email, phone);
        //        personalInformationLayout.setSpacing(false);
        //        personalInformationLayout.setPadding(false);
        //
        //        qualifications.
        //        qualifications.add("Qualifications", personalInformationLayout);
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        //        save.addClickListener(event -> validateAndSave()); // <1>
        //        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean()))); // <2>
        //        close.addClickListener(event -> fireEvent(new CloseEvent(this))); // <3>

        //        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid())); // <4>
        return new HorizontalLayout(save, delete, close);
    }

    //    private void validateAndSave() {
    //        if(binder.isValid()) {
    //            fireEvent(new SaveEvent(this, binder.getBean())); // <6>
    //        }
    //    }

    //    public void setContact(Contact contact) {
    //        binder.setBean(contact); // <1>
    //    }

    // Events
    //    public static abstract class ContactFormEvent extends ComponentEvent<ContactForm> {
    //        private Contact contact;

    //        protected ContactFormEvent(ContactForm source, Contact contact) {
    //            super(source, false);
    //            this.contact = contact;
    //        }

    //        public Contact getContact() {
    //            return contact;
    //        }
    //    }
    //
    //    public static class SaveEvent extends ContactFormEvent {
    //        SaveEvent(ContactForm source, Contact contact) {
    //            super(source, contact);
    //        }
    //    }
    //
    //    public static class DeleteEvent extends ContactFormEvent {
    //        DeleteEvent(ContactForm source, Contact contact) {
    //            super(source, contact);
    //        }
    //
    //    }
    //
    //    public static class CloseEvent extends ContactFormEvent {
    //        CloseEvent(ContactForm source) {
    //            super(source, null);
    //        }
    //    }
    //
    //    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
    //        return addListener(DeleteEvent.class, listener);
    //    }

    //    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
    //        return addListener(SaveEvent.class, listener);
    //    }
    //    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
    //        return addListener(CloseEvent.class, listener);
    //    }

}
