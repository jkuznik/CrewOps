package pl.crewops.component.grid;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import pl.crewops.component.form.QualificationForm;
import pl.crewops.component.notification.FailNotification;
import pl.crewops.component.notification.InfoNotification;
import pl.crewops.component.notification.SuccessNotification;
import pl.crewops.component.notification.guardian.DeleteQualificationGuardian;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.QualificationFormModel;
import pl.crewops.model.dto.qualification.QualificationDTO;
import pl.crewops.util.BrowserResolver;
import pl.crewops.view.HomeView;

@Getter
@Setter
public class QualificationGrid extends VerticalLayout {
    private final CoreAPI coreAPI;

    private final TextField filter = new TextField();
    private final Button addQualification = new Button();
    private final HorizontalLayout gridToolbar = getToolbar();

    private final Grid<QualificationFormModel> grid = new Grid<>();
    private final QualificationForm form = new QualificationForm();
    private EmployeeGrid employeeGrid;

    private List<QualificationFormModel> qualifications = new ArrayList<>();

    public QualificationGrid(CoreAPI coreAPI) {
        this.coreAPI = coreAPI;

        configureGrid();
        configureForm();

        localize();

        updateGrid();
        closeEditor();

        setSizeFull();
        add(gridToolbar, getContent());
    }

    private void localize() {
        filter.setPlaceholder(getTranslation("qualificationGrid.filter.placeholder"));

        addQualification.setText(getTranslation("qualificationGrid.button.addQualification"));

        grid.getColumnByKey("description").setHeader(getTranslation("qualificationGrid.column.description"));
        grid.getColumnByKey("employeesAmount").setHeader(getTranslation("qualificationGrid.column.employeesAmount"));
    }

    public void closeEditor() {
        form.setQualification(null);
        form.setVisible(false);
    }

    private HorizontalLayout getContent() {
        var content = new HorizontalLayout(grid, form);
        content.setSizeFull();
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        return content;
    }

    private HorizontalLayout getToolbar() {
        var toolbar = new HorizontalLayout();

        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(event -> updateGrid());

        addQualification.addClickListener(event -> addQualification());

        toolbar.add(filter, addQualification);
        return toolbar;
    }

    private void configureGrid() {
        grid.setSizeFull();

        grid.addColumn(QualificationFormModel::getDescription).setKey("description");
        grid.addColumn(QualificationFormModel::getEmployeesAmount).setKey("employeesAmount");

        grid.getColumns().forEach(column -> column.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event -> editQualification(event.getValue()));
    }

    private void configureForm() {
        if (BrowserResolver.isMobile()) {
            form.setWidthFull();
        } else {
            form.setWidth("25em");
        }

        form.addSaveListener(this::saveQualification);
        form.addUpdateListener(this::updateQualification);
        form.addDeleteListener(this::deleteQualification);

        form.addCloseListener(event -> {
            closeEditor();

            if (BrowserResolver.isMobile()) {
                grid.setVisible(true);
                gridToolbar.setVisible(true);
            }
        });
    }

    public void updateGrid() {
        try {
            qualifications = coreAPI.getAllQualifications().stream()
                    .map(QualificationFormModel::toQualificationFormModel)
                    .toList();

            if (filter.getValue() == null || filter.getValue().isBlank()) {
                grid.setItems(qualifications);
            } else {
                grid.setItems(qualifications.stream()
                        .filter(q -> q.getDescription()
                                .toLowerCase()
                                .contains(filter.getValue().toLowerCase()))
                        .toList());
            }
        } catch (NotAuthenticatedException e) {
            UI.getCurrent().navigate(HomeView.class);
        }
    }

    private void editQualification(QualificationFormModel qualificationFormModel) {
        if (qualificationFormModel == null) {
            closeEditor();
        } else {
            form.setQualification(qualificationFormModel);
            form.setFormModeUpdate();
            form.setVisible(true);

            if (BrowserResolver.isMobile()) {
                grid.setVisible(false);
                gridToolbar.setVisible(false);
                form.setWidthFull();
            }
        }
    }

    private void addQualification() {
        grid.asSingleSelect().clear();
        form.setFormModeSave();
        form.setVisible(true);

        if (BrowserResolver.isMobile()) {
            grid.setVisible(false);
            gridToolbar.setVisible(false);
            form.setWidthFull();
        }
    }

    private void saveQualification(QualificationForm.SaveEvent event) {
        if (qualifications.stream().anyMatch(q -> q.getDescription()
                .equalsIgnoreCase(event.getQualification().getDescription()))) {
            notification(event);
            closeEditor();
            return;
        }

        try {
            Optional<QualificationDTO> qualificationDTO = coreAPI.createQualification(
                    QualificationFormModel.toCreateQualificationDTO(event.getQualification()));
            updateGrid();
            closeEditor();
            qualificationDTO.ifPresent(value -> new SuccessNotification(
                    getTranslation("addQualificationNotification.successAddQualification") + value.description()));
        } catch (NotAuthenticatedException e) {
            UI.getCurrent().navigate(HomeView.class);
        }
    }

    private void notification(QualificationForm.SaveEvent event) {
        new FailNotification(getTranslation("qualificationAlreadyExistNotification.messagePrefix")
                + event.getQualification().getDescription()
                + getTranslation("qualificationAlreadyExistNotification.messageSuffix"));
    }

    private void updateQualification(QualificationForm.UpdateEvent event) {
        try {
            Optional<QualificationDTO> qualificationDTO = coreAPI.updateQualification(
                    QualificationFormModel.toUpdateQualificationDTO(event.getQualification()));
            updateGrid();
            closeEditor();
            qualificationDTO.ifPresent(value -> new SuccessNotification(
                    getTranslation("updateQualificationNotification.messagePrefix", value.description())));
        } catch (NotAuthenticatedException e) {
            UI.getCurrent().navigate(HomeView.class);
        }
    }

    private void deleteQualification(QualificationForm.DeleteEvent event) {
        var qualification = event.getQualification();

        if (qualification.getEmployeesAmount() > 0) {
            deleteQualificationWithGuardian(event, qualification);
        } else {
            executeDelete(event);
        }
    }

    private void deleteQualificationWithGuardian(
            QualificationForm.DeleteEvent event, QualificationFormModel qualification) {
        new DeleteQualificationGuardian(qualification, () -> executeDelete(event));
    }

    private void executeDelete(QualificationForm.DeleteEvent event) {
        try {
            coreAPI.deleteQualification(event.getQualification().getId());
            updateGrid();
            employeeGrid.updateGrid();
            new InfoNotification(getTranslation(
                    "qualificationGrid.deleteQualification",
                    event.getQualification().getDescription()));
            closeEditor();
        } catch (NotAuthenticatedException e) {
            UI.getCurrent().navigate(HomeView.class);
        }
    }
}
