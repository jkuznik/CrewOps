package pl.crewops.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtInfoService;
import pl.crewops.view.component.mainLayout.MainLayout;
import pl.crewops.view.form.VehicleForm;
import pl.crewops.view.form.model.VehicleFormModel;

@Slf4j
@Route(value = "vehicles")
@PageTitle("Vehicle view")
public class VehicleView extends MainLayout implements BeforeEnterObserver {
    Grid<VehicleFormModel> grid = new Grid<>(VehicleFormModel.class);
    TextField filterText = new TextField();
    VehicleForm form;

    public VehicleView(CoreAPI coreAPI, JwtInfoService jwtInfoService) {
        super(coreAPI, jwtInfoService);
        addClassName("vehicle-view");
        VerticalLayout currentContent = new VerticalLayout();
        currentContent.setId("view-content");

        mainContent.removeAll();
        mainContent.add(currentContent, mainFooter);
        mainContent.setFlexGrow(1, currentContent);

        currentContent.setSizeFull();
        currentContent.setPadding(true);
        currentContent.setSpacing(true);
        currentContent.getStyle().set("overflow", "auto");

        configureGrid();
        configureForm();

        currentContent.add(getToolbar(), getCurrentContent());

        updateGrid();
        closeEditor();
    }

    private Component getToolbar() {
        filterText.setLabel("Filter by type...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(event -> updateGrid());

        Button addVehicleButton = new Button("Add vehicle");
        addVehicleButton.addClickListener(e -> addVehicle());

        var toolbar = new HorizontalLayout(filterText, addVehicleButton);
        toolbar.addClassName("vehicle-toolbar");
        toolbar.setAlignItems(FlexComponent.Alignment.END);
        return toolbar;
    }

    private HorizontalLayout getCurrentContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.addClassName("vehicle-view-content");
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        form = new VehicleForm();
        form.setWidth("25em");

        form.addSaveListener(this::saveVehicle);
        form.addUpdateListener(this::updateVehicle);
        form.addDeleteListener(this::deleteVehicle);
        form.addCloseListener(event -> closeEditor());
    }

    private void configureGrid() {
        grid.addClassNames("vehicle-grid");
        grid.setSizeFull();
        grid.setColumns("vehicleType", "registerNumber", "broken", "make", "model", "year", "vin");
        grid.getColumns().forEach(column -> column.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event -> editVehicle(event.getValue()));
    }

    private void updateGrid() {
        try {
            List<VehicleFormModel> vehicles = coreAPI.getAllVehicles().stream()
                    .map(VehicleFormModel::toVehicleFormModel)
                    .toList();

            if (filterText.getValue() == null) {
                grid.setItems(vehicles);
            } else {
                grid.setItems(vehicles.stream()
                        .filter(vehicleDTO -> vehicleDTO
                                .getVehicleType()
                                .toLowerCase()
                                //                            .registerNumber()
                                //                            .toLowerCase()
                                .contains(filterText.getValue().toLowerCase()))
                        .toList());
            }
        } catch (NotAuthenticatedException e) {
            // TODO: implement logic
        }
    }

    private void closeEditor() {
        form.setVehicle(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    public void editVehicle(VehicleFormModel vehicleFormModel) {
        if (vehicleFormModel == null) {
            closeEditor();
        } else {
            form.setVehicle(vehicleFormModel);
            form.setFormModeUpdate();
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void saveVehicle(VehicleForm.SaveEvent event) {
        try {
            coreAPI.createVehicle(VehicleFormModel.toCreateVehicleDTO(event.getVehicle()));
            updateGrid();
            closeEditor();
        } catch (NotAuthenticatedException e) {
            // TODO: implement logic
        }
    }

    private void updateVehicle(VehicleForm.UpdateEvent event) {
        try {
            coreAPI.updateVehicle(VehicleFormModel.toUpdateVehicleDTO(event.getVehicle()));
            updateGrid();
            closeEditor();
            // TODO: implement notification
        } catch (NotAuthenticatedException e) {
            // TODO: implement logic
        }
    }

    private void deleteVehicle(VehicleForm.DeleteEvent event) {
        try {
            coreAPI.deleteVehicle(event.getVehicle().getId());
            updateGrid();
            closeEditor();
        } catch (NotAuthenticatedException e) {
            // TODO: implment logic
        }
    }

    private void addVehicle() {
        grid.asSingleSelect().clear();
        form.setFormModeSave();
        form.setVisible(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!jwtInfoService.validToken()) {
            event.forwardTo(HomeView.class);
            UI.getCurrent().getPage().setLocation("/");
        }
    }
}
