package pl.crewops.ui.component.dialog.departmentDialog;

import static pl.crewops.model.DepartmentFormModel.mapToDepartmentDTOs;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.shared.Registration;
import java.util.stream.Collectors;
import lombok.Getter;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.DepartmentFormModel;
import pl.crewops.model.EmployeeFormModel;
import pl.crewops.model.dto.auth.RoleDTO;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.ui.component.notification.FailNotification;
import pl.crewops.ui.view.HomeView;
import pl.crewops.util.SpringContextBridge;

class DepartmentManagerGrid extends VerticalLayout {
    private final Grid<DepartmentFormModel> grid = new Grid<>();

    public DepartmentManagerGrid(EmployeeFormModel employeeFormModel, AddDepartmentForm addDepartmentForm) {
        addClassName("departmentManagerGrid");
        setSizeFull();

        var coreAPI = SpringContextBridge.getBean(CoreAPI.class);

        configureGrid(employeeFormModel, coreAPI);
        populateGrid(employeeFormModel);

        addDepartmentForm.addUpdateDepartmentListener(event -> {
            updateGrid(event.getEmployeeDTO());
        });

        H1 employeeNameHolder = new H1();
        employeeNameHolder.setText(employeeFormModel.getFirstName() + " " + employeeFormModel.getLastName() + " - "
                + getTranslation("departmentManagerGrid.employeeNameHolder"));

        add(employeeNameHolder, grid);
    }

    private void configureGrid(EmployeeFormModel employeeFormModel, CoreAPI coreAPI) {
        grid.setSizeFull();
        grid.setMinWidth("300px");
        grid.setMaxWidth("100%");
        grid.addClassName("department-grid");

        // Department name column
        grid.addColumn(new ComponentRenderer<>(department -> {
                    Div div = new Div();
                    div.setText(department.getName());
                    div.getStyle()
                            .set("white-space", "normal")
                            .set("overflow-wrap", "break-word")
                            .set("font-size", "0.9rem");
                    return div;
                }))
                .setHeader(getTranslation("departmentManagerGrid.departmentHeader"))
                .setKey("name")
                .setFlexGrow(1)
                .setResizable(true);

        // Unassign button column
        grid.addColumn(new ComponentRenderer<>(department -> {
            Div container = new Div();
            Button removeButton = new Button(getTranslation("machineManagerGrid.unassigned"));
            removeButton.addClickListener(ev -> {
                try {
                    coreAPI.removeEmployeeDepartment(employeeFormModel.getId(), department.getId());
                    updateGrid(processedEmployeeDTO(employeeFormModel, department));
                    fireEvent(new UpdateDepartmentEvent(this, processedEmployeeDTO(employeeFormModel, department)));
                } catch (NotAuthenticatedException e) {
                    new FailNotification(e.getMessage());
                    UI.getCurrent().navigate(HomeView.class);
                }
            });
            container.add(removeButton);
            return container;
        }));
    }

    private void populateGrid(EmployeeFormModel employeeFormModel) {
        // Use actual departments from EmployeeFormModel
        grid.setItems(employeeFormModel.getDepartments());
    }

    private void updateGrid(EmployeeDTO employeeDTO) {
        if (employeeDTO != null) {
            var departments = employeeDTO.departments().stream()
                    .map(d -> new DepartmentFormModel(d.id(), d.name()))
                    .collect(Collectors.toList());
            grid.setItems(departments);
        }
    }

    private EmployeeDTO processedEmployeeDTO(
            EmployeeFormModel employeeFormModel, DepartmentFormModel departmentFormModel) {
        var remainingDepartments = employeeFormModel.getDepartments().stream()
                .filter(d -> !d.getId().equals(departmentFormModel.getId()))
                .collect(Collectors.toSet());

        return EmployeeDTO.builder()
                .id(employeeFormModel.getId())
                .firstName(employeeFormModel.getFirstName())
                .lastName(employeeFormModel.getLastName())
                .departments(mapToDepartmentDTOs(remainingDepartments))
                .roles(employeeFormModel.getRoles().stream()
                        .map(r -> new RoleDTO(r.name()))
                        .collect(Collectors.toSet()))
                .qualifications(employeeFormModel.getQualificationsSet())
                .machines(employeeFormModel.getMachinesSet())
                .build();
    }

    public abstract static class DepartmentManagerGridEvent extends ComponentEvent<DepartmentManagerGrid> {
        public DepartmentManagerGridEvent(DepartmentManagerGrid source) {
            super(source, false);
        }
    }

    public static class UpdateDepartmentEvent extends DepartmentManagerGridEvent {
        @Getter
        private final EmployeeDTO employeeDTO;

        public UpdateDepartmentEvent(DepartmentManagerGrid source, EmployeeDTO employeeDTO) {
            super(source);
            this.employeeDTO = employeeDTO;
        }
    }

    public Registration addUpdateDepartmentEvent(ComponentEventListener<UpdateDepartmentEvent> listener) {
        return addListener(UpdateDepartmentEvent.class, listener);
    }
}
