package pl.crewops.view.component.grid;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import java.util.List;
import java.util.Optional;
import pl.crewops.dto.breakdown.BreakdownDTO;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.BreakdownFormModel;
import pl.crewops.view.HomeView;
import pl.crewops.view.component.form.BreakdownForm;

public class BreakdownGrid extends VerticalLayout {
    private final CoreAPI coreAPI;

    private final Grid<BreakdownFormModel> grid = new Grid<>(BreakdownFormModel.class);
    private final TextField filter = new TextField();
    private final BreakdownForm form;

    public BreakdownGrid(CoreAPI coreAPI) {
        this.coreAPI = coreAPI;
        form = new BreakdownForm();

        configureGrid();
        configureForm();

        updateBreakdownGrid();
        closeEditor();

        setSizeFull();
        add(getToolbar(), getContent());
    }

    public void closeEditor() {
        form.setBreakdown(null);
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

        filter.setPlaceholder("Filter by description");
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(event -> updateBreakdownGrid());

        // TODO: consider possible action for breakdown, maybe 'update' or 'solve'?
        //        Button addVehicle = new Button("Add vehicle");
        //        addVehicle.addClickListener(event -> addBreakdown());

        //        toolbar.add(filter, addVehicle);

        toolbar.add(filter);

        return toolbar;
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.setColumns("vehicle", "description", "critical", "reportedBy", "solved", "repairedBy", "solvedAt");
        //        grid.addColumn(model -> model.getVehicle().registerNumber())
        //                        .setHeader("Registration Number");
        //
        //        grid.addColumn(BreakdownFormModel::getDescription)
        //                        .setHeader("Description");
        //
        //        grid.addColumn(BreakdownFormModel::isCritical)
        //                        .setHeader("Critical");
        //
        //        grid.addColumn(BreakdownFormModel::getReportedBy)
        //                .setHeader("Reported By");
        //
        //        grid.addColumn(BreakdownFormModel::isSolved)
        //                .setHeader("Solved");
        //
        //        grid.addColumn(BreakdownFormModel::getRepairedBy)
        //                .setHeader("Repaired By");
        //
        //        grid.addColumn(BreakdownFormModel::getSolvedAt)
        //                .setHeader("Solved At");

        grid.getColumns().forEach(column -> column.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event -> {
            editBreakdown(event.getValue());
        });
    }

    private void configureForm() {
        form.setWidth("25em");

        form.addSaveListener(this::saveBreakdown);
        //        form.addUpdateListener(this::updateVehicle);
        form.addCloseListener(event -> {
            closeEditor();
        });
    }

    public void updateBreakdownGrid() {
        try {
            List<BreakdownFormModel> breakdowns = coreAPI.getAllBreakdowns().stream()
                    .map(BreakdownFormModel::toBreakdownFormModel)
                    .toList();

            if (filter.getValue() == null) {
                grid.setItems(breakdowns);
            } else {
                grid.setItems(breakdowns.stream()
                        .filter(breakdownDTO -> breakdownDTO
                                .getDescription()
                                .toLowerCase()
                                .contains(filter.getValue().toLowerCase()))
                        .toList());
            }
        } catch (NotAuthenticatedException e) {
            UI.getCurrent().navigate(HomeView.class);
        }
    }

    private void editBreakdown(BreakdownFormModel breakdownFormModel) {
        if (breakdownFormModel == null) {
            closeEditor();
        } else {
            form.setBreakdown(breakdownFormModel);
            form.setFormModeUpdate();
            form.setVisible(true);
        }
    }

    private void addBreakdown() {
        grid.asSingleSelect().clear();
        form.setFormModeSave();
        form.setVisible(true);
    }

    private void saveBreakdown(BreakdownForm.SaveEvent event) {
        try {
            Optional<BreakdownDTO> breakdownDTO =
                    coreAPI.createBreakdown(BreakdownFormModel.toCreateBreakdownDTO(event.getBreakdown()));
            updateBreakdownGrid();
            closeEditor();
            //            breakdownDTO.ifPresent(UpdateVehicleNotification::new);
        } catch (NotAuthenticatedException e) {
            UI.getCurrent().navigate(HomeView.class);
        }
    }

    //    private void updateVehicle(BreakdownForm.UpdateEvent event) {
    //        try {
    //            Optional<BreakdownDTO> breakdownDTO =
    //                    coreAPI.updateBreakdown(BreakdownFormModel.toUpdateBreakdownDTO(event.getBreakdown()));
    //            updateGrid();
    //            closeEditor();
    ////            breakdownDTO.ifPresent(UpdateVehicleNotification::new);
    //        } catch (NotAuthenticatedException e) {
    //            UI.getCurrent().navigate(HomeView.class);
    //        }
    //    }
}
