package pl.crewops.view.component.form;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import pl.crewops.model.BreakdownFormModel;

public class BreakdownForm extends FormLayout {
    // TODO: Implement logic if vehicle have more than 1 critical breakdown - solve one of them should not make vehicle
    // operational
    // TODO: Update form to display all breakdown description, add solvedBy field and solvedAt date field, in case if
    // breakdown is solved hide 'update' button
    private final TextField vehicle = new TextField("Vehicle");
    private final TextField description = new TextField("Description");
    private final Checkbox solved = new Checkbox("Solved");
    private final Checkbox critical = new Checkbox("Critical");

    private final Button save = new Button("Save");
    private final Button update = new Button("Update");
    private final Button close = new Button("Close");

    private final Binder<BreakdownFormModel> binder = new BeanValidationBinder<>(BreakdownFormModel.class);

    public BreakdownForm() {
        addClassName("breakdown-form");

        localize();

        configureBinder();

        add(vehicle, description, solved, critical, createButtonsLayout());
    }

    private void localize() {
        vehicle.setLabel(getTranslation("breakdownForm.vehicle"));
        description.setLabel(getTranslation("breakdownForm.description"));
        solved.setLabel(getTranslation("breakdownForm.solved"));
        critical.setLabel(getTranslation("breakdownForm.critical"));

        save.setText(getTranslation("breakdownForm.save"));
        update.setText(getTranslation("breakdownForm.update"));
        close.setText(getTranslation("breakdownForm.close"));
    }

    private void configureBinder() {
        binder.forField(vehicle).bindReadOnly(model -> model.getVehicle().registerNumber());

        binder.forField(description).bind(BreakdownFormModel::getDescription, BreakdownFormModel::setDescription);

        // TODO: implement logic for setSolved only by mechanics, shift leader or manager
        binder.forField(solved).bind(BreakdownFormModel::isSolved, BreakdownFormModel::setSolved);

        binder.forField(critical).bind(BreakdownFormModel::isCritical, BreakdownFormModel::setCritical);
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        update.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        update.addClickListener(event -> validateAndUpdate());
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, update, close);
    }

    public void setFormModeSave() {
        save.setVisible(true);
        solved.setVisible(false);
        description.setReadOnly(false);
        critical.setVisible(true);
        update.setVisible(false);
    }

    public void setFormModeUpdate() {
        save.setVisible(false);
        solved.setVisible(true);
        description.setReadOnly(true);
        critical.setVisible(false);
        update.setVisible(true);
    }

    private void validateAndSave() {
        BreakdownFormModel breakdownFormModel = new BreakdownFormModel();
        breakdownFormModel.setVehicle(binder.getBean().getVehicle());
        breakdownFormModel.setDescription(description.getValue());
        breakdownFormModel.setReportedBy(binder.getBean().getReportedBy());
        breakdownFormModel.setCritical(critical.getValue());
        binder.setBean(breakdownFormModel);
        if (binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }

    private void validateAndUpdate() {
        if (binder.isValid()) {
            fireEvent(new UpdateEvent(this, binder.getBean()));
        }
    }

    public void setBreakdown(BreakdownFormModel breakdownFormModel) {
        binder.setBean(breakdownFormModel);
    }

    public abstract static class BreakdownFormEvent extends ComponentEvent<BreakdownForm> {
        private final BreakdownFormModel breakdownFormModel;

        protected BreakdownFormEvent(BreakdownForm source, BreakdownFormModel breakdownFormModel) {
            super(source, false);
            this.breakdownFormModel = breakdownFormModel;
        }

        public BreakdownFormModel getBreakdown() {
            return breakdownFormModel;
        }
    }

    public static class SaveEvent extends BreakdownFormEvent {
        SaveEvent(BreakdownForm source, BreakdownFormModel breakdownFormModel) {
            super(source, breakdownFormModel);
        }
    }

    public static class UpdateEvent extends BreakdownFormEvent {
        UpdateEvent(BreakdownForm source, BreakdownFormModel breakdownFormModel) {
            super(source, breakdownFormModel);
        }
    }

    public static class CloseEvent extends BreakdownFormEvent {
        CloseEvent(BreakdownForm source) {
            super(source, null);
        }
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addUpdateListener(ComponentEventListener<UpdateEvent> listener) {
        return addListener(UpdateEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }
}
