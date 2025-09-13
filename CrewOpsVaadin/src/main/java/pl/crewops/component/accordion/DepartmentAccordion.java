package pl.crewops.component.accordion;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import pl.crewops.component.dialog.departmentDialog.DepartmentManagerDialog;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.model.EmployeeFormModel;

public class DepartmentAccordion extends FormLayout {

    public DepartmentAccordion() {
        addClassName("departments-accordion");
    }

    public void setValues(EmployeeFormModel employeeFormModel) {
        removeAll();

        var departmentManagerDialog = getConfiguredDepartmentManagerDialog(employeeFormModel);

        // === Buttons & title ===
        Button edit = new Button(getTranslation("qualificationAccordion.editButton"));
        edit.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
        edit.addClickListener(event -> departmentManagerDialog.open());

        Span title = new Span(getTranslation("departmentAccordion.title"));
        title.getStyle().set("font-weight", "600");

        // Toggle button with chevron icons
        Icon closedIcon = VaadinIcon.CHEVRON_RIGHT.create();
        Icon openIcon = VaadinIcon.CHEVRON_DOWN.create();

        Button toggle = new Button(closedIcon);
        toggle.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL);
        toggle.getElement().getStyle().set("min-width", "2.2rem");

        // List of departments
        List<Span> items = new ArrayList<>();
        employeeFormModel.getDepartments().forEach(department -> {
            Span span = new Span(department.getName());
            span.getStyle().set("font-family", "monospace");
            span.getStyle().set("white-space", "pre");
            items.add(span);
        });

        VerticalLayout departmentDisplay = new VerticalLayout(items.toArray(new Span[0]));
        departmentDisplay.setSpacing(false);
        departmentDisplay.setPadding(false);
        departmentDisplay.setVisible(false);

        toggle.addClickListener(ev -> {
            boolean expand = !departmentDisplay.isVisible();
            departmentDisplay.setVisible(expand);
            toggle.setIcon(expand ? openIcon : closedIcon);
        });

        // === HEADER LAYOUT ===
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        // Left: toggle + title, Right: edit
        header.add(toggle, title);
        header.addAndExpand(new Span()); // flexible spacer
        header.add(edit);

        // Add header + collapsible content
        add(header, departmentDisplay);
    }

    private DepartmentManagerDialog getConfiguredDepartmentManagerDialog(EmployeeFormModel employeeFormModel) {
        var departmentManagerDialog = new DepartmentManagerDialog(employeeFormModel);
        departmentManagerDialog.addUpdateDepartmentListener(event -> {
            fireEvent(new UpdateDepartmentEvent(this, event.getEmployeeDTO()));
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
