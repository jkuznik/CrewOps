package pl.crewops.view.component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import java.util.List;
import java.util.Optional;
import pl.crewops.dto.vehicle.VehicleDTO;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.view.HomeView;
import pl.crewops.view.component.notification.UpdateVehicleNotification;
import pl.crewops.view.form.VehicleForm;
import pl.crewops.view.form.model.VehicleFormModel;

public class VehicleGrid extends VerticalLayout {
    private final CoreAPI coreAPI;

    private final Grid<VehicleFormModel> grid = new Grid<>(VehicleFormModel.class);
    private final TextField filter = new TextField();
    private final VehicleForm form = new VehicleForm();

    public VehicleGrid(CoreAPI coreAPI) {
        this.coreAPI = coreAPI;

        configureGrid();
        configureForm();

        updateGrid();
        closeEditor();

        setSizeFull();
        add(getToolbar(), getContent());
    }

    public void addVehicleEvent() {
        addVehicle();
    }

    public void closeEditor() {
        form.setVehicle(null);
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

        filter.setPlaceholder("Filter by type");
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(event -> updateGrid());

        toolbar.add(filter);

        return toolbar;
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.setColumns("vehicleType", "registerNumber", "broken", "make", "model", "year", "vin");
        grid.getColumns().forEach(column -> column.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event -> {
            editVehicle(event.getValue());
        });
    }

    private void configureForm() {
        form.setWidth("25em");

        form.addSaveListener(this::saveVehicle);
        form.addUpdateListener(this::updateVehicle);
        form.addDeleteListener(this::deleteVehicle);
        form.addCloseListener(event -> {
            closeEditor();
        });
    }

    private void updateGrid() {
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
        if (vehicleFormModel == null) {
            closeEditor();
        } else {
            form.setVehicle(vehicleFormModel);
            form.setFormModeUpdate();
            form.setVisible(true);
        }
    }

    private void addVehicle() {
        grid.asSingleSelect().clear();
        form.setFormModeSave();
        form.setVisible(true);
    }

    private void saveVehicle(VehicleForm.SaveEvent event) {
        try {
            Optional<VehicleDTO> vehicleDTO =
                    coreAPI.createVehicle(VehicleFormModel.toCreateVehicleDTO(event.getVehicle()));
            updateGrid();
            closeEditor();
            vehicleDTO.ifPresent(UpdateVehicleNotification::new);
        } catch (NotAuthenticatedException e) {
            UI.getCurrent().navigate(HomeView.class);
        }
    }

    private void updateVehicle(VehicleForm.UpdateEvent event) {
        try {
            Optional<VehicleDTO> vehicleDTO =
                    coreAPI.updateVehicle(VehicleFormModel.toUpdateVehicleDTO(event.getVehicle()));
            updateGrid();
            closeEditor();
            vehicleDTO.ifPresent(UpdateVehicleNotification::new);
        } catch (NotAuthenticatedException e) {
            UI.getCurrent().navigate(HomeView.class);
        }
    }

    private void deleteVehicle(VehicleForm.DeleteEvent event) {
        try {
            coreAPI.deleteVehicle(event.getVehicle().getId());
            updateGrid();
            closeEditor();
        } catch (NotAuthenticatedException e) {
            UI.getCurrent().navigate(HomeView.class);
        }
    }
}
