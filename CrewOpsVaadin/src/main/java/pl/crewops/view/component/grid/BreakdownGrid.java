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

    private final Grid<BreakdownFormModel> grid = new Grid<>();
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

    public void setFilter(String value) {
        filter.setValue(value);
        updateBreakdownGrid();
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

        filter.setPlaceholder("Filter by registration number");
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(event -> updateBreakdownGrid());

        // TODO: consider possible action for breakdown, maybe 'update' or 'solve'?
        //        addVehicle.addClickListener(event -> addBreakdown());

        toolbar.add(filter);

        return toolbar;
    }

    private void configureGrid() {
        grid.setSizeFull();
        // TODO: add column createdAt to display date of reported breakdown

        grid.addColumn(model -> {
                    if (model.getVehicle() != null) {
                        return model.getVehicle().registerNumber();
                    }
                    return "-";
                })
                .setHeader("Registration Number");

        grid.addColumn(BreakdownFormModel::getDescription).setHeader("Description");

        grid.addColumn(BreakdownFormModel::isCritical).setHeader("Critical");

        grid.addColumn(BreakdownFormModel::isSolved).setHeader("Solved");

        grid.addColumn(model -> {
                    if (model.getReportedBy() != null) {
                        return model.getReportedBy().firstName() + " "
                                + model.getReportedBy().lastName();
                    }
                    return "-";
                })
                .setHeader("Reported By");

        grid.addColumn(model -> {
                    if (model.getRepairedBy() != null) {
                        return model.getRepairedBy().firstName() + " "
                                + model.getRepairedBy().lastName();
                    }
                    return "-";
                })
                .setHeader("Repaired By");

        grid.addColumn(BreakdownFormModel::getSolvedAt).setHeader("Solved At");

        grid.getColumns().forEach(column -> column.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event -> {
            editBreakdown(event.getValue());
        });
    }

    private void configureForm() {
        form.setWidth("25em");

        //        form.addSaveListener(this::saveBreakdown);
        form.addUpdateListener(this::updateBreakdown);
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
                                .getVehicle()
                                .registerNumber()
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

    // TODO: this is logic of add breakdown button, delete if app works fine
    private void addBreakdown() {
        grid.asSingleSelect().clear();
        form.setFormModeSave();
        form.setVisible(true);
    }

    // TODO: delete this if app works fine. Save option is achieved from vehicles grid
    //    private void saveBreakdown(BreakdownForm.SaveEvent event) {
    //        try {
    //            Optional<BreakdownDTO> breakdownDTO =
    //                    coreAPI.createBreakdown(BreakdownFormModel.toCreateBreakdownDTO(event.getBreakdown()));
    //            updateBreakdownGrid();
    //            closeEditor();
    //            //            breakdownDTO.ifPresent(UpdateVehicleNotification::new);
    //        } catch (NotAuthenticatedException e) {
    //            UI.getCurrent().navigate(HomeView.class);
    //        }
    //    }

    private void updateBreakdown(BreakdownForm.UpdateEvent event) {
        try {
            Optional<BreakdownDTO> breakdownDTO =
                    coreAPI.updateBreakdown(BreakdownFormModel.toUpdateBreakdownDTO(event.getBreakdown()));
            updateBreakdownGrid();
            closeEditor();
            // TODO: implement if present()
            //            breakdownDTO.ifPresent();
        } catch (NotAuthenticatedException e) {
            UI.getCurrent().navigate(HomeView.class);
        }
    }
}
