package pl.crewops.component.accordion;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import pl.crewops.component.dialog.qualificationManager.QualificationsManagerDialog;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.model.EmployeeFormModel;

public class QualificationAccordion extends FormLayout {
    public QualificationAccordion() {
        addClassName("qualification-accordion");
    }

    public void setValues(EmployeeFormModel employeeFormModel) {
        removeAll();

        var accordion = new Accordion();
        var edit = new Button(getTranslation("qualificationAccordion.editButton"));
        var qualificationsManagerDialog = getConfiguredQualificationManagerDialog(employeeFormModel);

        edit.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        edit.addClickListener(event -> {
            qualificationsManagerDialog.open();
        });

        List<Span> accordionItems = new ArrayList<>();
        employeeFormModel.getQualificationsSet().forEach(qualification -> {
            accordionItems.add(new Span(qualification.description()));
        });

        var qualificationDisplay = new VerticalLayout(accordionItems.toArray(new Span[accordionItems.size()]));
        qualificationDisplay.setSpacing(false);
        qualificationDisplay.setPadding(false);
        qualificationDisplay.add(edit);

        accordion.setVisible(true);
        accordion.close();

        add(accordion);
        accordion.add(getTranslation("qualificationAccordion.title"), qualificationDisplay);
    }

    private QualificationsManagerDialog getConfiguredQualificationManagerDialog(EmployeeFormModel employeeFormModel) {
        var qualificationsManagerDialog = new QualificationsManagerDialog(employeeFormModel);
        qualificationsManagerDialog.addUpdateQualificationsListener(event -> {
            fireEvent(new UpdateQualificationsEvent(this, event.getEmployeeDTO()));
        });
        return qualificationsManagerDialog;
    }

    public abstract static class QualificationAccordionEvent extends ComponentEvent<QualificationAccordion> {

        public QualificationAccordionEvent(QualificationAccordion source) {
            super(source, false);
        }
    }

    public static class UpdateQualificationsEvent extends QualificationAccordionEvent {
        @Getter
        private final EmployeeDTO employeeDTO;

        public UpdateQualificationsEvent(QualificationAccordion source, EmployeeDTO employeeDTO) {
            super(source);
            this.employeeDTO = employeeDTO;
        }
    }

    public Registration addUpdateQualificationsListener(ComponentEventListener<UpdateQualificationsEvent> listener) {
        return addListener(UpdateQualificationsEvent.class, listener);
    }
}
