package pl.crewops.ui.component.dialog.departmentDialog;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;
import pl.crewops.model.EmployeeFormModel;
import pl.crewops.model.dto.employee.EmployeeDTO;

public class DepartmentManagerDialog extends Dialog {

    public DepartmentManagerDialog(EmployeeFormModel employeeFormModel) {
        addClassName("departmentManagerDialog");

        setModal(true);
        setDraggable(true);
        setResizable(true);
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);

        setWidth("95vw");
        setHeight("85vh");

        var addDepartmentForm = getConfiguredAddDepartmentForm(employeeFormModel);
        var departmentManagerGrid = getConfiguredDepartmentManagerGrid(employeeFormModel, addDepartmentForm);

        departmentManagerGrid.setSizeFull();

        // i18n same as QualificationManagerDialog, update if needed
        var closeButton = new Button(getTranslation("qualificationManagerDialog.closeButton"), event -> close());
        closeButton.addClickShortcut(Key.ESCAPE);

        var layout = new VerticalLayout(departmentManagerGrid, addDepartmentForm, closeButton);
        layout.setSizeFull();
        layout.setSpacing(true);
        layout.setPadding(true);

        add(layout);
    }

    private AddDepartmentForm getConfiguredAddDepartmentForm(EmployeeFormModel employeeFormModel) {

        var addDepartmentForm = new AddDepartmentForm(employeeFormModel);
        addDepartmentForm.addUpdateDepartmentListener(event -> {
            fireEvent(new UpdateDepartmentEvent(this, event.getEmployeeDTO()));
        });

        return addDepartmentForm;
    }

    private DepartmentManagerGrid getConfiguredDepartmentManagerGrid(
            EmployeeFormModel employeeFormModel, AddDepartmentForm addDepartmentForm) {
        var departmentManagerGrid = new DepartmentManagerGrid(employeeFormModel, addDepartmentForm);
        departmentManagerGrid.addUpdateDepartmentEvent(event -> {
            fireEvent(new UpdateDepartmentEvent(this, event.getEmployeeDTO()));
        });

        return departmentManagerGrid;
    }

    public abstract static class DepartmentManagerDialogEvent extends ComponentEvent<DepartmentManagerDialog> {
        public DepartmentManagerDialogEvent(DepartmentManagerDialog source) {
            super(source, false);
        }
    }

    public static class UpdateDepartmentEvent extends DepartmentManagerDialogEvent {
        @Getter
        private final EmployeeDTO employeeDTO;

        public UpdateDepartmentEvent(DepartmentManagerDialog source, EmployeeDTO employeeDTO) {
            super(source);
            this.employeeDTO = employeeDTO;
        }
    }

    public Registration addUpdateDepartmentListener(ComponentEventListener<UpdateDepartmentEvent> listener) {
        return addListener(UpdateDepartmentEvent.class, listener);
    }
}
