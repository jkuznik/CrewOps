package pl.crewops.view.component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import java.util.List;
import java.util.Optional;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.view.HomeView;
import pl.crewops.view.component.notification.SaveEmployeeNotification;
import pl.crewops.view.component.notification.UpdateEmployeeNotification;
import pl.crewops.view.form.EmployeeForm;
import pl.crewops.view.form.model.EmployeeFormModel;

public class EmployeeGrid extends VerticalLayout {
    private final CoreAPI coreAPI;

    private final Grid<EmployeeFormModel> grid = new Grid<>(EmployeeFormModel.class);
    private final TextField filter = new TextField();
    private final EmployeeForm form = new EmployeeForm();

    public EmployeeGrid(CoreAPI coreAPI) {
        this.coreAPI = coreAPI;

        configureGrid();
        configureForm();

        updateGrid();
        closeEditor();

        setSizeFull();
        add(getToolbar(), getContent());
    }

    public void addEmployeeEvent() {
        addEmployee();
    }

    public void closeEditor() {
        form.setEmployee(null);
        form.setVisible(false);
    }

    private HorizontalLayout getContent() {
        var content = new HorizontalLayout(grid, form);
        content.setSizeFull();
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        return content;
    }

    private HorizontalLayout getToolbar() {
        var toolbar = new HorizontalLayout();

        filter.setPlaceholder("Filter by name");
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(event -> updateGrid());

        Button addEmployee = new Button("Add Employee");
        addEmployee.addClickListener(event -> addEmployee());

        toolbar.add(filter, addEmployee);

        return toolbar;
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.setColumns("firstName", "lastName", "birthDate", "phoneNumber", "department");
        grid.getColumns().forEach(column -> column.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event -> {
            editEmployee(event.getValue());
        });
    }

    private void configureForm() {
        form.setWidth("25em");

        form.addSaveListener(this::saveEmployee);
        form.addUpdateListener(this::updateEmployee);
        form.addDeleteListener(this::deleteEmployee);
        form.addCloseListener(event -> {
            closeEditor();
        });
    }

    private void updateGrid() {
        try {
            List<EmployeeFormModel> employees = coreAPI.getAllEmployees().stream()
                    .map(EmployeeFormModel::toEmployeeFormModel)
                    .toList();

            if (filter.getValue() == null) {
                grid.setItems(employees);
            } else {
                grid.setItems(employees.stream()
                        .filter(employeeDTO -> employeeDTO
                                        .getFirstName()
                                        .toLowerCase()
                                        .contains(filter.getValue().toLowerCase())
                                || employeeDTO
                                        .getLastName()
                                        .toLowerCase()
                                        .contains(filter.getValue().toLowerCase()))
                        .toList());
            }
        } catch (NotAuthenticatedException e) {
            UI.getCurrent().navigate(HomeView.class);
        }
    }

    private void editEmployee(EmployeeFormModel employeeFormModel) {
        if (employeeFormModel == null) {
            closeEditor();
        } else {
            form.setEmployee(employeeFormModel);
            form.setFormModeUpdate();
            form.setVisible(true);
        }
    }

    private void addEmployee() {
        grid.asSingleSelect().clear();
        form.setFormModeSave();
        form.setVisible(true);
    }

    private void saveEmployee(EmployeeForm.SaveEvent event) {
        try {
            Optional<EmployeeDTO> employeeDTO =
                    coreAPI.createEmployee(EmployeeFormModel.toCreateEmployeeDTO(event.getEmployee()));
            updateGrid();
            closeEditor();
            employeeDTO.ifPresent(SaveEmployeeNotification::new);
        } catch (NotAuthenticatedException e) {
            UI.getCurrent().navigate(HomeView.class);
        }
    }

    private void updateEmployee(EmployeeForm.UpdateEvent event) {
        try {
            Optional<EmployeeDTO> employeeDTO =
                    coreAPI.updateEmployee(EmployeeFormModel.toUpdateEmployeeDTO(event.getEmployee()));
            updateGrid();
            closeEditor();
            employeeDTO.ifPresent(UpdateEmployeeNotification::new);
        } catch (NotAuthenticatedException e) {
            UI.getCurrent().navigate(HomeView.class);
        }
    }

    private void deleteEmployee(EmployeeForm.DeleteEvent event) {
        try {
            coreAPI.deleteEmployee(event.getEmployee().getId());
            updateGrid();
            closeEditor();
        } catch (NotAuthenticatedException e) {
            UI.getCurrent().navigate(HomeView.class);
        }
    }
}
