package pl.crewops.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.List;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtInfoService;
import pl.crewops.view.component.mainLayout.MainLayout;
import pl.crewops.view.form.EmployeeForm;
import pl.crewops.view.form.model.EmployeeFormModel;

@Route(value = "employees")
@PageTitle("Employee management")
public class EmployeeView extends MainLayout implements BeforeEnterObserver {
    Grid<EmployeeFormModel> grid = new Grid<>(EmployeeFormModel.class);
    TextField filterText = new TextField();
    EmployeeForm form;

    public EmployeeView(CoreAPI coreAPI, JwtInfoService jwtInfoService) {
        super(coreAPI, jwtInfoService);
        addClassName("employee-view");
        VerticalLayout currentContent = new VerticalLayout();
        currentContent.setId("view-content");

        mainContent.removeAll();
        mainContent.add(currentContent, mainFooter);
        mainContent.setFlexGrow(1, currentContent);

        currentContent.setSizeFull();
        currentContent.setPadding(true);
        currentContent.setSpacing(true);
        currentContent.getStyle().set("overflow", "auto");

        configureGrid();
        configureForm();

        currentContent.add(getToolbar(), getCurrentContent());

        updateGrid();
        closeEditor();
    }

    private Component getToolbar() {
        filterText.setLabel("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateGrid());

        Button addEmployeeButton = new Button("Add employee");
        addEmployeeButton.addClickListener(click -> addEmployee());

        var toolbar = new HorizontalLayout(filterText, addEmployeeButton);
        toolbar.addClassName("employee-toolbar");
        toolbar.setAlignItems(FlexComponent.Alignment.END);
        return toolbar;
    }

    private HorizontalLayout getCurrentContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.addClassNames("employee-view-content");
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.setSizeFull();
        return content;
    }

    private void configureGrid() {
        grid.addClassNames("employee-grid");
        grid.setSizeFull();
        grid.setColumns("firstName", "lastName", "birthDate", "phoneNumber", "department");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event -> editEmployee(event.getValue()));
    }

    private void configureForm() {
        form = new EmployeeForm();
        form.setWidth("25em");

        form.addSaveListener(this::saveContact);
        form.addUpdateListener(this::updateContact);
        form.addDeleteListener(this::deleteContact);
        form.addCloseListener(e -> closeEditor());
    }

    private void updateGrid() {
        try {
            List<EmployeeFormModel> employees = coreAPI.getAllEmployees().stream()
                    .map(EmployeeFormModel::toEmployeeFormModel)
                    .toList();

            if (filterText.getValue() == null) {
                grid.setItems(employees);
            } else {
                grid.setItems(employees.stream()
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
        } catch (NotAuthenticatedException e) {
            // TODO: implement logic, redirect to home view and display notification
        }
    }

    private void editEmployee(EmployeeFormModel employeeFormModel) {
        if (employeeFormModel == null) {
            closeEditor();
        } else {
            form.setEmployee(employeeFormModel);
            form.setFormModeEdit();
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setEmployee(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void saveContact(EmployeeForm.SaveEvent event) {
        try {
            coreAPI.createEmployee(EmployeeFormModel.toCreateEmployeeDTO(event.getEmployee()));
            updateGrid();
            closeEditor();
            // TODO: implement notification
        } catch (NotAuthenticatedException e) {
            // TODO: implement logic
        }
    }

    private void updateContact(EmployeeForm.UpdateEvent event) {
        try {
            coreAPI.updateEmployee(EmployeeFormModel.toUpdateEmployeeDTO(event.getEmployee()));
            updateGrid();
            closeEditor();
            // TODO: implement notification
        } catch (NotAuthenticatedException e) {
            // TODO: implement logic
        }
    }

    private void deleteContact(EmployeeForm.DeleteEvent event) {
        try {
            coreAPI.deleteEmployee(event.getEmployee().getId());
            updateGrid();
            closeEditor();
        } catch (NotAuthenticatedException e) {
            // TODO: implement logic
        }
    }

    private void addEmployee() {
        grid.asSingleSelect().clear();
        form.setFormModeSave();
        form.setVisible(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!jwtInfoService.validToken()) {
            event.forwardTo(HomeView.class);
            UI.getCurrent().getPage().setLocation("/");
        }
    }
}
