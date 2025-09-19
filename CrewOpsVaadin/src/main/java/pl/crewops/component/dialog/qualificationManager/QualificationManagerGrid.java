package pl.crewops.component.dialog.qualificationManager;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.shared.Registration;
import java.util.UUID;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import pl.crewops.component.notification.FailNotification;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.EmployeeFormModel;
import pl.crewops.model.QualificationFormModel;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.util.SpringContextBridge;

@Slf4j
public class QualificationManagerGrid extends VerticalLayout {
    private final Grid<QualificationFormModel> grid = new Grid<>();
    private final EditQualificationDialog editQualificationDialog = new EditQualificationDialog();
    private UUID employeeId;

    public QualificationManagerGrid(EmployeeFormModel employeeFormModel, AddQualificationForm addQualificationForm) {
        addClassName("qualification-manager-grid");

        setSizeFull();

        var coreAPI = SpringContextBridge.getBean(CoreAPI.class);
        employeeId = employeeFormModel.getId();

        configureGrid(employeeFormModel);

        populateGrid(employeeFormModel, coreAPI);

        editQualificationDialog.addUpdateEventListener(event -> {
            updateGrid(event.getEmployeeDTO(), coreAPI);
            fireEvent(new UpdateEvent(this, event.getEmployeeDTO()));
        });

        addQualificationForm.addUpdateQualificationsListener(e -> {
            updateGrid(e.getEmployeeDTO(), coreAPI);
        });

        H1 employeeNameHolder = new H1();
        employeeNameHolder.setText(employeeFormModel.getFirstName() + " " + employeeFormModel.getLastName() + " - "
                + getTranslation("qualificationManagerGrid.employeeNameHolder"));

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
                .setHeader(getTranslation("qualificationManagerGrid.description"))
                .setKey("description")
                .setFlexGrow(3)
                .setAutoWidth(false)
                .setResizable(true);

        grid.addColumn(QualificationFormModel::getExpiredAt)
                .setHeader(getTranslation("qualificationManagerGrid.expires"))
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
            new FailNotification(e.getMessage());
        }
    }

    private void updateGrid(EmployeeDTO employeeDTO, CoreAPI coreAPI) {
        try {
            if (employeeDTO != null) {
                employeeId = employeeDTO.id();
            }
            var qualifications = coreAPI.getAllQualificationsWithExpirationTimeByEmployeeId(employeeId).stream()
                    .map(QualificationFormModel::toQualificationFormModel)
                    .toList();
            grid.setItems(qualifications);
        } catch (NotAuthenticatedException e) {
            new FailNotification(e.getMessage());
        }
    }

    public abstract static class QualificationManagerGridEvent extends ComponentEvent<QualificationManagerGrid> {
        public QualificationManagerGridEvent(QualificationManagerGrid source) {
            super(source, false);
        }
    }

    public static class UpdateEvent extends QualificationManagerGridEvent {
        @Getter
        private final EmployeeDTO employeeDTO;

        UpdateEvent(QualificationManagerGrid qualificationManagerGrid, EmployeeDTO employeeDTO) {
            super(qualificationManagerGrid);
            this.employeeDTO = employeeDTO;
        }
    }

    public Registration addUpdateQualificationListener(ComponentEventListener<UpdateEvent> listener) {
        return addListener(UpdateEvent.class, listener);
    }
}
