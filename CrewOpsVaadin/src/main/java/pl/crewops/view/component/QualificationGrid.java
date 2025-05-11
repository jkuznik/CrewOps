package pl.crewops.view.component;

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
import pl.crewops.dto.qualification.QualificationDTO;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.view.HomeView;
import pl.crewops.view.component.notification.QualificationAlreadyExistNotification;
import pl.crewops.view.component.notification.UpdateQualificationNotification;
import pl.crewops.view.form.QualificationForm;
import pl.crewops.view.form.model.QualificationFormModel;

public class QualificationGrid extends VerticalLayout {
    private final CoreAPI coreAPI;

    private final Grid<QualificationFormModel> grid = new Grid<>(QualificationFormModel.class);
    private final TextField filter = new TextField();
    private final QualificationForm form = new QualificationForm();

    private List<QualificationFormModel> employees = new ArrayList<>();

    public QualificationGrid(CoreAPI coreAPI) {
        this.coreAPI = coreAPI;

        configureGrid();
        configureForm();

        updateGrid();
        closeEditor();

        setSizeFull();
        add(getToolbar(), getContent());
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

        filter.setPlaceholder("Filter by name");
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(event -> updateGrid());

        Button addQualification = new Button("Add qualification");
        addQualification.addClickListener(event -> addQualification());

        toolbar.add(filter, addQualification);

        return toolbar;
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.setColumns("description", "employeesAmount");
        grid.getColumns().forEach(column -> column.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event -> {
            editQualification(event.getValue());
        });
    }

    private void configureForm() {
        form.setWidth("25em");

        form.addSaveListener(this::saveQualification);
        form.addUpdateListener(this::updateQualification);
        form.addDeleteListener(this::deleteQualification);
        form.addCloseListener(event -> {
            closeEditor();
        });
    }

    private void updateGrid() {
        try {
            employees = coreAPI.getAllQualifications().stream()
                    .map(QualificationFormModel::toQualificationFormModel)
                    .toList();

            if (filter.getValue() == null) {
                grid.setItems(employees);
            } else {
                grid.setItems(employees.stream()
                        .filter(qualificationDTO -> qualificationDTO
                                .getDescription()
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
            addClassName("editing");
        }
    }

    private void addQualification() {
        grid.asSingleSelect().clear();
        form.setFormModeSave();
        form.setVisible(true);
    }

    private void saveQualification(QualificationForm.SaveEvent event) {
        if (employees.stream().anyMatch(qualification -> qualification
                .getDescription()
                .equals(event.getQualification().getDescription()))) {
            new QualificationAlreadyExistNotification(event.getQualification().getDescription());
            closeEditor();
            return;
        }

        try {
            coreAPI.createQualification(QualificationFormModel.toCreateQualificationDTO(event.getQualification()));
            updateGrid();
            closeEditor();
        } catch (NotAuthenticatedException e) {
            UI.getCurrent().navigate(HomeView.class);
        }
    }

    private void updateQualification(QualificationForm.UpdateEvent event) {
        try {
            Optional<QualificationDTO> qualificationDTO = coreAPI.updateQualification(
                    QualificationFormModel.toUpdateQualificationDTO(event.getQualification()));
            updateGrid();
            closeEditor();
            qualificationDTO.ifPresent(UpdateQualificationNotification::new);
        } catch (NotAuthenticatedException e) {
            UI.getCurrent().navigate(HomeView.class);
        }
    }

    private void deleteQualification(QualificationForm.DeleteEvent event) {
        try {
            coreAPI.deleteQualification(event.getQualification().getId());
            updateGrid();
            closeEditor();
        } catch (NotAuthenticatedException e) {
            UI.getCurrent().navigate(HomeView.class);
        }
    }
}
