package pl.crewops.component.dialog.machineDialog;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.model.EmployeeFormModel;

public class MachineManagerDialog extends Dialog {

    public MachineManagerDialog(EmployeeFormModel employeeFormModel) {
        addClassName("machineManagerDialog");

        setModal(true);
        setDraggable(true);
        setResizable(true);
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);

        setWidth("95vw");
        setHeight("85vh");

        var addMachineForm = getConfiguredAddMachineForm(employeeFormModel);
        var machineManagerGrid = getConfiguredMachineManagerGrid(employeeFormModel, addMachineForm);

        machineManagerGrid.setSizeFull();

        // i18n same as QualificationManagerDialog, update if needed
        var closeButton = new Button(getTranslation("qualificationManagerDialog.closeButton"), event -> close());
        closeButton.addClickShortcut(Key.ESCAPE);

        var layout = new VerticalLayout(machineManagerGrid, addMachineForm, closeButton);
        layout.setSizeFull();
        layout.setSpacing(true);
        layout.setPadding(true);

        add(layout);
    }

    private AddMachineForm getConfiguredAddMachineForm(EmployeeFormModel employeeFormModel) {

        var addMachineForm = new AddMachineForm(employeeFormModel);
        addMachineForm.addUpdateMachineListener(event -> {
            fireEvent(new UpdateMachineEvent(this, event.getEmployeeDTO()));
        });

        return addMachineForm;
    }

    private MachineMangerGrid getConfiguredMachineManagerGrid(
            EmployeeFormModel employeeFormModel, AddMachineForm addMachineForm) {
        var machineManagerGrid = new MachineMangerGrid(employeeFormModel, addMachineForm);

        return machineManagerGrid;
    }

    public abstract static class MachineManagerDialogEvent extends ComponentEvent<MachineManagerDialog> {
        public MachineManagerDialogEvent(MachineManagerDialog source) {
            super(source, false);
        }
    }

    public static class UpdateMachineEvent extends MachineManagerDialogEvent {
        @Getter
        private final EmployeeDTO employeeDTO;

        public UpdateMachineEvent(MachineManagerDialog source, EmployeeDTO employeeDTO) {
            super(source);
            this.employeeDTO = employeeDTO;
        }
    }

    public Registration addUpdateMachineListener(ComponentEventListener<UpdateMachineEvent> listener) {
        return addListener(UpdateMachineEvent.class, listener);
    }
}
