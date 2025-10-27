package pl.crewops.ui.component.accordion;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;
import pl.crewops.model.EmployeeFormModel;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.ui.component.dialog.qualificationManager.QualificationsManagerDialog;

public class QualificationAccordion extends FormLayout {
    public QualificationAccordion() {
        addClassName("qualification-accordion");
    }

    public void setValues(EmployeeFormModel employeeFormModel) {
        removeAll();

        var qualificationsManagerDialog = getConfiguredQualificationManagerDialog(employeeFormModel);

        // === Buttons & subject ===
        Button edit = new Button(getTranslation("qualificationAccordion.editButton"));
        edit.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
        edit.addClickListener(event -> qualificationsManagerDialog.open());

        Span title = new Span(getTranslation("qualificationAccordion.title"));
        title.getStyle().set("font-weight", "600");

        // Toggle button with chevron icons
        Icon closedIcon = VaadinIcon.CHEVRON_RIGHT.create();
        Icon openIcon = VaadinIcon.CHEVRON_DOWN.create();

        Button toggle = new Button(closedIcon);
        toggle.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL);
        toggle.getElement().getStyle().set("min-width", "2.2rem");

        // List of qualifications with separators
        VerticalLayout qualificationDisplay = new VerticalLayout();
        qualificationDisplay.setSpacing(false);
        qualificationDisplay.setPadding(false);
        qualificationDisplay.setVisible(false);

        employeeFormModel.getQualificationsSet().forEach(qualification -> {
            Span span = new Span(qualification.description());

            Div itemWrapper = new Div();
            itemWrapper.add(span);
            itemWrapper.getStyle().set("border-bottom", "1px solid #e0e0e0");
            itemWrapper.getStyle().set("padding", "2px 0");

            qualificationDisplay.add(itemWrapper);
        });

        toggle.addClickListener(ev -> {
            boolean expand = !qualificationDisplay.isVisible();
            qualificationDisplay.setVisible(expand);
            toggle.setIcon(expand ? openIcon : closedIcon);
        });

        // === HEADER LAYOUT ===
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        // Left: toggle + subject, Right: edit
        header.add(toggle, title);
        header.addAndExpand(new Span()); // flexible spacer
        header.add(edit);

        // Add header + collapsible content
        add(header, qualificationDisplay);
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
