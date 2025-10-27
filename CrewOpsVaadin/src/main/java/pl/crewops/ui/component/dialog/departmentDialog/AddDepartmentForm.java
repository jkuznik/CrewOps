package pl.crewops.ui.component.dialog.departmentDialog;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.shared.Registration;
import java.util.Comparator;
import lombok.Getter;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.exceptions.UpdateDepartmentException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.EmployeeFormModel;
import pl.crewops.model.dto.department.DepartmentDTO;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.ui.component.content.HomeContent;
import pl.crewops.ui.component.custom.ComboBoxCustom;
import pl.crewops.ui.component.notification.FailNotification;
import pl.crewops.util.SpringContextBridge;

public class AddDepartmentForm extends FormLayout {

    private final ComboBoxCustom<DepartmentDTO> departments = new ComboBoxCustom<>();
    // same i18n like in AddQualificationForm, update if needed
    private final Button add = new Button(getTranslation("addQualificationForm.addButton"));

    public AddDepartmentForm(EmployeeFormModel employeeFormModel) {
        addClassName("addDepartmentForm");

        var coreAPI = SpringContextBridge.getBean(CoreAPI.class);

        configureDepartments(coreAPI);
        configureAddButtonClickListener(employeeFormModel, coreAPI);

        add(departments, add);
    }

    private void configureDepartments(CoreAPI coreAPI) {
        departments.setItemLabelGenerator(DepartmentDTO::name);
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
            } catch (UpdateDepartmentException ex) {
                new FailNotification(ex.getMessage());
            } catch (NotAuthenticatedException ex) {
                new FailNotification(ex.getMessage());
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
