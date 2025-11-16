package pl.crewops.ui.component.custom;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Span; // Dodany import dla Span
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import java.util.List;
import java.util.Optional;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;
import pl.crewops.model.dto.shift.ShiftConfig;
import pl.crewops.ui.component.notification.FailNotification;
import pl.crewops.util.SpringContextBridge;

public class JobPositionSelector extends VerticalLayout {

    private final CoreAPI coreAPI;

    private final Span orderNumberSpan = new Span();
    private final Checkbox criticalCheckbox = new Checkbox("Kluczowe stanowisko");
    private final Button remove = new Button(VaadinIcon.TRASH.create());

    private final ComboBoxCustom<JobPositionDTO> jobPositionCombo = new ComboBoxCustom<>();
    private final ComboBoxCustom<EmployeeDTO> employeeCombo = new ComboBoxCustom<>();

    public JobPositionSelector() {
        this.coreAPI = SpringContextBridge.getBean(CoreAPI.class);
        setSpacing(false);
        setPadding(false);

        orderNumberSpan.getStyle().set("font-weight", "bold");

        localize();

        try {
            setJobPositions(coreAPI.getAllJobPositions());
            setEmployees(coreAPI.getAllEmployees());
        } catch (NotAuthenticatedException e) {
            new FailNotification(e.getMessage());
        }

        HorizontalLayout comboLayout = new HorizontalLayout(jobPositionCombo, employeeCombo);
        comboLayout.setSpacing(true);
        comboLayout.setWidthFull();
        jobPositionCombo.setWidth("50%");
        employeeCombo.setWidth("50%");

        HorizontalLayout firstRow = new HorizontalLayout(orderNumberSpan, criticalCheckbox, remove);
        firstRow.setWidthFull();
        firstRow.setAlignItems(Alignment.CENTER);
        firstRow.setJustifyContentMode(JustifyContentMode.END);
        firstRow.setSpacing(true);

        remove.addClickListener(event -> {
            fireEvent(new RemoveEvent(this));
        });

        add(firstRow, comboLayout);
    }

    public void configureExistingJobPositions(ShiftConfig shiftConfig) {
        jobPositionCombo.setValue(shiftConfig.jopPosition());
        if (shiftConfig.relatedEmployee() != null) {
            employeeCombo.setValue(shiftConfig.relatedEmployee());
        }
        criticalCheckbox.setValue(shiftConfig.critical());
    }

    private void localize() {
        jobPositionCombo.setPlaceholder("Stanowisko *");
        employeeCombo.setPlaceholder("");
    }

    // NOWA METODA: Ustawianie numeru porządkowego
    public void setOrderNumber(int number) {
        orderNumberSpan.setText(number + ".");
    }

    public void setJobPositions(List<JobPositionDTO> positions) {
        jobPositionCombo.setItems(positions);
        jobPositionCombo.setItemLabelGenerator(JobPositionDTO::name);
    }

    public void setEmployees(List<EmployeeDTO> employees) {
        employeeCombo.setItems(employees);
        employeeCombo.setItemLabelGenerator(EmployeeDTO::firstName);
    }

    public boolean isJobPositionSelected() {
        return jobPositionCombo.getValue() != null;
    }

    public JobPositionDTO getSelectedJobPosition() {
        return jobPositionCombo.getValue();
    }

    public Optional<EmployeeDTO> getSelectedEmployee() {
        return Optional.ofNullable(employeeCombo.getValue());
    }

    public boolean isCritical() {
        return criticalCheckbox.getValue();
    }

    public boolean validate() {
        if (jobPositionCombo.getValue() == null) {
            jobPositionCombo.focus();
            jobPositionCombo.setErrorMessage("Job position is required");
            jobPositionCombo.setInvalid(true);
            return false;
        } else {
            jobPositionCombo.setInvalid(false);
            return true;
        }
    }

    public abstract static class JobPositionSelectorEvent extends ComponentEvent<JobPositionSelector> {
        public JobPositionSelectorEvent(JobPositionSelector source) {
            super(source, false);
        }
    }

    public static class RemoveEvent extends JobPositionSelectorEvent {
        public RemoveEvent(JobPositionSelector source) {
            super(source);
        }
    }

    public Registration addRemoveListener(ComponentEventListener<RemoveEvent> listener) {
        return addListener(RemoveEvent.class, listener);
    }
}
