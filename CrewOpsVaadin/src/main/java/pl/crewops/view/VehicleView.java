package pl.crewops.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import pl.crewops.dto.vehicle.VehicleDTO;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtInfoService;
import pl.crewops.view.component.mainLayout.MainLayout;
import pl.crewops.view.form.VehicleForm;
import pl.crewops.view.form.model.VehicleFormModel;

@SpringComponent
@Slf4j
@Scope("prototype")
@Route(value = "vehicles")
@PageTitle("Vehicle view")
public class VehicleView extends MainLayout {
    Grid<VehicleDTO> grid = new Grid<>(VehicleDTO.class);
    TextField filterText = new TextField();
    VehicleForm form;

    public VehicleView(CoreAPI coreAPI, JwtInfoService jwtInfoService) {
        super(coreAPI, jwtInfoService);
        addClassName("vehicle-view");
        VerticalLayout currentContent = new VerticalLayout();
        currentContent.setId("current-content");

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

        updateList();
        closeEditor();
    }

    private Component getToolbar() {
        filterText.setPlaceholder("Filter by type");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(event -> updateList());

        Button addVehicleButton = new Button("Add vehicle");
        addVehicleButton.addClickListener(e -> addVehicle());

        var toolbar = new HorizontalLayout(filterText, addVehicleButton);
        toolbar.addClassName("vehicle-toolbar");
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
        form = new VehicleForm(coreAPI);
        form.setWidth("25em");
    }

    private void configureGrid() {
        grid.addClassNames("vehicle-grid");
        grid.setSizeFull();
        grid.setColumns("vehicleType", "registerNumber", "broken", "make", "model", "year", "vin");
        grid.getColumns().forEach(column -> column.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event -> editVehicle(event.getValue()));
    }

    private void updateList() {
        List<VehicleDTO> vehicles = coreAPI.getAllVehicles();

        if (filterText.getValue() == null) {
            grid.setItems(vehicles);
        } else {
            grid.setItems(vehicles.stream()
                    .filter(vehicleDTO -> vehicleDTO
                            .vehicleType()
                            .name()
                            .toLowerCase()
                            //                            .registerNumber()
                            //                            .toLowerCase()
                            .contains(filterText.getValue().toLowerCase()))
                    .toList());
        }
    }

    private void closeEditor() {
        form.setVehicle(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void saveContact(VehicleForm.SaveEvent event) {
        coreAPI.createVehicle(VehicleFormModel.toCreateVehicleDTO(event.getVehicle()));
        updateList();
        closeEditor();
    }

    public void editVehicle(VehicleDTO vehicleDTO) {
        if (vehicleDTO == null) {
            closeEditor();
        } else {
            form.setVehicle(vehicleDTO);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void deleteContact(VehicleForm.DeleteEvent event) {
        log.info("Deleting contact {}", event.getVehicle().getId());
        coreAPI.deleteVehicle(event.getVehicle().getId());
        updateList();
        closeEditor();
    }

    private void addVehicle() {
        grid.asSingleSelect().clear();
        form.setVisible(true);
    }
}
