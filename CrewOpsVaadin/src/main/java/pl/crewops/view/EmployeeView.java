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
import jakarta.annotation.security.PermitAll;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Scope;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.employee.EmployeeFormModel;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.view.component.EmployeeForm;

@SpringComponent
@Scope("prototype")
@PermitAll
@Route(value = "employees", layout = MainLayout.class)
@PageTitle("Home page")
public class EmployeeView extends VerticalLayout {
    Grid<EmployeeFormModel> grid = new Grid<>(EmployeeFormModel.class);
    TextField filterText = new TextField();
    EmployeeForm form;
    CoreAPI coreAPI;
    List<EmployeeDTO> employees = new ArrayList<>();

    public EmployeeView(CoreAPI coreAPI) {
        addClassName("list-view");

        this.coreAPI = coreAPI;
        employees = coreAPI.getEmployees();

        setSizeFull();
        configureGrid();
        configureForm();

        add(getToolbar(), getContent());
        updateList();
        closeEditor();
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        //        EmployeeDTO first = coreAPI.getEmployees().getFirst();
        form = new EmployeeForm(coreAPI);
        //        form.completeFormData(first);
        //        form.setEmployee(first);
        form.setWidth("25em");

        form.addSaveListener(this::saveEmployee);
        form.addDeleteListener(this::deleteContact);
        form.addCloseListener(e -> closeEditor());
    }

    private void saveEmployee(EmployeeForm.SaveEvent event) {
        coreAPI.createEmployee(EmployeeFormModel.toCreateEmployeeDTO(event.getEmployee()));
        updateList();
        closeEditor();
    }

    private void deleteContact(EmployeeForm.DeleteEvent event) {
        //            service.deleteContact(event.getContact());
        updateList();
        closeEditor();
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

        Button addContactButton = new Button("Add employee");
        addContactButton.addClickListener(click -> addEmployee());

        var toolbar = new HorizontalLayout(filterText, addContactButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    public void editEmployee(EmployeeFormModel employeeFormModel) {
        if (employeeFormModel == null) {
            closeEditor();
        } else {
            form.setEmployee(employeeFormModel);
            form.setVisible(true);
            addClassName("editing");
        }
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
        if (filterText.getValue() == null) {
            grid.setItems(
                    employees.stream().map(EmployeeDTO::toEmployeeFormModel).toList());
        } else {
            grid.setItems(employees.stream()
                    .map(EmployeeDTO::toEmployeeFormModel)
                    .filter(employeeDTO -> employeeDTO
                                    .getFirstName()
                                    .toLowerCase()
                                    .contains(filterText.getValue().toLowerCase())
                            || employeeDTO
                                    .getLastName()
                                    .toLowerCase()
                                    .contains(filterText.getValue().toLowerCase()))
                    .toList());
        }
    }
}
