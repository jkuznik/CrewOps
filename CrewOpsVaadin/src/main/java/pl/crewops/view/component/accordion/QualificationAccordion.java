package pl.crewops.view.component.accordion;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import pl.crewops.dto.qualification.QualificationDTO;

// TODO: consider or just test if this component really have to extends FormLayout or some other
public class QualificationAccordion extends FormLayout {

    public QualificationAccordion() {
        addClassName("qualification-accordion");
    }

    public void getValues(Set<QualificationDTO> qualifications) {
        removeAll();
        Accordion accordion = new Accordion();
        List<Span> items = new ArrayList<>();

        qualifications.forEach(qualification -> {
            items.add(new Span(qualification.description()));
        });

        var qualificationDisplay = new VerticalLayout(items.toArray(new Span[items.size()]));
        qualificationDisplay.setSpacing(false);
        qualificationDisplay.setPadding(false);

        accordion.setVisible(true);
        add(accordion);
        accordion.add(getTranslation("qualificationAccordion.title"), qualificationDisplay);
    }
}
