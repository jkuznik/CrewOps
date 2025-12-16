package pl.crewops.ui.component.form;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;
import pl.crewops.model.QualificationFormModel;

public class QualificationForm extends FormLayout {
    TextField description = new TextField("Description");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button update = new Button("Update");
    Button close = new Button("Cancel");

    Binder<QualificationFormModel> binder = new BeanValidationBinder<>(QualificationFormModel.class);

    public QualificationForm() {
        addClassName("qualification-form");

        localize();

        description.setValueChangeMode(ValueChangeMode.EAGER);

        binder.bindInstanceFields(this);
        binder.setBean(QualificationFormModel.builder().build()); // kluczowe!

        add(description, createButtonsLayout());
    }

    private void localize() {
        description.setLabel(getTranslation("qualificationForm.description"));

        save.setText(getTranslation("qualificationForm.save"));
        update.setText(getTranslation("qualificationForm.update"));
        delete.setText(getTranslation("qualificationForm.delete"));
        close.setText(getTranslation("qualificationForm.close"));
    }

    public void setFormModeSave() {
        save.setVisible(true);
        update.setVisible(false);
        delete.setVisible(false);
    }

    public void setFormModeUpdate() {
        save.setVisible(false);
        update.setVisible(true);
        delete.setVisible(true);
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

    private void validateAndSave() {
        var qualificationFormModel = QualificationFormModel.builder()
                .description(description.getValue())
                .build();
        binder.setBean(qualificationFormModel);
        if (binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }

    private void validateAndUpdate() {
        if (binder.isValid()) {
            fireEvent(new UpdateEvent(this, binder.getBean()));
        }
    }

    public void setQualification(QualificationFormModel qualificationFormModel) {
        binder.setBean(qualificationFormModel);
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

    public static class UpdateEvent extends QualificationFormEvent {

        public UpdateEvent(QualificationForm source, QualificationFormModel qualificationFormModel) {
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

    public Registration addUpdateListener(ComponentEventListener<UpdateEvent> listener) {
        return addListener(UpdateEvent.class, listener);
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        return addListener(DeleteEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }
}
