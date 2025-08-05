package pl.crewops.view.component.dialog;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import pl.crewops.model.EmployeeFormModel;
import pl.crewops.view.component.grid.QualificationManagerGrid;

public class QualificationsManagerDialog extends Dialog {

    public QualificationsManagerDialog(EmployeeFormModel employeeFormModel) {
        addClassName("qualifications-manager-dialog");

        setModal(true);
        setDraggable(true);
        setResizable(true);
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        setWidth("95vw");
        setHeight("85vh");

        QualificationManagerGrid grid = new QualificationManagerGrid(employeeFormModel);
        grid.setSizeFull();

        // TODO: i18n
        Button closeButton = new Button("Close", event -> close());

        VerticalLayout layout = new VerticalLayout(grid, closeButton);
        layout.setSizeFull();
        layout.setSpacing(true);
        layout.setPadding(true);

        add(layout);

        open();
    }
}
