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
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.model.EmployeeFormModel;

public class DepartmentAccordion extends FormLayout {

    public DepartmentAccordion() {
        addClassName("departments-accordion");
    }

    public void setValues(EmployeeFormModel employeeFormModel) {
        removeAll();
        var accordion = new Accordion();
        // TODO : i18n
        var edit = new Button("Edit");
        var departmentManagerDialog = getConfiguredDepartmentManagerDialog(employeeFormModel);

        edit.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        edit.addClickListener(event -> {
            departmentManagerDialog.open();
        });

        List<Span> items = new ArrayList<>();
        employeeFormModel.getDepartments().forEach(department -> {
            var departmentName = department.getName();
            var span = new Span();
            span.getStyle().set("font-family", "monospace");
            span.getStyle().set("white-space", "pre");
            items.add(span);
        });

        var departmentDisplay = new VerticalLayout(items.toArray(new Span[items.size()]));
        departmentDisplay.setSpacing(false);
        departmentDisplay.setMargin(false);
        departmentDisplay.add(edit);

        accordion.setVisible(true);
        accordion.close();

        add(accordion);
        // todo i18n accordion title
        accordion.add("Departments", departmentDisplay);
    }

    private DepartmentManagerDialog getConfiguredDepartmentManagerDialog(EmployeeFormModel employeeFormModel) {
        var departmentManagerDialog = new DepartmentManagerDialog(employeeFormModel);
        departmentManagerDialog.addUpdateDepartmentListener(event -> {
            fireEvent(new UpdateDepartmentEvent(this, event.getEmployeeDTO));
        });

        return departmentManagerDialog;
    }

    public abstract static class DepartmentAccordionEvent extends ComponentEvent<DepartmentAccordion> {
        public DepartmentAccordionEvent(DepartmentAccordion source) {
            super(source, false);
        }
    }

    public static class UpdateDepartmentEvent extends DepartmentAccordionEvent {
        @Getter
        private final EmployeeDTO employeeDTO;

        public UpdateDepartmentEvent(DepartmentAccordion source, EmployeeDTO employeeDTO) {
            super(source);
            this.employeeDTO = employeeDTO;
        }
    }

    public Registration addUpdateDepartmentListener(ComponentEventListener<UpdateDepartmentEvent> listener) {
        return addListener(UpdateDepartmentEvent.class, listener);
    }
}
