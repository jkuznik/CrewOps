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
import pl.crewops.ui.component.dialog.machineDialog.MachineManagerDialog;

public class MachineAccordion extends FormLayout {

    public MachineAccordion() {
        addClassName("qualification-accordion");
    }

    public void setValues(EmployeeFormModel employeeFormModel) {
        removeAll();

        var machineManagerDialog = getConfiguredMachineManagerDialog(employeeFormModel);

        // === Buttons & subject ===
        Button edit = new Button(getTranslation("qualificationAccordion.editButton"));
        edit.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
        edit.addClickListener(event -> machineManagerDialog.open());

        Span title = new Span(getTranslation("machineAccordion.title"));
        title.getStyle().set("font-weight", "600");

        // Toggle button with icon instead of text
        Icon closedIcon = VaadinIcon.CHEVRON_RIGHT.create();
        Icon openIcon = VaadinIcon.CHEVRON_DOWN.create();

        Button toggle = new Button(closedIcon);
        toggle.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL);
        toggle.getElement().getStyle().set("min-width", "2.2rem");

        // List of machines with separators
        VerticalLayout machinesDisplay = new VerticalLayout();
        machinesDisplay.setSpacing(false);
        machinesDisplay.setPadding(false);
        machinesDisplay.setVisible(false);

        employeeFormModel.getMachinesSet().forEach(machine -> {
            String formatted = String.format("%-15s%s", machine.machineType().name(), machine.registerNumber());
            Span span = new Span(formatted);
            span.getStyle().set("font-family", "monospace");
            span.getStyle().set("white-space", "pre");
            span.getStyle().set("padding", "2px 0");

            Div itemWrapper = new Div();
            itemWrapper.add(span);
            itemWrapper.getStyle().set("border-bottom", "1px solid #e0e0e0");
            itemWrapper.getStyle().set("padding-bottom", "2px");

            machinesDisplay.add(itemWrapper);
        });

        toggle.addClickListener(ev -> {
            boolean expand = !machinesDisplay.isVisible();
            machinesDisplay.setVisible(expand);
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
        add(header, machinesDisplay);
    }

    private MachineManagerDialog getConfiguredMachineManagerDialog(EmployeeFormModel employeeFormModel) {
        var machineManagerDialog = new MachineManagerDialog(employeeFormModel);
        machineManagerDialog.addUpdateMachineListener(event -> {
            fireEvent(new UpdateMachineEvent(this, event.getEmployeeDTO()));
        });

        return machineManagerDialog;
    }

    public abstract static class MachineAccordionEvent extends ComponentEvent<MachineAccordion> {
        public MachineAccordionEvent(MachineAccordion source) {
            super(source, false);
        }
    }

    public static class UpdateMachineEvent extends MachineAccordionEvent {
        @Getter
        private final EmployeeDTO employeeDTO;

        public UpdateMachineEvent(MachineAccordion source, EmployeeDTO employeeDTO) {
            super(source);
            this.employeeDTO = employeeDTO;
        }
    }

    public Registration addUpdateMachineListener(ComponentEventListener<UpdateMachineEvent> listener) {
        return addListener(UpdateMachineEvent.class, listener);
    }
}
