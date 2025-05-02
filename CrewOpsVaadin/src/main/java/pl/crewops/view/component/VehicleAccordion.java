package pl.crewops.view.component;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import pl.crewops.dto.vehicle.VehicleDTO;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;

@SpringComponent
public class VehicleAccordion extends FormLayout {

    private final CoreAPI coreAPI;

    public VehicleAccordion(CoreAPI coreAPI) {
        addClassName("qualification-accordion");
        this.coreAPI = coreAPI;
    }

    public void setConfig(Set<UUID> vehicleIds) {
        removeAll();
        Accordion accordion = new Accordion();
        List<Span> items = new ArrayList<>();

        List<VehicleDTO> vehicles = getVehicles(vehicleIds);
        vehicles.forEach(vehicle -> {
            // TODO: implement logic to display formatted registerNumber and vehicle type
            items.add(new Span(vehicle.registerNumber()));
        });

        VerticalLayout vehiclesDisplay = new VerticalLayout(items.toArray(new Span[items.size()]));
        vehiclesDisplay.setSpacing(false);
        vehiclesDisplay.setPadding(false);

        accordion.setVisible(true);
        add(accordion);
        accordion.add("Vehicles", vehiclesDisplay);
    }

    private List<VehicleDTO> getVehicles(Set<UUID> vehicleIds) {
        try {
            return coreAPI.getVehiclesByIds(vehicleIds);
        } catch (NotAuthenticatedException e) {
            // TODO: implement logic
            return new ArrayList<>();
        }
    }
}
