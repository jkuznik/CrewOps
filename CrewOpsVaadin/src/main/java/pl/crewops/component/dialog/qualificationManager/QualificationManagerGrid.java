package pl.crewops.component.dialog.qualificationManager;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import lombok.extern.slf4j.Slf4j;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.model.EmployeeFormModel;
import pl.crewops.model.QualificationFormModel;

@Slf4j
public class QualificationManagerGrid extends VerticalLayout {
    private final Grid<QualificationFormModel> grid = new Grid<>();

    public QualificationManagerGrid(EmployeeFormModel employeeFormModel, AddQualificationForm addQualificationForm) {
        addClassName("qualification-manager-grid");

        setSizeFull();

        configureGrid(employeeFormModel);

        populateGrid(employeeFormModel);

        addQualificationForm.addUpdateQualificationsListener(e -> {
            updateGrid(e.getEmployeeDTO());
        });

        // TODO: i18n
        H1 employeeNameHolder = new H1();
        employeeNameHolder.setText(employeeFormModel.getFirstName() + " " + employeeFormModel.getLastName() + " - "
                + "kwalifikacje i uprawnienia");

        add(employeeNameHolder, grid);
    }

    private void configureGrid(EmployeeFormModel employeeFormModel) {
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
                .setFlexGrow(3)
                .setAutoWidth(false)
                .setResizable(true);

        grid.addColumn(QualificationFormModel::getExpiredAt)
                .setHeader("Expires")
                .setKey("expiredAt")
                .setAutoWidth(true)
                .setFlexGrow(1)
                .setResizable(true);

        grid.asSingleSelect().addValueChangeListener(e -> {
            new EditQualificationDialog(employeeFormModel, e.getValue());
        });
    }

    private void populateGrid(EmployeeFormModel employeeFormModel) {
        var qualifications = employeeFormModel.getQualificationsSet().stream()
                .map(QualificationFormModel::toQualificationFormModel)
                .toList();

        grid.setItems(qualifications);
    }

    private void updateGrid(EmployeeDTO employeeDTO) {
        var qualifications = employeeDTO.qualifications().stream()
                .map(QualificationFormModel::toQualificationFormModel)
                .toList();

        grid.setItems(qualifications);
    }
}
