package pl.crewops.view.component.grid;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.EmployeeFormModel;
import pl.crewops.util.RoleResolver;
import pl.crewops.view.HomeView;
import pl.crewops.view.component.form.EmployeeForm;
import pl.crewops.view.component.notification.guardian.DeleteEmployeeGuardian;
import pl.crewops.view.component.notification.info.AddEmployeeNotification;
import pl.crewops.view.component.notification.info.UpdateEmployeeNotification;

@Getter
@Setter
public class EmployeeGrid extends VerticalLayout {
    private final CoreAPI coreAPI;
    private final RoleResolver roleResolver;

    private final Grid<EmployeeFormModel> grid = new Grid<>();
    private final TextField filter = new TextField();
    private final EmployeeForm form;
    private final Button addEmployee = new Button();
    private QualificationGrid qualificationGrid;

    public EmployeeGrid(CoreAPI coreAPI, RoleResolver roleResolver) {
        this.coreAPI = coreAPI;
        this.roleResolver = roleResolver;
        form = new EmployeeForm(roleResolver);

        configureGrid();
        configureForm();

        localize();

        updateGrid();
        closeEditor();

        setSizeFull();
        add(getToolbar(), getContent());
    }

    private void localize() {
        filter.setPlaceholder(getTranslation("employeeGrid.filter.placeholder"));

        addEmployee.setText(getTranslation("employeeGrid.button.addEmployee"));

        grid.getColumnByKey("firstName").setHeader(getTranslation("employeeGrid.column.firstName"));
        grid.getColumnByKey("lastName").setHeader(getTranslation("employeeGrid.column.lastName"));
        grid.getColumnByKey("birthDate").setHeader(getTranslation("employeeGrid.column.birthDate"));
        grid.getColumnByKey("phoneNumber").setHeader(getTranslation("employeeGrid.column.phoneNumber"));
        grid.getColumnByKey("department").setHeader(getTranslation("employeeGrid.column.department"));
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

        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(event -> updateGrid());

        addEmployee.addClickListener(event -> addEmployee());

        toolbar.add(filter, addEmployee);
        return toolbar;
    }

    private void configureGrid() {
        grid.setSizeFull();

        grid.addColumn(EmployeeFormModel::getFirstName).setKey("firstName");
        grid.addColumn(EmployeeFormModel::getLastName).setKey("lastName");
        grid.addColumn(EmployeeFormModel::getBirthDate).setKey("birthDate");
        grid.addColumn(EmployeeFormModel::getPhoneNumber).setKey("phoneNumber");
        grid.addColumn(EmployeeFormModel::getDepartment).setKey("department");

        grid.getColumns().forEach(column -> column.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event -> editEmployee(event.getValue()));
    }

    private void configureForm() {
        form.setWidth("25em");

        form.addSaveListener(this::saveEmployee);
        form.addUpdateListener(this::updateEmployee);
        form.addDeleteListener(this::deleteEmployee);
        form.addCloseListener(event -> closeEditor());
    }

    public void updateGrid() {
        try {
            List<EmployeeFormModel> employees = coreAPI.getAllEmployees().stream()
                    .map(EmployeeFormModel::toEmployeeFormModel)
                    .toList();

            if (filter.getValue() == null || filter.getValue().isBlank()) {
                grid.setItems(employees);
            } else {
                grid.setItems(employees.stream()
                        .filter(employee -> employee.getFirstName()
                                        .toLowerCase()
                                        .contains(filter.getValue().toLowerCase())
                                || employee.getLastName()
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
            var principal = roleResolver.getPrincipal();
            UUID companyId = principal.getCompanyId();

            Optional<EmployeeDTO> employeeDTO =
                    coreAPI.createEmployee(EmployeeFormModel.toCreateEmployeeDTO(event.getEmployee(), companyId));
            updateGrid();
            closeEditor();
            employeeDTO.ifPresent(AddEmployeeNotification::new);
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
        new DeleteEmployeeGuardian(event.getEmployee(), () -> {
            try {
                coreAPI.terminateEmployeeAccount(event.getEmployee().getId());
                updateGrid();
                qualificationGrid.updateGrid();
                closeEditor();
            } catch (NotAuthenticatedException e) {
                UI.getCurrent().navigate(HomeView.class);
            }
        });
    }
}
