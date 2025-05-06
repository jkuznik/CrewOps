package pl.crewops.view.component;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import pl.crewops.dto.vehicle.VehicleDTO;

public class VehicleAccordion extends FormLayout {

    public VehicleAccordion() {
        addClassName("qualification-accordion");
    }

    public void getValues(Set<VehicleDTO> vehicles) {
        removeAll();
        Accordion accordion = new Accordion();
        List<Span> items = new ArrayList<>();

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
}
