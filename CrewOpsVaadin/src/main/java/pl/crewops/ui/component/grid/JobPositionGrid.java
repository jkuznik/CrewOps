package pl.crewops.ui.component.grid;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import java.util.List;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.JobPositionFormModel;
import pl.crewops.model.dto.jobPosition.CreateJobPositionDTO;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;
import pl.crewops.model.dto.jobPosition.UpdateJobPositionDTO;
import pl.crewops.ui.component.form.JobPositionForm;
import pl.crewops.ui.component.notification.FailNotification;
import pl.crewops.ui.component.notification.InfoNotification;
import pl.crewops.ui.component.notification.NotAuthenticatedNotification;
import pl.crewops.ui.component.notification.SuccessNotification;
import pl.crewops.util.AuthenticationResolver;
import pl.crewops.util.BrowserResolver;

public class JobPositionGrid extends VerticalLayout {

    // todo znalezc przyczyne ładnego przejscie z update mode na save mode w employee form i zastosowac w pozostalych
    // formularzach tak samo
    private final CoreAPI coreAPI;
    private final AuthenticationResolver authenticationResolver;

    private final TextField nameFilter = new TextField();
    private final TextField machineFilter = new TextField();
    private final Button addJobPosition = new Button();

    private final HorizontalLayout gridToolbar = getToolbar();

    private final Grid<JobPositionFormModel> grid = new Grid<>();
    private final JobPositionForm form = new JobPositionForm();

    public JobPositionGrid(CoreAPI coreAPI, AuthenticationResolver authenticationResolver) {
        this.coreAPI = coreAPI;
        this.authenticationResolver = authenticationResolver;

        setSizeFull();

        configureGrid();
        configureForm();

        localize();

        add(gridToolbar, getContent());
    }

    public void updateGrid() {
        try {
            List<JobPositionFormModel> list = coreAPI.getAllJobPositions().stream()
                    .map(JobPositionFormModel::toFormModel)
                    .toList();

            String nameFilterValue =
                    nameFilter.getValue() != null ? nameFilter.getValue().toLowerCase() : "";
            String machineFilterValue = machineFilter.getValue();

            List<JobPositionFormModel> filteredList = list.stream()
                    .filter(position -> {
                        boolean nameMatches = nameFilterValue.isBlank()
                                || position.getName().toLowerCase().contains(nameFilterValue);

                        boolean machineMatches = true;
                        if (machineFilterValue != null && !machineFilterValue.isBlank()) {
                            if (position.getMachine() != null) {
                                machineMatches = position.getMachine()
                                        .registerNumber()
                                        .toLowerCase()
                                        .contains(nameFilterValue);
                            } else {
                                machineMatches = false;
                            }
                        }

                        return nameMatches && machineMatches;
                    })
                    .toList();

            grid.setItems(filteredList);
        } catch (NotAuthenticatedException e) {
            new NotAuthenticatedNotification(e.getMessage());
        }
    }

    private void configureGrid() {
        grid.setSizeFull();

        grid.addColumn(JobPositionFormModel::getName).setKey("name");

        grid.addColumn(model -> model.getMachine() != null
                        ? model.getMachine().machineType().name() + " "
                                + model.getMachine().registerNumber()
                        : "-")
                .setKey("machine");

        grid.asSingleSelect().addValueChangeListener(e -> {
            form.setFormModeUpdate();
            form.setBean(e.getValue());
            form.setVisible(true);
        });

        updateGrid();
    }

    private void configureForm() {
        if (BrowserResolver.isMobile()) {
            form.setWidthFull();
        } else {
            form.setWidth("25em");
        }

        form.setVisible(false);
        form.addCreateEventListener(event -> {
            try {
                var createJobPosition = CreateJobPositionDTO.builder()
                        .name(event.getModel().getName())
                        .machineDTO(event.getModel().getMachine())
                        .build();

                JobPositionDTO jobPositionDTO = coreAPI.createJobPosition(createJobPosition);
                if (jobPositionDTO != null) {
                    new SuccessNotification(getTranslation("jobPosition.create.success"));
                    updateGrid();
                } else {
                    new FailNotification(getTranslation("jobPosition.create.fail"));
                }
            } catch (NotAuthenticatedException e) {
                new NotAuthenticatedNotification(e.getMessage());
            }
        });

        form.addUpdateEventListener(event -> {
            try {
                var updateJobPosition = UpdateJobPositionDTO.builder()
                        .id(event.getModel().getId())
                        .name(event.getModel().getName())
                        .machineDTO(event.getModel().getMachine())
                        .build();

                JobPositionDTO jobPositionDTO = coreAPI.updateJobPosition(updateJobPosition);
                if (jobPositionDTO != null) {
                    new SuccessNotification(getTranslation("jobPosition.update.success"));
                    updateGrid();
                } else {
                    new FailNotification(getTranslation("failNotification"));
                }
            } catch (NotAuthenticatedException e) {
                new NotAuthenticatedNotification(e.getMessage());
            }
        });

        form.addDeleteEventListener(event -> {
            try {
                coreAPI.deleteJobPositionById(event.getModel().getId());
                new InfoNotification(getTranslation(
                        "jobPositionGrid.deleteJobPosition", event.getModel().getName()));
                updateGrid();
            } catch (NotAuthenticatedException e) {
                new NotAuthenticatedNotification(e.getMessage());
            }
        });
    }

    private HorizontalLayout getToolbar() {
        var toolbar = new HorizontalLayout();
        toolbar.setWidthFull();

        nameFilter.setClearButtonVisible(true);
        nameFilter.setValueChangeMode(ValueChangeMode.LAZY);
        nameFilter.addValueChangeListener(event -> updateGrid());

        machineFilter.setClearButtonVisible(true);
        machineFilter.setValueChangeMode(ValueChangeMode.LAZY);
        machineFilter.addValueChangeListener(event -> updateGrid());

        addJobPosition.setWidth("160px");
        addJobPosition.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        addJobPosition.addClickListener(event -> addJobPosition());

        var spacer = new Span();
        var spacer2 = new Span();
        spacer2.setWidth("30em");

        toolbar.add(nameFilter, machineFilter, spacer, addJobPosition, spacer2);

        toolbar.setFlexGrow(1, spacer);
        return toolbar;
    }

    private HorizontalLayout getContent() {
        var content = new HorizontalLayout(grid, form);
        content.setSizeFull();
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        return content;
    }

    private void addJobPosition() {
        form.setFormModeSave();
        form.setVisible(true);
    }

    private void localize() {
        grid.getColumnByKey("name").setHeader(getTranslation("jobPositionGrid.name"));
        grid.getColumnByKey("machine").setHeader(getTranslation("jobPositionGrid.machine"));

        nameFilter.setPlaceholder(getTranslation("jobPositionGrid.nameFilter"));
        machineFilter.setPlaceholder(getTranslation("jobPositionGrid.machineFilter"));
        addJobPosition.setText(getTranslation("jobPositionGrid.addJobPosition"));
    }
}
