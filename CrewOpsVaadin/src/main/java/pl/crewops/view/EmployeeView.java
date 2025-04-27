package pl.crewops.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.view.form.EmployeeForm;
import pl.crewops.view.model.EmployeeFormModel;

@SpringComponent
@Slf4j
@Scope("prototype")
@Route(value = "employees")
@PageTitle("Employee management")
public class EmployeeView extends VerticalLayout {
    Grid<EmployeeDTO> grid = new Grid<>(EmployeeDTO.class);
    TextField filterText = new TextField();
    EmployeeForm form;
    CoreAPI coreAPI;

    public EmployeeView(CoreAPI coreAPI) {
        addClassName("employee-view");

        this.coreAPI = coreAPI;
        log.info(coreAPI.toString());

        setSizeFull();
        configureGrid();
        configureForm();

        add(getToolbar(), getContent());
        updateList();
        closeEditor();
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.addClassNames("employee-view-content");
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        form = new EmployeeForm(coreAPI);
        form.setWidth("25em");

        form.addSaveListener(this::saveContact);
        form.addDeleteListener(this::deleteContact);
        form.addCloseListener(e -> closeEditor());
    }

    private void configureGrid() {
        grid.addClassNames("employee-grid");
        grid.setSizeFull();
        grid.setColumns("firstName", "lastName", "birthDate", "phoneNumber", "department");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event -> editEmployee(event.getValue()));
    }

    private Component getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addEmployeeButton = new Button("Add employee");
        addEmployeeButton.addClickListener(click -> addEmployee());

        var toolbar = new HorizontalLayout(filterText, addEmployeeButton);
        toolbar.addClassName("employee-toolbar");
        return toolbar;
    }

    public void editEmployee(EmployeeDTO employeeDTO) {
        if (employeeDTO == null) {
            closeEditor();
        } else {
            form.setEmployee(employeeDTO);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void saveContact(EmployeeForm.SaveEvent event) {
        coreAPI.createEmployee(EmployeeFormModel.toCreateEmployeeDTO(event.getEmployee()));
        updateList();
        closeEditor();
    }

    private void deleteContact(EmployeeForm.DeleteEvent event) {
        log.info("Deleting contact {}", event.getEmployee().getFirstName());
        coreAPI.deleteEmployee(event.getEmployee().getId());
        updateList();
        closeEditor();
    }

    private void closeEditor() {
        form.setEmployee(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void addEmployee() {
        grid.asSingleSelect().clear();
        form.setVisible(true);
    }

    private void updateList() {
        List<EmployeeDTO> employees = coreAPI.getAllEmployees();

        if (filterText.getValue() == null) {
            grid.setItems(employees);
        } else {
            grid.setItems(employees.stream()
                    .filter(employeeDTO -> employeeDTO
                                    .firstName()
                                    .toLowerCase()
                                    .contains(filterText.getValue().toLowerCase())
                            || employeeDTO
                                    .lastName()
                                    .toLowerCase()
                                    .contains(filterText.getValue().toLowerCase()))
                    .toList());
        }
    }
}
