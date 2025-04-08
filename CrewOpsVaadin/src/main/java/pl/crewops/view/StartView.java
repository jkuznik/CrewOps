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
import pl.crewops.infrastructure.core.CoreClient;

@SpringComponent
@Scope("prototype")
@PermitAll
@Route(value = "", layout = MainLayout.class)
@PageTitle("Home page")
public class StartView extends VerticalLayout {
    Grid<EmployeeDTO> grid = new Grid<>(EmployeeDTO.class);
    TextField filterText = new TextField();
    EmployeeForm form;
    CoreClient coreClient;
    List<EmployeeDTO> employees = new ArrayList<>();
    //    CrmService service;

    public StartView(CoreClient coreClient) {
        this.coreClient = coreClient;
        addClassName("list-view");
        employees = coreClient.getEmployees();

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
        EmployeeDTO first = coreClient.getEmployees().getFirst();
        form = new EmployeeForm(coreClient);
        form.completeFormData(first);
        form.setEmployee(first);
        form.setWidth("25em");

        form.addSaveListener(this::saveContact); // <1>
        form.addDeleteListener(this::deleteContact); // <2>
        form.addCloseListener(e -> closeEditor()); // <3>
    }

    private void saveContact(EmployeeForm.SaveEvent event) {
        //            service.saveContact(event.getContact());
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

        grid.asSingleSelect().addValueChangeListener(event -> employeeForm(event.getValue()));
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

    public void employeeForm(EmployeeDTO employeeDTO) {
        if (employeeDTO == null) {
            closeEditor();
        } else {
            form.setEmployee(employeeDTO);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setEmployee(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    public void editContact(EmployeeDTO employeeDTO) {
        if (employeeDTO == null) {
            closeEditor();
        } else {
            form.setEmployee(employeeDTO);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    // TODO: implement logic
    private void addEmployee() {
        grid.asSingleSelect().clear();
        form.setVisible(true);
    }

    private void updateList() {
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
