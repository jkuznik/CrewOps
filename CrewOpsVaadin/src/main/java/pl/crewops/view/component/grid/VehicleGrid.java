package pl.crewops.view.component.grid;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
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
import pl.crewops.util.RoleResolver;
import pl.crewops.view.HomeView;
import pl.crewops.view.component.form.BreakdownForm;
import pl.crewops.view.component.form.VehicleForm;
import pl.crewops.view.component.notification.AddBreakdownNotification;
import pl.crewops.view.component.notification.AddVehicleNotification;
import pl.crewops.view.component.notification.DeleteVehicleGuardian;
import pl.crewops.view.component.notification.UpdateVehicleNotification;

@Slf4j
public class VehicleGrid extends VerticalLayout {
    private final CoreAPI coreAPI;
    private final RoleResolver roleResolver;

    private final Grid<VehicleFormModel> grid = new Grid<>(VehicleFormModel.class);
    private final TextField filter = new TextField();
    private final Button addVehicle = new Button();
    private final VehicleForm vehicleForm;
    private final BreakdownForm breakdownForm = new BreakdownForm();
    private VehicleFormModel selectedModel;

    public VehicleGrid(CoreAPI coreAPI, BreakdownGrid breakdownGrid, RoleResolver roleResolver) {
        this.coreAPI = coreAPI;
        this.roleResolver = roleResolver;
        this.vehicleForm = new VehicleForm(this, breakdownGrid, coreAPI);

        configureGrid();
        configureForm();

        localize();

        this.addDisplayBreakdownsListener(event -> {
            displayBreakdowns(this, breakdownGrid);
        });

        updateVehicleGrid();
        closeEditor();

        setSizeFull();
        add(getToolbar(), getContent());
    }

    private void localize() {
        filter.setPlaceholder(getTranslation("vehicleGrid.filter.placeholder"));

        addVehicle.setText(getTranslation("vehicleGrid.button.addVehicle"));

        setColumnHeader("vehicleType", "vehicleGrid.column.vehicleType");
        setColumnHeader("registrationNumber", "vehicleGrid.column.registrationNumber");
        setColumnHeader("broken", "vehicleGrid.column.broken");
        setColumnHeader("make", "vehicleGrid.column.make");
        setColumnHeader("model", "vehicleGrid.column.model");
        setColumnHeader("year", "vehicleGrid.column.year");
        setColumnHeader("vin", "vehicleGrid.column.vin");
    }

    private void setColumnHeader(String key, String translationKey) {
        grid.getColumnByKey(key).setHeader(getTranslation(translationKey));
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

        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(event -> updateVehicleGrid());

        addVehicle.addClickListener(event -> addVehicle());

        if (roleResolver.principalHasAtLeastManagerRole()) {
            toolbar.add(filter, addVehicle);
        } else {
            toolbar.add(filter);
        }

        return toolbar;
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.removeAllColumns();

        grid.addColumn(VehicleFormModel::getVehicleType).setKey("vehicleType");
        grid.addColumn(VehicleFormModel::getRegisterNumber).setKey("registrationNumber");

        grid.addColumn(new ComponentRenderer<>(vehicle -> {
                    if (vehicle.getBroken()) {
                        Icon cross = VaadinIcon.CLOSE_CIRCLE.create();
                        cross.setColor("red");
                        return cross;
                    } else {
                        Icon check = VaadinIcon.CHECK_CIRCLE.create();
                        check.setColor("green");
                        return check;
                    }
                }))
                .setHeader("Broken")
                .setKey("broken");

        grid.addColumn(VehicleFormModel::getMake).setKey("make");
        grid.addColumn(VehicleFormModel::getModel).setKey("model");
        grid.addColumn(VehicleFormModel::getYear).setKey("year");
        grid.addColumn(VehicleFormModel::getVin).setKey("vin");

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
        vehicleForm.addCloseListener(event -> closeEditor());
        vehicleForm.addReportBreakdownListener(this::reportBreakdown);
        breakdownForm.addSaveListener(this::saveBreakdown);
    }

    public void updateVehicleGrid() {
        try {
            List<VehicleFormModel> vehicles = coreAPI.getAllVehicles().stream()
                    .map(VehicleFormModel::toVehicleFormModel)
                    .toList();

            if (filter.getValue() == null || filter.getValue().isBlank()) {
                grid.setItems(vehicles);
            } else {
                grid.setItems(vehicles.stream()
                        .filter(vehicleDTO -> vehicleDTO
                                .getVehicleType()
                                .toLowerCase()
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
            if (roleResolver.principalHasAtLeastManagerRole()) {
                vehicleForm.setFormModeUpdate();
            } else {
                vehicleForm.setFormModeEmployeePermission();
            }
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
            vehicleForm.updateAvailableVehicleTypes(coreAPI);
            closeEditor();
            vehicleDTO.ifPresent(AddVehicleNotification::new);
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
            breakdownDTO.ifPresent(AddBreakdownNotification::new);
        } catch (NotAuthenticatedException e) {
            UI.getCurrent().navigate(HomeView.class);
        }
    }

    private void deleteVehicle(VehicleForm.DeleteEvent event) {
        new DeleteVehicleGuardian(event.getVehicle(), () -> {
            try {
                coreAPI.deleteVehicle(event.getVehicle().getId());
                updateVehicleGrid();
                vehicleForm.updateAvailableVehicleTypes(coreAPI);
                closeEditor();
            } catch (NotAuthenticatedException e) {
                UI.getCurrent().navigate(HomeView.class);
            }
        });
    }

    private void reportBreakdown(VehicleForm.ReportBreakdown event) {
        vehicleForm.setVisible(false);

        var breakdownFormModel = new BreakdownFormModel();
        try {
            breakdownFormModel.setId(UUID.randomUUID());
            breakdownFormModel.setVehicle(coreAPI.getAllVehicles().stream()
                    .filter(vehicleDTO -> vehicleDTO
                            .registerNumber()
                            .equals(event.getVehicle().getRegisterNumber()))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("No vehicle registered with id "
                            + event.getVehicle().getRegisterNumber())));
            breakdownFormModel.setReportedBy(EmployeeDTO.builder()
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
            breakdownGrid.setFilter(source.getSelectedVehicle().getRegisterNumber());
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
