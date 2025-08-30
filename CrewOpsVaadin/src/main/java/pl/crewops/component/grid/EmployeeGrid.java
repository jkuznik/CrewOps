package pl.crewops.component.grid;

import static pl.crewops.model.auth.RoleType.*;
import static pl.crewops.model.auth.RoleType.EMPLOYEE;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import pl.crewops.component.form.EmployeeForm;
import pl.crewops.component.notification.AddEmployeeNotification;
import pl.crewops.component.notification.UpdateEmployeeNotification;
import pl.crewops.component.notification.guardian.DeleteEmployeeGuardian;
import pl.crewops.dto.auth.CreateAuthUserResult;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.DepartmentFormModel;
import pl.crewops.model.EmployeeFormModel;
import pl.crewops.model.auth.RoleType;
import pl.crewops.util.AuthenticationResolver;
import pl.crewops.util.BrowserResolver;
import pl.crewops.view.HomeView;

@Getter
@Setter
@CssImport("./styles/component/combo-box.css")
public class EmployeeGrid extends VerticalLayout {
    private final CoreAPI coreAPI;
    private final AuthenticationResolver authenticationResolver;

    private final Grid<EmployeeFormModel> grid = new Grid<>();
    private final TextField nameFilter = new TextField();
    private final TextField departmentFilter = new TextField();
    private ComboBox<RoleType> roleFilter = new ComboBox<>();
    private final EmployeeForm form;
    private final Button addEmployee = new Button();
    private QualificationGrid qualificationGrid;

    public EmployeeGrid(CoreAPI coreAPI, AuthenticationResolver authenticationResolver) {
        this.coreAPI = coreAPI;
        this.authenticationResolver = authenticationResolver;
        form = new EmployeeForm();

        configureGrid();
        configureForm();

        localize();

        updateGrid();
        closeEditor();

        setSizeFull();
        add(getToolbar(), getContent());
    }

    private void localize() {
        nameFilter.setPlaceholder(getTranslation("employeeGrid.nameFilter.placeholder"));
        departmentFilter.setPlaceholder(getTranslation("employeeGrid.departmentFilter.placeholder"));
        roleFilter.setPlaceholder(getTranslation("employeeGrid.roleFilter.placeholder"));

        addEmployee.setText(getTranslation("employeeGrid.button.addEmployee"));

        grid.getColumnByKey("firstName").setHeader(getTranslation("employeeGrid.column.firstName"));
        grid.getColumnByKey("lastName").setHeader(getTranslation("employeeGrid.column.lastName"));
        grid.getColumnByKey("roles").setHeader(getTranslation("employeeGrid.column.roles"));
        grid.getColumnByKey("phoneNumber").setHeader(getTranslation("employeeGrid.column.phoneNumber"));
        grid.getColumnByKey("departments").setHeader(getTranslation("employeeGrid.column.department"));
    }

    public void closeEditor() {
        form.setBinderValue(null);
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

        nameFilter.setClearButtonVisible(true);
        nameFilter.setValueChangeMode(ValueChangeMode.LAZY);
        nameFilter.addValueChangeListener(event -> updateGrid());

        roleFilter.setClearButtonVisible(true);
        roleFilter.setItems(Arrays.stream(values())
                .filter(roleType -> roleType != EMPLOYEE && roleType != SYSTEM_ADMIN)
                .toList());
        roleFilter.setItemLabelGenerator(this::getRoleTranslation);
        roleFilter.addClassName("employee-grid-role-combobox");
        roleFilter.getElement().setAttribute("theme", "role-combo");
        roleFilter.addValueChangeListener(event -> updateGrid());

        departmentFilter.setClearButtonVisible(true);
        departmentFilter.setValueChangeMode(ValueChangeMode.LAZY);
        departmentFilter.addValueChangeListener(event -> updateGrid());

        addEmployee.addClickListener(event -> addEmployee());

        toolbar.add(nameFilter, departmentFilter, roleFilter, addEmployee);
        return toolbar;
    }

    private String getRoleTranslation(RoleType roleType) {
        Map<RoleType, String> collect = Arrays.stream(values())
                .collect(Collectors.toMap(role -> role, role -> switch (role) {
                    case MECHANIC -> getTranslation("roleType.mechanic");
                    case SHIFT_LEADER -> getTranslation("roleType.shiftLeader");
                    case MANAGER -> getTranslation("roleType.manager");
                    case COMPANY_ADMIN -> getTranslation("roleType.companyAdmin");
                    case SYSTEM_ADMIN -> getTranslation("roleType.systemAdmin");
                    default -> "";
                }));
        return collect.get(roleType);
    }

    private void configureGrid() {
        grid.setSizeFull();

        // todo update grid to fetch employee collection ordered by lastname and firtst name
        grid.addColumn(EmployeeFormModel::getFirstName).setKey("firstName");
        grid.addColumn(EmployeeFormModel::getLastName).setKey("lastName");
        grid.addColumn(employee -> employee.getDepartments().stream()
                        .map(DepartmentFormModel::getName)
                        .sorted(String::compareToIgnoreCase)
                        .collect(Collectors.joining(", ")))
                .setKey("departments");
        grid.addColumn(employee -> employee.getRoles().stream()
                        .filter(role -> role != EMPLOYEE)
                        .map(this::getRoleTranslation)
                        .filter(name -> !name.isBlank())
                        .sorted(String::compareToIgnoreCase)
                        .collect(Collectors.joining(", ")))
                .setHeader(getTranslation("roles"))
                .setKey("roles");
        grid.addColumn(EmployeeFormModel::getPhoneNumber).setKey("phoneNumber");

        grid.getColumns().forEach(column -> column.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event -> editEmployee(event.getValue()));
    }

    private void configureForm() {
        if (BrowserResolver.isMobile()) {
            form.setWidthFull();
        } else {
            form.setWidth("25em");
        }

        form.addSaveListener(this::saveEmployee);
        form.addUpdateListener(event -> {
            updateEmployee(event);
            qualificationGrid.updateGrid();
        });
        form.addDeleteListener(this::deleteEmployee);
        form.addCloseListener(event -> closeEditor());
    }

    public void updateGrid() {
        try {
            List<EmployeeFormModel> employees = coreAPI.getAllEmployees().stream()
                    .map(EmployeeFormModel::toEmployeeFormModel)
                    .toList();

            String nameFilterValue =
                    nameFilter.getValue() != null ? nameFilter.getValue().toLowerCase() : "";
            RoleType selectedRole = roleFilter.getValue();
            String selectedDepartment = departmentFilter.getValue();

            grid.setItems(employees.stream()
                    .filter(employee -> {
                        boolean nameMatches = nameFilterValue.isBlank()
                                || employee.getFirstName().toLowerCase().contains(nameFilterValue)
                                || employee.getLastName().toLowerCase().contains(nameFilterValue);

                        boolean roleMatches =
                                (selectedRole == null) || employee.getRoles().contains(selectedRole);

                        // TODO: reimplement this to multiselector
                        boolean departmentMatches = (selectedDepartment == null || selectedDepartment.isBlank())
                                || (employee.getDepartments() != null
                                        && employee.getDepartments()
                                                .contains(DepartmentFormModel.builder()
                                                        .name(selectedDepartment)
                                                        .build()));

                        return nameMatches && roleMatches && departmentMatches;
                    })
                    .toList());
        } catch (NotAuthenticatedException e) {
            UI.getCurrent().navigate(HomeView.class);
        }
    }

    private void editEmployee(EmployeeFormModel employeeFormModel) {
        if (employeeFormModel == null) {
            closeEditor();
        } else {
            form.setBinderValue(employeeFormModel);
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
            var principal = authenticationResolver.getPrincipal();
            UUID companyId = principal.getCompanyId();

            Optional<CreateAuthUserResult> createAuthUserResult =
                    coreAPI.createEmployee(EmployeeFormModel.toCreateEmployeeDTO(event.getEmployee(), companyId));
            updateGrid();
            closeEditor();
            createAuthUserResult.ifPresent(value -> new AddEmployeeNotification(value.employeeDTO()));
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
