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
import pl.crewops.component.dialog.machineDialog.MachineManagerDialog;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.model.EmployeeFormModel;

public class MachineAccordion extends FormLayout {

    public MachineAccordion() {
        addClassName("qualification-accordion");
    }

    public void setValues(EmployeeFormModel employeeFormModel) {
        removeAll();
        Accordion accordion = new Accordion();
        // same i18n key like qualification accordion, update if needed
        var edit = new Button(getTranslation("qualificationAccordion.editButton"));
        var machineManagerDialog = getConfiguredMachineManagerDialog(employeeFormModel);

        edit.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        edit.addClickListener(event -> {
            machineManagerDialog.open();
        });

        List<Span> items = new ArrayList<>();
        employeeFormModel.getMachinesSet().forEach(machine -> {
            String formatted = String.format("%-15s%s", machine.machineType().name(), machine.registerNumber());
            Span span = new Span(formatted);
            span.getStyle().set("font-family", "monospace");
            span.getStyle().set("white-space", "pre");
            items.add(span);
        });

        var machinesDisplay = new VerticalLayout(items.toArray(new Span[items.size()]));
        machinesDisplay.setSpacing(false);
        machinesDisplay.setPadding(false);
        machinesDisplay.add(edit);

        accordion.setVisible(true);
        accordion.close();

        add(accordion);
        accordion.add(getTranslation("machineAccordion.title"), machinesDisplay);
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
