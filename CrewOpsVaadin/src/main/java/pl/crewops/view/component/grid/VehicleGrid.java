package pl.crewops.view.component.grid;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import pl.crewops.dto.breakdown.BreakdownDTO;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.vehicle.VehicleDTO;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.BreakdownFormModel;
import pl.crewops.model.VehicleFormModel;
import pl.crewops.view.HomeView;
import pl.crewops.view.component.form.BreakdownForm;
import pl.crewops.view.component.form.VehicleForm;
import pl.crewops.view.component.notification.UpdateVehicleNotification;

@Slf4j
public class VehicleGrid extends VerticalLayout {
    private final CoreAPI coreAPI;

    private final Grid<VehicleFormModel> grid = new Grid<>(VehicleFormModel.class);
    private final TextField filter = new TextField();
    private final VehicleForm vehicleForm;
    private final BreakdownForm breakdownForm = new BreakdownForm();
    private VehicleFormModel selectedModel;

    public VehicleGrid(CoreAPI coreAPI, BreakdownGrid breakdownGrid) {
        this.coreAPI = coreAPI;
        this.vehicleForm = new VehicleForm(this, breakdownGrid);

        configureGrid();
        configureForm();
        this.addDisplayBreakdownsListener(event -> {
            displayBreakdowns(this, breakdownGrid);
        });

        updateVehicleGrid();
        closeEditor();

        setSizeFull();
        add(getToolbar(), getContent());
    }

    public void closeEditor() {
        vehicleForm.setVehicle(null);
        vehicleForm.setVisible(false);
        breakdownForm.setVisible(false);
    }

    public VehicleFormModel getSelectedVehicle() {
        return selectedModel;
    }

    private HorizontalLayout getContent() {
        var content = new HorizontalLayout(grid, vehicleForm, breakdownForm);
        content.setSizeFull();
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, vehicleForm);
        content.setFlexGrow(1, breakdownForm);
        return content;
    }

    private HorizontalLayout getToolbar() {
        var toolbar = new HorizontalLayout();

        filter.setPlaceholder(getTranslation("vehicleGrid.filter.placeholder"));
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(event -> updateVehicleGrid());

        Button addVehicle = new Button(getTranslation("vehicleGrid.button.addVehicle"));
        addVehicle.addClickListener(event -> addVehicle());

        toolbar.add(filter, addVehicle);

        return toolbar;
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.removeAllColumns();

        grid.addColumn(VehicleFormModel::getVehicleType).setHeader(getTranslation("vehicleGrid.column.vehicleType"));
        grid.addColumn(VehicleFormModel::getRegistrationNumber)
                .setHeader(getTranslation("vehicleGrid.column.registrationNumber"));
        grid.addColumn(VehicleFormModel::getBroken).setHeader(getTranslation("vehicleGrid.column.broken"));
        grid.addColumn(VehicleFormModel::getMake).setHeader(getTranslation("vehicleGrid.column.make"));
        grid.addColumn(VehicleFormModel::getModel).setHeader(getTranslation("vehicleGrid.column.model"));
        grid.addColumn(VehicleFormModel::getYear).setHeader(getTranslation("vehicleGrid.column.year"));
        grid.addColumn(VehicleFormModel::getVin).setHeader(getTranslation("vehicleGrid.column.vin"));

        grid.getColumns().forEach(column -> column.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event -> {
            selectedModel = event.getValue();
            editVehicle(event.getValue());
        });
    }

    private void configureForm() {
        vehicleForm.setWidth("25em");

        vehicleForm.addSaveListener(this::saveVehicle);
        vehicleForm.addUpdateListener(this::updateVehicle);
        vehicleForm.addDeleteListener(this::deleteVehicle);
        vehicleForm.addCloseListener(event -> {
            closeEditor();
        });
        vehicleForm.addReportBreakdownListener(this::reportBreakdown);
        breakdownForm.addSaveListener(this::saveBreakdown);
    }

    public void updateVehicleGrid() {
        try {
            List<VehicleFormModel> vehicles = coreAPI.getAllVehicles().stream()
                    .map(VehicleFormModel::toVehicleFormModel)
                    .toList();

            if (filter.getValue() == null) {
                grid.setItems(vehicles);
            } else {
                grid.setItems(vehicles.stream()
                        .filter(vehicleDTO -> vehicleDTO
                                .getVehicleType()
                                .toLowerCase()
                                //                            .registerNumber()
                                //                            .toLowerCase()
                                .contains(filter.getValue().toLowerCase()))
                        .toList());
            }
        } catch (NotAuthenticatedException e) {
            UI.getCurrent().navigate(HomeView.class);
        }
    }

    private void editVehicle(VehicleFormModel vehicleFormModel) {
        breakdownForm.setVisible(false);
        if (vehicleFormModel == null) {
            closeEditor();
        } else {
            vehicleForm.setVehicle(vehicleFormModel);
            vehicleForm.setFormModeUpdate();
            vehicleForm.setVisible(true);
        }
    }

    private void addVehicle() {
        breakdownForm.setVisible(false);

        grid.asSingleSelect().clear();
        vehicleForm.setFormModeSave();
        vehicleForm.setVisible(true);
    }

    private void saveVehicle(VehicleForm.SaveEvent event) {
        try {

            Optional<VehicleDTO> vehicleDTO =
                    coreAPI.createVehicle(VehicleFormModel.toCreateVehicleDTO(event.getVehicle()));
            updateVehicleGrid();
            closeEditor();
            // todo: modify and check other similar exceptions
            vehicleDTO.ifPresent(UpdateVehicleNotification::new);
        } catch (NotAuthenticatedException e) {
            UI.getCurrent().navigate(HomeView.class);
        }
    }

    private void updateVehicle(VehicleForm.UpdateEvent event) {
        try {
            Optional<VehicleDTO> vehicleDTO =
                    coreAPI.updateVehicle(VehicleFormModel.toUpdateVehicleDTO(event.getVehicle()));
            updateVehicleGrid();
            closeEditor();
            vehicleDTO.ifPresent(UpdateVehicleNotification::new);
        } catch (NotAuthenticatedException e) {
            UI.getCurrent().navigate(HomeView.class);
        }
    }

    private void saveBreakdown(BreakdownForm.SaveEvent event) {
        try {
            log.info("Saving breakdown");
            Optional<BreakdownDTO> breakdownDTO =
                    coreAPI.createBreakdown(BreakdownFormModel.toCreateBreakdownDTO(event.getBreakdown()));
            updateVehicleGrid();
            closeEditor();
            // TODO: notification for add breakdown action
            //            breakdownDTO.ifPresent()
        } catch (NotAuthenticatedException e) {
            UI.getCurrent().navigate(HomeView.class);
        }
    }

    private void deleteVehicle(VehicleForm.DeleteEvent event) {
        try {
            coreAPI.deleteVehicle(event.getVehicle().getId());
            updateVehicleGrid();
            closeEditor();
        } catch (NotAuthenticatedException e) {
            UI.getCurrent().navigate(HomeView.class);
        }
    }

    private void reportBreakdown(VehicleForm.ReportBreakdown event) {
        vehicleForm.setVisible(false);

        var breakdownFormModel = new BreakdownFormModel();
        try {
            breakdownFormModel.setId(UUID.randomUUID());
            breakdownFormModel.setVehicle(coreAPI.getAllVehicles().stream()
                    .filter(vehicleDTO -> vehicleDTO
                            .registerNumber()
                            .equals(event.getVehicle().getRegistrationNumber()))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("No vehicle registered with id "
                            + event.getVehicle().getRegistrationNumber())));
            breakdownFormModel.setReportedBy(
                    // TODO: just PoC logic - implement logic
                    EmployeeDTO.builder()
                            .id(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                            .build());
        } catch (NotAuthenticatedException e) {
            UI.getCurrent().navigate(HomeView.class);
        }

        breakdownForm.setBreakdown(breakdownFormModel);
        breakdownForm.setFormModeSave();
        breakdownForm.setVisible(true);
    }

    public void displayBreakdowns(VehicleGrid vehicleGrid, BreakdownGrid breakdownGrid) {
        fireEvent(new DisplayBreakdownsEvent(vehicleGrid, breakdownGrid));
    }

    public abstract static class VehicleGridEvent extends ComponentEvent<VehicleGrid> {

        protected VehicleGridEvent(VehicleGrid source, BreakdownGrid breakdownGrid) {
            super(source, false);

            source.setVisible(false);
            breakdownGrid.setFilter(source.getSelectedVehicle().getRegistrationNumber());
            breakdownGrid.setVisible(true);
        }
    }

    public static class DisplayBreakdownsEvent extends VehicleGridEvent {

        DisplayBreakdownsEvent(VehicleGrid source, BreakdownGrid breakdownGrid) {
            super(source, breakdownGrid);
        }
    }

    public Registration addDisplayBreakdownsListener(ComponentEventListener<DisplayBreakdownsEvent> listener) {
        return addListener(DisplayBreakdownsEvent.class, listener);
    }
}
