package pl.crewops.view.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import pl.crewops.dto.qualification.QualificationDTO;
import pl.crewops.dto.qualification.QualificationFormModel;
import pl.crewops.infrastructure.core.CoreAPI;

@SpringComponent
public class QualificationForm extends FormLayout {
    TextField description = new TextField("Description");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");
    Binder<QualificationDTO> binder = new BeanValidationBinder<>(QualificationDTO.class);

    // TODO: temporary I left this solution. Improve binding values in the future
    QualificationFormModel model = new QualificationFormModel();

    public QualificationForm(CoreAPI coreAPI) {
        addClassName("qualification-form");

        binder.bindInstanceFields(this);

        add(description, createButtonsLayout());
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

    public void setQualification(QualificationDTO qualificationDTO) {
        binder.readBean(qualificationDTO);
        if (qualificationDTO != null) {
            setModelValues(qualificationDTO);
        }
    }

    // Events
    public abstract static class QualificationFormEvent extends ComponentEvent<QualificationForm> {

        private QualificationFormModel qualificationFormModel;

        protected QualificationFormEvent(QualificationForm source, QualificationFormModel qualificationFormModel) {
            super(source, false);
            this.qualificationFormModel = qualificationFormModel;
        }

        public QualificationFormModel getQualification() {
            return qualificationFormModel;
        }
    }

    public static class SaveEvent extends QualificationFormEvent {

        SaveEvent(QualificationForm source, QualificationFormModel qualificationFormModel) {
            super(source, qualificationFormModel);
        }
    }

    public static class DeleteEvent extends QualificationFormEvent {

        DeleteEvent(QualificationForm source, QualificationFormModel qualificationFormModel) {
            super(source, qualificationFormModel);
        }
    }

    public static class CloseEvent extends QualificationFormEvent {

        CloseEvent(QualificationForm source) {
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

    private void setModelValues(QualificationDTO qualificationDTO) {
        model.setId(qualificationDTO.id());
        model.setDescription(description.getValue());
    }

    private boolean modelValidation(QualificationFormModel model) {
        model.setDescription(description.getValue());

        if (model.getDescription().isEmpty()) {
            return false;
        }
        return true;
    }
}
