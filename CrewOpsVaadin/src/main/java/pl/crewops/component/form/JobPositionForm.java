package pl.crewops.component.form;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import pl.crewops.model.JobPositionFormModel;
import pl.crewops.model.dto.machine.MachineDTO;

public class JobPositionForm extends FormLayout {

    private final TextField name = new TextField();
    private final ComboBox<MachineDTO> machine = new ComboBox<>();

    private final Button save = new Button();
    private final Button update = new Button();
    private final Button delete = new Button();
    private final Button close = new Button();

    private final Binder<JobPositionFormModel> binder = new BeanValidationBinder<>(JobPositionFormModel.class);

    public JobPositionForm() {
        setSizeFull();

        localize();

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

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        update.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.getElement().getStyle().set("background-color", "#FFA500");
        delete.getElement().getStyle().set("color", "#333333");
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        //        save.addClickListener(event -> validateAndSave());
        //        update.addClickListener(event -> validateAndUpdate());
        //        delete.addClickListener(event -> fireEvent(new EmployeeForm.DeleteEvent(this, binder.getBean())));
        //        close.addClickListener(event -> fireEvent(new EmployeeForm.CloseEvent(this)));
        close.addClickListener(event -> setVisible(false));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, update, delete, close);
    }

    private void localize() {
        name.setLabel(getTranslation("jobPositionForm.name"));
        machine.setLabel(getTranslation("jobPositionForm.machine"));

        save.setText(getTranslation("employeeForm.save"));
        update.setText(getTranslation("employeeForm.update"));
        delete.setText(getTranslation("employeeForm.delete"));
        close.setText(getTranslation("employeeForm.close"));
    }
}
