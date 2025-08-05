package pl.crewops.view.component.accordion;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.ArrayList;
import java.util.List;
import pl.crewops.model.EmployeeFormModel;
import pl.crewops.view.component.dialog.QualificationsManagerDialog;

public class QualificationAccordion extends FormLayout {
    public QualificationAccordion() {
        addClassName("qualification-accordion");
    }

    public void getValues(EmployeeFormModel employeeFormModel) {
        removeAll();

        Accordion accordion = new Accordion();
        // TODO: i18n
        Button edit = new Button("edit");

        edit.addClickListener(event -> {
            new QualificationsManagerDialog(employeeFormModel);
        });

        List<Span> items = new ArrayList<>();

        employeeFormModel.getQualificationsSet().forEach(qualification -> {
            items.add(new Span(qualification.description()));
        });

        var qualificationDisplay = new VerticalLayout(items.toArray(new Span[items.size()]));
        qualificationDisplay.setSpacing(false);
        qualificationDisplay.setPadding(false);
        qualificationDisplay.add(edit);

        accordion.setVisible(true);
        add(accordion);
        accordion.add(getTranslation("qualificationAccordion.title"), qualificationDisplay);
    }
}
