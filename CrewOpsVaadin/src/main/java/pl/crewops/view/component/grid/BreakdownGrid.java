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
        this.form = new BreakdownForm();

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

        filter.setPlaceholder(getTranslation("breakdownGrid.filter.placeholder"));
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(event -> updateBreakdownGrid());

        toolbar.add(filter);

        return toolbar;
    }

    private void configureGrid() {
        grid.setSizeFull();

        grid.addColumn(model -> model.getVehicle() != null ? model.getVehicle().registerNumber() : "-")
                .setHeader(getTranslation("breakdownGrid.column.registrationNumber"));

        grid.addColumn(BreakdownFormModel::getDescription)
                .setHeader(getTranslation("breakdownGrid.column.description"));

        grid.addColumn(BreakdownFormModel::isCritical).setHeader(getTranslation("breakdownGrid.column.critical"));

        grid.addColumn(BreakdownFormModel::isSolved).setHeader(getTranslation("breakdownGrid.column.solved"));

        grid.addColumn(model -> model.getReportedBy() != null
                        ? model.getReportedBy().firstName() + " "
                                + model.getReportedBy().lastName()
                        : "-")
                .setHeader(getTranslation("breakdownGrid.column.reportedBy"));

        grid.addColumn(model -> model.getRepairedBy() != null
                        ? model.getRepairedBy().firstName() + " "
                                + model.getRepairedBy().lastName()
                        : "-")
                .setHeader(getTranslation("breakdownGrid.column.repairedBy"));

        grid.addColumn(BreakdownFormModel::getSolvedAt).setHeader(getTranslation("breakdownGrid.column.solvedAt"));

        grid.getColumns().forEach(column -> column.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event -> editBreakdown(event.getValue()));
    }

    private void configureForm() {
        form.setWidth("25em");

        form.addUpdateListener(this::updateBreakdown);
        form.addCloseListener(event -> closeEditor());
    }

    public void updateBreakdownGrid() {
        try {
            List<BreakdownFormModel> breakdowns = coreAPI.getAllBreakdowns().stream()
                    .map(BreakdownFormModel::toBreakdownFormModel)
                    .toList();

            if (filter.getValue() == null || filter.getValue().isBlank()) {
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

    private void updateBreakdown(BreakdownForm.UpdateEvent event) {
        try {
            Optional<BreakdownDTO> breakdownDTO =
                    coreAPI.updateBreakdown(BreakdownFormModel.toUpdateBreakdownDTO(event.getBreakdown()));
            updateBreakdownGrid();
            closeEditor();
        } catch (NotAuthenticatedException e) {
            UI.getCurrent().navigate(HomeView.class);
        }
    }
}
