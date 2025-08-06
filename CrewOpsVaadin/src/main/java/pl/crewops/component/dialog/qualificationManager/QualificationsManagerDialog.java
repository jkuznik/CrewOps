package pl.crewops.component.dialog.qualificationManager;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.model.EmployeeFormModel;

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

        var employeeQualificationForm = getConfiguredEmployeeQualificationForm(employeeFormModel);
        var qualificationManagerGrid = new QualificationManagerGrid(employeeFormModel, employeeQualificationForm);

        qualificationManagerGrid.setSizeFull();

        // TODO: i18n
        Button closeButton = new Button("Close", event -> close());

        VerticalLayout layout = new VerticalLayout(qualificationManagerGrid, employeeQualificationForm, closeButton);
        layout.setSizeFull();
        layout.setSpacing(true);
        layout.setPadding(true);

        add(layout);
    }

    private AddQualificationForm getConfiguredEmployeeQualificationForm(EmployeeFormModel employeeFormModel) {
        var employeeQualificationForm = new AddQualificationForm(employeeFormModel);

        employeeQualificationForm.addUpdateQualificationsListener(event -> {
            fireEvent(new UpdateQualificationsEvent(this, event.getEmployeeDTO()));
        });

        return employeeQualificationForm;
    }

    public abstract static class QualificationsManagerDialogEvent extends ComponentEvent<QualificationsManagerDialog> {
        @Getter
        private final EmployeeDTO employeeDTO;

        public QualificationsManagerDialogEvent(QualificationsManagerDialog source, EmployeeDTO employeeDTO) {
            super(source, false);
            this.employeeDTO = employeeDTO;
        }
    }

    public static class UpdateQualificationsEvent extends QualificationsManagerDialogEvent {
        public UpdateQualificationsEvent(QualificationsManagerDialog source, EmployeeDTO employeeDTO) {
            super(source, employeeDTO);
        }
    }

    public Registration addUpdateQualificationsListener(ComponentEventListener<UpdateQualificationsEvent> listener) {
        return addListener(UpdateQualificationsEvent.class, listener);
    }
}
