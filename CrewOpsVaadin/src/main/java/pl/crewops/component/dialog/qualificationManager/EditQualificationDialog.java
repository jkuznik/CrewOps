package pl.crewops.component.dialog.qualificationManager;

import com.vaadin.flow.component.dialog.Dialog;
import pl.crewops.model.EmployeeFormModel;
import pl.crewops.model.QualificationFormModel;

public class EditQualificationDialog extends Dialog {

    public EditQualificationDialog(EmployeeFormModel employeeFormModel, QualificationFormModel qualificationFormModel) {
        addClassName("edit-qualification-dialog");

        setModal(true);
        setDraggable(true);
        setResizable(true);
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);

        var editQualificationForm = getConfiguredEditQualificationForm(employeeFormModel, qualificationFormModel);

        add(editQualificationForm);

        open();
    }

    private EditQualificationForm getConfiguredEditQualificationForm(
            EmployeeFormModel employeeFormModel, QualificationFormModel qualificationFormModel) {
        var editQualificationForm = new EditQualificationForm(employeeFormModel, qualificationFormModel);
        editQualificationForm.addCancelListener(event -> close());
        return editQualificationForm;
    }
}
