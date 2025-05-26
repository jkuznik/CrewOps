package pl.crewops.view.component.accordion;

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
            String formatted = String.format("%-15s%s", vehicle.vehicleType().name(), vehicle.registerNumber());
            Span span = new Span(formatted);
            span.getStyle().set("font-family", "monospace");
            span.getStyle().set("white-space", "pre");
            items.add(span);
        });

        VerticalLayout vehiclesDisplay = new VerticalLayout(items.toArray(new Span[items.size()]));
        vehiclesDisplay.setSpacing(false);
        vehiclesDisplay.setPadding(false);

        accordion.setVisible(true);
        add(accordion);
        accordion.add(getTranslation("vehicleAccordion.title"), vehiclesDisplay);
    }
}
