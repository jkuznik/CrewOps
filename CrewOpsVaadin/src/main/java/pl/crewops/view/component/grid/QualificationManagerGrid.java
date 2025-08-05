package pl.crewops.view.component.grid;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import lombok.extern.slf4j.Slf4j;
import pl.crewops.model.EmployeeFormModel;
import pl.crewops.model.QualificationFormModel;

@Slf4j
public class QualificationManagerGrid extends VerticalLayout {
    private final H1 employeeNameHolder = new H1();
    private final Grid<QualificationFormModel> grid = new Grid<>();

    public QualificationManagerGrid(EmployeeFormModel employeeFormModel) {
        addClassName("qualification-manager-grid");

        setSizeFull();

        configureGrid();

        updateGrid(employeeFormModel);

        employeeNameHolder.setText(employeeFormModel.getFirstName() + " " + employeeFormModel.getLastName());
        add(employeeNameHolder, grid);
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.setMinWidth("300px");
        grid.setMaxWidth("100%");
        grid.addClassName("qualification-grid");

        grid.addColumn(new ComponentRenderer<>(qualification -> {
                    Div descriptionDiv = new Div();
                    descriptionDiv.setText(qualification.getDescription());
                    descriptionDiv
                            .getStyle()
                            .set("white-space", "normal")
                            .set("overflow-wrap", "break-word")
                            .set("font-size", "0.9rem");
                    return descriptionDiv;
                }))
                .setHeader("Description")
                .setKey("description")
                .setFlexGrow(3) // Fill available space
                .setAutoWidth(false) // Allow wrapping
                .setResizable(true); // Optional

        grid.addColumn(QualificationFormModel::getExpiredAt)
                .setHeader("Expires")
                .setKey("expiredAt")
                .setAutoWidth(true)
                .setFlexGrow(1)
                .setResizable(true);
    }

    private void updateGrid(EmployeeFormModel employeeFormModel) {
        var qualifications = employeeFormModel.getQualificationsSet().stream()
                .map(QualificationFormModel::toQualificationFormModel)
                .toList();

        grid.setItems(qualifications);
    }
}
