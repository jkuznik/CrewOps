package pl.crewops.component.dialog.machineDialog;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.shared.Registration;
import java.util.Comparator;
import lombok.Getter;
import pl.crewops.component.notification.FailNotification;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.exceptions.UpdateMachineException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.EmployeeFormModel;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.model.dto.machine.MachineDTO;
import pl.crewops.util.SpringContextBridge;
import pl.crewops.view.HomeView;

public class AddMachineForm extends FormLayout {

    private final ComboBox<MachineDTO> machines = new ComboBox<>();
    // same i18n like in AddQualificationForm, update if needed
    private final Button add = new Button(getTranslation("addQualificationForm.addButton"));

    public AddMachineForm(EmployeeFormModel employeeFormModel) {
        addClassName("addMachineForm");

        machines.addClassName("dark-combo");
        machines.getElement().setAttribute("theme", "dark-combo");

        var coreAPI = SpringContextBridge.getBean(CoreAPI.class);

        configureMachines(coreAPI);
        configureAddButtonClickListener(employeeFormModel, coreAPI);

        add(machines, add);
    }

    private void configureMachines(CoreAPI coreAPI) {
        machines.setItemLabelGenerator(MachineDTO::registerNumber);
        machines.setPlaceholder(getTranslation("addMachineForm.machinesPlaceholder"));

        machines.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                add.setEnabled(true);
            }
        });

        populateMachines(coreAPI);
    }

    private void configureAddButtonClickListener(EmployeeFormModel employeeFormModel, CoreAPI coreAPI) {
        add.setEnabled(false);
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add.setMaxWidth("200px");
        add.setWidth("100%");

        add.addClickListener(event -> {
            try {
                EmployeeDTO employeeDTO = coreAPI.addEmployeeMachine(
                                employeeFormModel.getId(), machines.getValue().id())
                        .orElseThrow(UpdateMachineException::new);
                fireEvent(new AddMachineEvent(this, employeeDTO));
            } catch (UpdateMachineException e) {
                new FailNotification(e.getMessage());
            } catch (NotAuthenticatedException e) {
                new FailNotification(e.getMessage());
                UI.getCurrent().navigate(HomeView.class);
            }
        });
    }

    private void populateMachines(CoreAPI coreAPI) {
        try {
            machines.setItems(coreAPI.getAllMachines().stream()
                    .sorted(Comparator.comparing(MachineDTO::registerNumber))
                    .toList());
        } catch (NotAuthenticatedException e) {
            new FailNotification(e.getMessage());
            UI.getCurrent().navigate(HomeView.class);
        }
    }

    public abstract static class AddMachineFormEvent extends ComponentEvent<AddMachineForm> {
        public AddMachineFormEvent(AddMachineForm source) {
            super(source, false);
        }
    }

    public static class AddMachineEvent extends AddMachineFormEvent {
        @Getter
        private final EmployeeDTO employeeDTO;

        public AddMachineEvent(AddMachineForm source, EmployeeDTO employeeDTO) {
            super(source);
            this.employeeDTO = employeeDTO;
        }
    }

    public Registration addUpdateMachineListener(ComponentEventListener<AddMachineEvent> listener) {
        return addListener(AddMachineEvent.class, listener);
    }
}
