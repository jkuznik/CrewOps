package pl.crewops.component.dialog.qualificationManager;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import lombok.extern.slf4j.Slf4j;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.EmployeeFormModel;
import pl.crewops.model.QualificationFormModel;
import pl.crewops.util.SpringContextBridge;

@Slf4j
public class QualificationManagerGrid extends VerticalLayout {
    private final Grid<QualificationFormModel> grid = new Grid<>();
    private final EditQualificationDialog editQualificationDialog = new EditQualificationDialog();

    public QualificationManagerGrid(EmployeeFormModel employeeFormModel, AddQualificationForm addQualificationForm) {
        addClassName("qualification-manager-grid");

        setSizeFull();

        var coreAPI = SpringContextBridge.getBean(CoreAPI.class);

        configureGrid(employeeFormModel);

        populateGrid(employeeFormModel, coreAPI);

        addQualificationForm.addUpdateQualificationsListener(e -> {
            updateGrid(e.getEmployeeDTO(), coreAPI);
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
            if (e.getValue() != null && e.isFromClient()) {
                editQualificationDialog.setEmployeeFormModel(employeeFormModel);
                editQualificationDialog.setQualificationFormModel(e.getValue());
                editQualificationDialog.open();
            }
        });
    }

    private void populateGrid(EmployeeFormModel employeeFormModel, CoreAPI coreAPI) {
        try {
            var qualifications =
                    coreAPI.getAllQualificationsWithExpirationTimeByEmployeeId(employeeFormModel.getId()).stream()
                            .map(QualificationFormModel::toQualificationFormModel)
                            .toList();
            grid.setItems(qualifications);

        } catch (NotAuthenticatedException e) {
            log.info("Not authenticated");
            // TODO: implement
        }
    }

    private void updateGrid(EmployeeDTO employeeDTO, CoreAPI coreAPI) {
        try {
            var qualifications = coreAPI.getAllQualificationsWithExpirationTimeByEmployeeId(employeeDTO.id()).stream()
                    .map(QualificationFormModel::toQualificationFormModel)
                    .toList();
            grid.setItems(qualifications);
        } catch (NotAuthenticatedException e) {
            log.info("Not authenticated");
        }
    }
}
