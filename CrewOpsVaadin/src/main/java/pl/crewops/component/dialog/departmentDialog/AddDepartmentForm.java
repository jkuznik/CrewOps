package pl.crewops.component.dialog.departmentDialog;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.shared.Registration;
import java.util.Comparator;
import lombok.Getter;
import pl.crewops.component.content.HomeContent;
import pl.crewops.component.notification.FailNotification;
import pl.crewops.dto.department.DepartmentDTO;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.EmployeeFormModel;
import pl.crewops.util.SpringContextBridge;

public class AddDepartmentForm extends FormLayout {

    private final ComboBox<DepartmentDTO> departments = new ComboBox<>();
    // same i18n like in AddQualificationForm, update if needed
    private final Button add = new Button(getTranslation("addQualificationForm.addButton"));

    public AddDepartmentForm(EmployeeFormModel employeeFormModel) {
        addClassName("addDepartmentForm");

        departments.addClassName("dark-combo");
        departments.getElement().setAttribute("theme", "dark-combo");

        var coreAPI = SpringContextBridge.getBean(CoreAPI.class);

        configureDepartments(coreAPI);
        configureAddButtonClickListener(employeeFormModel, coreAPI);

        add(departments, add);
    }

    private void configureDepartments(CoreAPI coreAPI) {
        departments.setItemLabelGenerator(DepartmentDTO::name);
        // todo i18n
        departments.setPlaceholder(getTranslation("addDepartmentForm.departments"));

        departments.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                add.setEnabled(true);
            }
        });

        populateDepartments(coreAPI);
    }

    private void configureAddButtonClickListener(EmployeeFormModel employeeFormModel, CoreAPI coreAPI) {
        add.setEnabled(false);
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add.setMaxWidth("200px");
        add.setWidth("100%");

        add.addClickListener(e -> {
            try {
                var employeeDTO = coreAPI.addEmployeeDepartment(
                                employeeFormModel.getId(),
                                departments.getValue().id())
                        .orElseThrow(UpdateDepartmentException::new);
                fireEvent(new AddDepartmentEvent(this, employeeDTO));
            } catch (UpdateDepartmentException e) {
                new FailNotification(e.getMessage());
            } catch (NotAuthenticatedException e) {
                new FailNotification(e.getMessage());
                UI.getCurrent().navigate(HomeContent.class);
            }
        });
    }

    private void populateDepartments(CoreAPI coreAPI) {
        try {
            departments.setItems(coreAPI.getAllDepartments().stream()
                    .sorted(Comparator.comparing(DepartmentDTO::name))
                    .toList());
        } catch (NotAuthenticatedException e) {
            new FailNotification(e.getMessage());
            UI.getCurrent().navigate(HomeContent.class);
        }
    }

    public abstract static class AddDepartmentFormEvent extends ComponentEvent<AddDepartmentForm> {
        public AddDepartmentFormEvent(AddDepartmentForm source) {
            super(source, false);
        }
    }

    public static class AddDepartmentEvent extends AddDepartmentFormEvent {
        @Getter
        private final EmployeeDTO employeeDTO;

        public AddDepartmentEvent(AddDepartmentForm source, EmployeeDTO employeeDTO) {
            super(source);
            this.employeeDTO = employeeDTO;
        }
    }

    public Registration addUpdateDepartmentListener(ComponentEventListener<AddDepartmentEvent> listener) {
        return addListener(AddDepartmentEvent.class, listener);
    }
}
