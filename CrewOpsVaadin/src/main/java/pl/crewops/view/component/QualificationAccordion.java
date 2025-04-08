package pl.crewops.view.component;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.*;
import pl.crewops.dto.qualification.QualificationDTO;
import pl.crewops.infrastructure.core.CoreClient;

@SpringComponent
public class QualificationAccordion extends FormLayout {

    private final CoreClient coreClient;

    public QualificationAccordion(CoreClient coreClient) {
        this.coreClient = coreClient;
    }

    public void config(Set<UUID> qualificationsIds) {
        removeAll();
        Accordion accordion = new Accordion();
        List<Span> items = new ArrayList<>();

        List<QualificationDTO> qualifications = getQualifications(qualificationsIds);
        qualifications.forEach(qualification -> {
            items.add(new Span(qualification.description()));
        });

        VerticalLayout qualificationDisplay = new VerticalLayout(items.toArray(new Span[items.size()]));
        qualificationDisplay.setSpacing(false);
        qualificationDisplay.setPadding(false);

        accordion.setVisible(true);
        add(accordion);
        accordion.add("Qualifications", qualificationDisplay);
    }

    private List<QualificationDTO> getQualifications(Set<UUID> qualificationsIds) {
        return coreClient.getQualifications(qualificationsIds);
    }
}
