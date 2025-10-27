package pl.crewops.ui.component.dialog.machineDialog;

import static pl.crewops.model.DepartmentFormModel.mapToDepartmentDTOs;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.shared.Registration;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.EmployeeFormModel;
import pl.crewops.model.MachineFormModel;
import pl.crewops.model.dto.auth.RoleDTO;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.model.dto.machine.MachineDTO;
import pl.crewops.ui.component.notification.FailNotification;
import pl.crewops.util.SpringContextBridge;

public class MachineMangerGrid extends VerticalLayout {
    private final Grid<MachineFormModel> grid = new Grid<>();
    private Set<UUID> employeeMachines;

    public MachineMangerGrid(EmployeeFormModel employeeFormModel, AddMachineForm addMachineForm) {
        addClassName("machineMangerGrid");

        setSizeFull();

        var coreAPI = SpringContextBridge.getBean(CoreAPI.class);
        employeeMachines =
                employeeFormModel.getMachinesSet().stream().map(MachineDTO::id).collect(Collectors.toSet());

        configureGrid(employeeFormModel, coreAPI);

        populateGrid(employeeFormModel, coreAPI);

        addMachineForm.addUpdateMachineListener(event -> {
            updateGrid(event.getEmployeeDTO(), coreAPI);
        });

        H1 employeeNameHolder = new H1();
        employeeNameHolder.setText(employeeFormModel.getFirstName() + " " + employeeFormModel.getLastName() + " - "
                + getTranslation("machineManagerGrid.employeeNameHolder"));

        add(employeeNameHolder, grid);
    }

    private void configureGrid(EmployeeFormModel employeeFormModel, CoreAPI coreAPI) {
        grid.setSizeFull();

        grid.setMinWidth("300px");
        grid.setMaxWidth("100%");
        grid.addClassName("machine-grid");

        grid.addColumn(new ComponentRenderer<>(machine -> {
                    Div registerNumberDiv = new Div();
                    registerNumberDiv.setText(machine.getRegisterNumber());
                    registerNumberDiv
                            .getStyle()
                            .set("white-space", "normal")
                            .set("overflow-wrap", "break-word")
                            .set("font-size", "0.9rem");

                    return registerNumberDiv;
                }))
                .setHeader(getTranslation("machineManagerGrid.registerNumber"))
                .setKey("registerNumber")
                .setFlexGrow(1)
                .setAutoWidth(false)
                .setResizable(true);

        grid.addColumn(new ComponentRenderer<>(machine -> {
                    if (machine.getBroken() != null && machine.getBroken()) {
                        Icon redIcon = VaadinIcon.CLOSE_CIRCLE.create();
                        redIcon.setColor("red");
                        return redIcon;
                    } else {
                        Icon greenIcon = VaadinIcon.CHECK_CIRCLE.create();
                        greenIcon.setColor("green");
                        return greenIcon;
                    }
                }))
                .setHeader(getTranslation("machineManagerGrid.broken"))
                .setKey("broken");

        grid.addColumn(new ComponentRenderer<>(machine -> {
            Div unassignedDiv = new Div();
            var unassignButton = new Button(getTranslation("machineManagerGrid.unassigned"));

            unassignButton.addClickListener(event -> {
                try {
                    coreAPI.removeEmployeeMachine(employeeFormModel.getId(), machine.getId());
                    updateGrid(processedEmployeeDTO(employeeFormModel, machine), coreAPI);
                    fireEvent(new UpdateMachineEvent(this, processedEmployeeDTO(employeeFormModel, machine)));
                } catch (NotAuthenticatedException e) {
                    new FailNotification(e.getMessage());
                    UI.getCurrent().getPage().setLocation("/");
                }
            });

            unassignedDiv.add(unassignButton);
            return unassignedDiv;
        }));

        // TODO: consider to add column with button to allow open list of others employee assignment to current machine
    }

    private void populateGrid(EmployeeFormModel employeeFormModel, CoreAPI coreAPI) {
        try {
            var machines = coreAPI
                    .getAllEmployeeMachinesByIds(employeeFormModel.getMachinesSet().stream()
                            .map(MachineDTO::id)
                            .collect(Collectors.toSet()))
                    .stream()
                    .map(MachineFormModel::toMachineFormModel)
                    .toList();
            grid.setItems(machines);
        } catch (NotAuthenticatedException e) {
            new FailNotification(e.getMessage());
            UI.getCurrent().getPage().setLocation("/");
        }
    }

    private void updateGrid(EmployeeDTO employeeDTO, CoreAPI coreAPI) {
        try {
            if (employeeDTO != null) {
                employeeMachines =
                        employeeDTO.machines().stream().map(MachineDTO::id).collect(Collectors.toSet());
            }
            var machines = coreAPI.getAllEmployeeMachinesByIds(employeeMachines).stream()
                    .map(MachineFormModel::toMachineFormModel)
                    .toList();
            grid.setItems(machines);
        } catch (NotAuthenticatedException e) {
            new FailNotification(e.getMessage());
            UI.getCurrent().getPage().setLocation("/");
        }
    }

    private EmployeeDTO processedEmployeeDTO(EmployeeFormModel employeeFormModel, MachineFormModel machineFormModel) {

        var employeeMachines = employeeFormModel.getMachinesSet();
        var machineToRemove = machineFormModel.getId();
        Set<MachineDTO> processedMachines = employeeMachines.stream()
                .filter(machineDTO -> !machineDTO.id().equals(machineToRemove))
                .collect(Collectors.toSet());

        // this complete object builder is required to satisfy binder in EmployeeForm
        return EmployeeDTO.builder()
                .id(employeeFormModel.getId())
                .firstName(employeeFormModel.getFirstName())
                .lastName(employeeFormModel.getLastName())
                .departments(mapToDepartmentDTOs(employeeFormModel.getDepartments()))
                .phoneNumber(employeeFormModel.getPhoneNumber())
                .roles(employeeFormModel.getRoles().stream()
                        .map(r -> new RoleDTO(r.name()))
                        .collect(Collectors.toSet()))
                .qualifications(employeeFormModel.getQualificationsSet())
                .machines(processedMachines)
                .build();
    }

    public abstract static class MachineManagerGridEvent extends ComponentEvent<MachineMangerGrid> {
        public MachineManagerGridEvent(MachineMangerGrid source) {
            super(source, false);
        }
    }

    public static class UpdateMachineEvent extends MachineManagerGridEvent {
        @Getter
        private final EmployeeDTO employeeDTO;

        public UpdateMachineEvent(MachineMangerGrid source, EmployeeDTO employeeDTO) {
            super(source);
            this.employeeDTO = employeeDTO;
        }
    }

    public Registration addUpdateMachineEvent(ComponentEventListener<UpdateMachineEvent> listener) {
        return addListener(UpdateMachineEvent.class, listener);
    }
}
