package pl.crewops.component.dialog;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import pl.crewops.component.form.EmployeeQualificationForm;
import pl.crewops.component.grid.QualificationManagerGrid;
import pl.crewops.model.EmployeeFormModel;

public class QualificationsManagerDialog extends Dialog {

    // TODO: looks like works fine but need to trigger update parent grids (employee grid and qualification grid)
    public QualificationsManagerDialog(EmployeeFormModel employeeFormModel) {
        addClassName("qualifications-manager-dialog");

        setModal(true);
        setDraggable(true);
        setResizable(true);
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        setWidth("95vw");
        setHeight("85vh");

        EmployeeQualificationForm employeeQualificationForm = new EmployeeQualificationForm(employeeFormModel);
        QualificationManagerGrid grid = new QualificationManagerGrid(employeeFormModel, employeeQualificationForm);
        grid.setSizeFull();

        // TODO: i18n
        Button closeButton = new Button("Close", event -> close());

        VerticalLayout layout = new VerticalLayout(grid, employeeQualificationForm, closeButton);
        layout.setSizeFull();
        layout.setSpacing(true);
        layout.setPadding(true);

        add(layout);

        open();
    }
}
