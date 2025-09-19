package pl.crewops.component.dialog.qualificationManager;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;
import pl.crewops.model.EmployeeFormModel;
import pl.crewops.model.QualificationFormModel;
import pl.crewops.model.dto.employee.EmployeeDTO;

public class EditQualificationDialog extends Dialog {
    private final EditQualificationForm editQualificationForm = new EditQualificationForm();

    public EditQualificationDialog() {
        addClassName("edit-qualification-dialog");

        setModal(true);
        setDraggable(true);
        setResizable(true);
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);

        configureEditQualificationForm();

        add(editQualificationForm);
    }

    // this setters has responsibility to pass values from parent component to child component
    // this approach is chosen instead of creating each time new object to handle required operations
    public void setEmployeeFormModel(EmployeeFormModel employeeFormModel) {
        editQualificationForm.setEmployeeFormModel(employeeFormModel);
    }

    // this setters has responsibility to pass values from parent component to child component
    // this approach is chosen instead of creating each time new object to handle required operations
    public void setQualificationFormModel(QualificationFormModel qualificationFormModel) {
        editQualificationForm.setQualificationFormModel(qualificationFormModel);
    }

    private EditQualificationForm configureEditQualificationForm() {
        editQualificationForm.addUpdateEventListener(event -> {
            fireEvent(new UpdateEvent(this, event.getEmployeeDTO()));
            close();
        });
        editQualificationForm.addCancelEventListener(event -> {
            close();
        });
        return editQualificationForm;
    }

    public abstract static class EditQualificationDialogEvent extends ComponentEvent<EditQualificationDialog> {

        public EditQualificationDialogEvent(EditQualificationDialog source) {
            super(source, false);
        }
    }

    public static class UpdateEvent extends EditQualificationDialogEvent {
        @Getter
        private final EmployeeDTO employeeDTO;

        public UpdateEvent(EditQualificationDialog source, EmployeeDTO employeeDTO) {
            super(source);
            this.employeeDTO = employeeDTO;
        }
    }

    public Registration addUpdateEventListener(ComponentEventListener<UpdateEvent> listener) {
        return addListener(UpdateEvent.class, listener);
    }
}
