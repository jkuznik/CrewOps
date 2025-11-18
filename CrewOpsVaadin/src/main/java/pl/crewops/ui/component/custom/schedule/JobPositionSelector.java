package pl.crewops.ui.component.custom.schedule;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Span; // Dodany import dla Span
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import java.util.*;
import lombok.Getter;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;
import pl.crewops.model.dto.shift.ShiftConfig;
import pl.crewops.ui.component.custom.ComboBoxCustom;
import pl.crewops.ui.component.notification.FailNotification;
import pl.crewops.util.SpringContextBridge;

public class JobPositionSelector extends VerticalLayout {

    private final CoreAPI coreAPI;
    private final Span orderNumberSpan = new Span();

    private final Checkbox criticalCheckbox = new Checkbox("Kluczowe stanowisko");
    private final Button remove = new Button(VaadinIcon.TRASH.create());
    private final HorizontalLayout comboLayout = new HorizontalLayout();

    private ComboBoxCustom<JobPositionDTO> jobPositionCombo = new ComboBoxCustom<>();
    private final ComboBoxCustom<EmployeeDTO> employeeCombo = new ComboBoxCustom<>();

    private List<JobPositionDTO> allAvailableJobPositions;
    private JobPositionDTO currentSelectedJobPosition = null;

    public JobPositionSelector(List<JobPositionDTO> allAvailableJobPositions) {

        this.coreAPI = SpringContextBridge.getBean(CoreAPI.class);
        this.allAvailableJobPositions = allAvailableJobPositions;

        setSpacing(false);
        setPadding(false);

        orderNumberSpan.getStyle().set("font-weight", "bold");

        configureJobPositionComboBox();

        try {
            setEmployees(coreAPI.getAllEmployees());
        } catch (NotAuthenticatedException e) {
            new FailNotification(e.getMessage());
        }

        comboLayout.setSpacing(true);
        comboLayout.setWidthFull();
        comboLayout.add(jobPositionCombo, employeeCombo);
        employeeCombo.setWidth("50%");

        HorizontalLayout firstRow = new HorizontalLayout(orderNumberSpan, criticalCheckbox, remove);
        firstRow.setWidthFull();
        firstRow.setAlignItems(Alignment.CENTER);
        firstRow.setJustifyContentMode(JustifyContentMode.END);
        firstRow.setSpacing(true);

        remove.addClickListener(event -> {
            fireEvent(new RemoveEvent(this, currentSelectedJobPosition));
        });

        add(firstRow, comboLayout);
    }

    private void configureJobPositionComboBox() {

        jobPositionCombo.setWidth("50%");
        jobPositionCombo.setItems(allAvailableJobPositions);
        jobPositionCombo.setItemLabelGenerator(JobPositionDTO::name);
        jobPositionCombo.addValueChangeListener(event -> {
            if (event.getValue() != null && event.getValue() != currentSelectedJobPosition) {
                currentSelectedJobPosition = JobPositionDTO.builder()
                        .id(event.getValue().id())
                        .name(event.getValue().name())
                        .build();
                allAvailableJobPositions.remove(currentSelectedJobPosition);
                fireEvent(new SelectedJobPositionEvent(this, event.getOldValue(), currentSelectedJobPosition));
            }
        });

        localize();
    }

    public void configureExistingJobPositions(ShiftConfig shiftConfig) {
        jobPositionCombo.setValue(shiftConfig.jopPosition());
        currentSelectedJobPosition = shiftConfig.jopPosition();

        if (shiftConfig.relatedEmployee() != null) {
            employeeCombo.setValue(shiftConfig.relatedEmployee());
        }

        criticalCheckbox.setValue(shiftConfig.critical());
    }

    public void removeJobPositionFromItems(JobPositionDTO position) {
        allAvailableJobPositions.remove(position);
        jobPositionCombo.setValue(null);
        ArrayList<JobPositionDTO> copiedList = new ArrayList<>(allAvailableJobPositions);
        jobPositionCombo.setItems(copiedList);

        if (currentSelectedJobPosition != null) {
            if (!copiedList.contains(currentSelectedJobPosition)) {
                copiedList.add(currentSelectedJobPosition);
                copiedList.sort(Comparator.comparing(JobPositionDTO::name));
            }
            jobPositionCombo.setValue(copiedList.stream()
                    .filter(jobPositionDTO -> jobPositionDTO.id().equals(currentSelectedJobPosition.id()))
                    .findFirst()
                    .orElse(null));
        }
    }

    public void addJobPositionToItems(JobPositionDTO position) {
        if (!allAvailableJobPositions.contains(position)) {
            allAvailableJobPositions.add(position);
            allAvailableJobPositions.sort(Comparator.comparing(JobPositionDTO::name));

            jobPositionCombo.setValue(null);
            ArrayList<JobPositionDTO> copiedList = new ArrayList<>(allAvailableJobPositions);
            jobPositionCombo.setItems(copiedList);

            if (currentSelectedJobPosition != null) {
                if (!copiedList.contains(currentSelectedJobPosition)) {
                    copiedList.add(currentSelectedJobPosition);
                    copiedList.sort(Comparator.comparing(JobPositionDTO::name));
                }
                jobPositionCombo.setValue(copiedList.stream()
                        .filter(jobPositionDTO -> jobPositionDTO.id().equals(currentSelectedJobPosition.id()))
                        .findFirst()
                        .orElse(null));
            }
        }
    }

    private void localize() {
        jobPositionCombo.setPlaceholder("Stanowisko *");
        employeeCombo.setPlaceholder("");
    }

    public void setOrderNumber(int number) {
        orderNumberSpan.setText(number + ".");
    }

    private void setEmployees(List<EmployeeDTO> employees) {
        employeeCombo.setItems(employees);
        employeeCombo.setItemLabelGenerator(EmployeeDTO::firstName);
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

    @Getter
    public static class SelectedJobPositionEvent extends JobPositionSelectorEvent {
        private final JobPositionDTO oldValue;
        private final JobPositionDTO newValue;

        public SelectedJobPositionEvent(JobPositionSelector source, JobPositionDTO oldValue, JobPositionDTO newValue) {
            super(source);
            this.oldValue = oldValue;
            this.newValue = newValue;
        }
    }

    @Getter
    public static class RemoveEvent extends JobPositionSelectorEvent {
        private final JobPositionDTO value;

        public RemoveEvent(JobPositionSelector source, JobPositionDTO value) {
            super(source);
            this.value = value;
        }
    }

    public Registration addSelectionJobPositionListener(ComponentEventListener<SelectedJobPositionEvent> listener) {
        return addListener(SelectedJobPositionEvent.class, listener);
    }

    public Registration addRemoveListener(ComponentEventListener<RemoveEvent> listener) {
        return addListener(RemoveEvent.class, listener);
    }
}
