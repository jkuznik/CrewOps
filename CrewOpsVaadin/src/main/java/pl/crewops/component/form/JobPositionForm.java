package pl.crewops.component.form;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import java.util.List;
import lombok.Getter;
import pl.crewops.component.notification.NotAuthenticatedNotification;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.JobPositionFormModel;
import pl.crewops.model.dto.machine.MachineDTO;
import pl.crewops.util.SpringContextBridge;

public class JobPositionForm extends FormLayout {

    private final CoreAPI coreAPI;

    private final TextField name = new TextField();
    private final ComboBox<MachineDTO> machine = new ComboBox<>();

    private final Button save = new Button();
    private final Button update = new Button();
    private final Button delete = new Button();
    private final Button close = new Button();

    private final Binder<JobPositionFormModel> binder = new BeanValidationBinder<>(JobPositionFormModel.class);

    public JobPositionForm() {
        this.coreAPI = SpringContextBridge.getBean(CoreAPI.class);

        setSizeFull();

        localize();

        updateMachineComboBox();
        configureBinder();

        binder.bindInstanceFields(this);

        add(name, machine, createButtonsLayout());
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

    public void setBean(JobPositionFormModel model) {
        if (model != null) {
            binder.setBean(model);
        }
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);
        save.addClickListener(event -> {
            validateAndSave();
        });

        update.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        update.addClickListener(event -> {
            validateAndUpdate();
        });

        delete.getElement().getStyle().set("background-color", "#FFA500");
        delete.getElement().getStyle().set("color", "#333333");
        delete.addClickListener(event -> {
            fireEvent(new DeleteEvent(this, binder.getBean()));
            setVisible(false);
        });

        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.addClickShortcut(Key.ESCAPE);
        close.addClickListener(event -> {
            //            fireEvent(new CloseEvent(this));
            binder.setBean(null);
            setVisible(false);
        });

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, update, delete, close);
    }

    private void updateMachineComboBox() {
        machine.setItemLabelGenerator(machine -> {
            return machine.machineType().name() + " " + machine.registerNumber();
        });

        try {
            List<MachineDTO> allMachines = coreAPI.getAllMachines();

            machine.setItems(allMachines);

        } catch (NotAuthenticatedException e) {
            new NotAuthenticatedNotification(e.getMessage());
        }
    }

    private void validateAndSave() {
        var jobPositionFormModel = JobPositionFormModel.builder()
                .name(name.getValue())
                .machine(machine.getValue())
                .build();

        if (binder.writeBeanIfValid(jobPositionFormModel)) {
            fireEvent(new CreateEvent(this, jobPositionFormModel));
            updateMachineComboBox();
            setVisible(false);
        }
    }

    private void validateAndUpdate() {
        if (binder.validate().isOk()) {
            fireEvent(new UpdateEvent(this, binder.getBean()));
            updateMachineComboBox();
            setVisible(false);
        }
    }

    private void configureBinder() {
        binder.forField(name)
                .asRequired(getTranslation("validation.required"))
                .withValidator(value -> value.length() <= 255, getTranslation("validation.max.length", 255))
                .bind(JobPositionFormModel::getName, JobPositionFormModel::setName);
    }

    private void localize() {
        name.setLabel(getTranslation("jobPositionForm.name"));
        machine.setLabel(getTranslation("jobPositionForm.machine"));

        save.setText(getTranslation("employeeForm.save"));
        update.setText(getTranslation("employeeForm.update"));
        delete.setText(getTranslation("employeeForm.delete"));
        close.setText(getTranslation("employeeForm.close"));
    }

    public abstract static class JobPositionFormEvent extends ComponentEvent<JobPositionForm> {
        public JobPositionFormEvent(JobPositionForm source) {
            super(source, false);
        }
    }

    public static class CreateEvent extends JobPositionFormEvent {
        @Getter
        private final JobPositionFormModel model;

        public CreateEvent(JobPositionForm source, JobPositionFormModel model) {
            super(source);
            this.model = model;
        }
    }

    public static class UpdateEvent extends JobPositionFormEvent {
        @Getter
        private final JobPositionFormModel model;

        public UpdateEvent(JobPositionForm source, JobPositionFormModel model) {
            super(source);
            this.model = model;
        }
    }

    public static class DeleteEvent extends JobPositionFormEvent {
        @Getter
        private final JobPositionFormModel model;

        public DeleteEvent(JobPositionForm source, JobPositionFormModel model) {
            super(source);
            this.model = model;
        }
    }

    public static class CloseEvent extends JobPositionFormEvent {
        public CloseEvent(JobPositionForm source) {
            super(source);
        }
    }

    public Registration addCreateEventListener(ComponentEventListener<CreateEvent> listener) {
        return addListener(CreateEvent.class, listener);
    }

    public Registration addUpdateEventListener(ComponentEventListener<UpdateEvent> listener) {
        return addListener(UpdateEvent.class, listener);
    }

    public Registration addDeleteEventListener(ComponentEventListener<DeleteEvent> listener) {
        return addListener(DeleteEvent.class, listener);
    }

    public Registration addCloseEventListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }
}
