package pl.crewops.view.component;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.*;
import pl.crewops.dto.qualification.QualificationDTO;
import pl.crewops.infrastructure.core.CoreClient;

public class QualificationAccordion extends FormLayout {

    private final CoreClient coreClient;
    private Accordion accordion = new Accordion();

    public QualificationAccordion(CoreClient coreClient) {
        this.coreClient = coreClient;

        accordion.setVisible(true);
        add(accordion);
    }

    public void config(Set<UUID> qualificationsIds) {
        List<Span> items = new ArrayList<>();

        List<QualificationDTO> qualifications = getQualifications(qualificationsIds);
        qualifications.forEach(qualification -> {
            items.add(new Span(qualification.description()));
        });

        VerticalLayout qualificationDisplay = new VerticalLayout(items.toArray(new Span[items.size()]));
        qualificationDisplay.setSpacing(false);
        qualificationDisplay.setPadding(false);

        accordion.add("Qualifications", qualificationDisplay);
    }

    private List<QualificationDTO> getQualifications(Set<UUID> qualificationsIds) {
        return coreClient.getQualifications(qualificationsIds);
    }
}
