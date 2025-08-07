package pl.crewops.component.dialog.qualificationManager;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.shared.Registration;
import pl.crewops.model.EmployeeFormModel;
import pl.crewops.model.QualificationFormModel;

public class EditQualificationDialog extends Dialog {
    private final EditQualificationForm editQualificationForm = new EditQualificationForm();
    private EmployeeFormModel employeeFormModel;
    private QualificationFormModel qualificationFormModel;

    public EditQualificationDialog() {
        addClassName("edit-qualification-dialog");

        setModal(true);
        setDraggable(true);
        setResizable(true);
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);

        configureEditQualificationForm(employeeFormModel, qualificationFormModel);

        add(editQualificationForm);
    }

    public void setEmployeeFormModel(EmployeeFormModel employeeFormModel) {
        this.employeeFormModel = employeeFormModel;
        editQualificationForm.setEmployeeFormModel(employeeFormModel);
    }

    public void setQualificationFormModel(QualificationFormModel qualificationFormModel) {
        this.qualificationFormModel = qualificationFormModel;
        editQualificationForm.setQualificationFormModel(qualificationFormModel);
    }

    private EditQualificationForm configureEditQualificationForm(
            EmployeeFormModel employeeFormModel, QualificationFormModel qualificationFormModel) {
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
        public UpdateEvent(EditQualificationDialog source) {
            super(source);
        }
    }

    public Registration addUpdateEventListener(ComponentEventListener<UpdateEvent> listener) {
        return addListener(UpdateEvent.class, listener);
    }
}
