package pl.crewops.ui.component.dialog.qualificationManager;

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

public class QualificationsManagerDialog extends Dialog {

    public QualificationsManagerDialog(EmployeeFormModel employeeFormModel) {
        addClassName("qualifications-manager-dialog");

        setModal(true);
        setDraggable(true);
        setResizable(true);
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);

        setWidth("95vw");
        setHeight("85vh");

        var addQualificationForm = getConfiguredAddQualificationForm(employeeFormModel);
        var qualificationManagerGrid = getConfiguredQualificationManagerGrid(employeeFormModel, addQualificationForm);

        qualificationManagerGrid.setSizeFull();

        var layout = new VerticalLayout(qualificationManagerGrid, addQualificationForm);
        layout.setSizeFull();
        layout.setSpacing(true);
        layout.setPadding(true);

        Button closeButton = new Button(getTranslation("qualificationManagerDialog.closeButton"), event -> close());
        closeButton.addClickShortcut(Key.ESCAPE);

        add(layout);

        getFooter().add(closeButton);
    }

    private AddQualificationForm getConfiguredAddQualificationForm(EmployeeFormModel employeeFormModel) {
        var addQualificationForm = new AddQualificationForm(employeeFormModel);

        addQualificationForm.addUpdateQualificationsListener(event -> {
            fireEvent(new UpdateQualificationsEvent(this, event.getEmployeeDTO()));
        });

        return addQualificationForm;
    }

    private QualificationManagerGrid getConfiguredQualificationManagerGrid(
            EmployeeFormModel employeeFormModel, AddQualificationForm addQualificationForm) {
        var qualificationManagerGrid = new QualificationManagerGrid(employeeFormModel, addQualificationForm);
        qualificationManagerGrid.addUpdateQualificationListener(event -> {
            fireEvent(new UpdateQualificationsEvent(this, event.getEmployeeDTO()));
        });

        return qualificationManagerGrid;
    }

    public abstract static class QualificationsManagerDialogEvent extends ComponentEvent<QualificationsManagerDialog> {
        public QualificationsManagerDialogEvent(QualificationsManagerDialog source) {
            super(source, false);
        }
    }

    public static class UpdateQualificationsEvent extends QualificationsManagerDialogEvent {
        @Getter
        private final EmployeeDTO employeeDTO;

        public UpdateQualificationsEvent(QualificationsManagerDialog source, EmployeeDTO employeeDTO) {
            super(source);
            this.employeeDTO = employeeDTO;
        }
    }

    public Registration addUpdateQualificationsListener(ComponentEventListener<UpdateQualificationsEvent> listener) {
        return addListener(UpdateQualificationsEvent.class, listener);
    }
}
