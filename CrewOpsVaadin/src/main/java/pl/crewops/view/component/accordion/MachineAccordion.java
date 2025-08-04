package pl.crewops.view.component.accordion;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import pl.crewops.dto.machine.MachineDTO;

public class MachineAccordion extends FormLayout {

    public MachineAccordion() {
        addClassName("qualification-accordion");
    }

    public void getValues(Set<MachineDTO> machines) {
        removeAll();
        Accordion accordion = new Accordion();
        List<Span> items = new ArrayList<>();

        machines.forEach(machine -> {
            String formatted = String.format("%-15s%s", machine.machineType().name(), machine.registerNumber());
            Span span = new Span(formatted);
            span.getStyle().set("font-family", "monospace");
            span.getStyle().set("white-space", "pre");
            items.add(span);
        });

        VerticalLayout machinesDisplay = new VerticalLayout(items.toArray(new Span[items.size()]));
        machinesDisplay.setSpacing(false);
        machinesDisplay.setPadding(false);

        accordion.setVisible(true);
        add(accordion);
        accordion.add(getTranslation("machineAccordion.title"), machinesDisplay);
    }
}
