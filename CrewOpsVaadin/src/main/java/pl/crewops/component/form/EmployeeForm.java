package pl.crewops.component.form;

import static pl.crewops.model.DepartmentFormModel.orderedMapToDepartmentForms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import java.util.*;
import java.util.stream.Collectors;
import pl.crewops.component.accordion.MachineAccordion;
import pl.crewops.component.accordion.QualificationAccordion;
import pl.crewops.component.accordion.RoleAccordion;
import pl.crewops.component.notification.FailNotification;
import pl.crewops.dto.department.DepartmentDTO;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.DepartmentFormModel;
import pl.crewops.model.EmployeeFormModel;
import pl.crewops.util.SpringContextBridge;

public class EmployeeForm extends FormLayout {
    private final TextField firstName = new TextField();
    private final TextField lastName = new TextField();
    private final DatePicker birthDate = new DatePicker();
    private final TextField phoneNumber = new TextField();

    // TODO: multiselect css configuration match with all comopnents
    private final MultiSelectComboBox<DepartmentFormModel> departments;
    private final QualificationAccordion qualifications;
    private final MachineAccordion machines;
    private final RoleAccordion roleAccordion;

    private final Button save = new Button();
    private final Button update = new Button();
    private final Button delete = new Button();
    private final Button close = new Button();

    private final Binder<EmployeeFormModel> binder = new BeanValidationBinder<>(EmployeeFormModel.class);

    public EmployeeForm() {
        addClassName("employee-form");

        this.departments = getConfiguredDepartmentsMultiSelectedComboBox();
        this.qualifications = getConfiguredQualificationsAccordion();
        this.machines = getConfiguredMachinesAccordion();
        this.roleAccordion = getConfiguredRoleAccordion();

        localize();

        binder.bindInstanceFields(this);
        binder.forField(departments).bind(EmployeeFormModel::getDepartments, EmployeeFormModel::setDepartments);

        add(
                firstName,
                lastName,
                birthDate,
                phoneNumber,
                departments,
                createButtonsLayout(),
                qualifications,
                machines,
                roleAccordion);
    }

    private MultiSelectComboBox<DepartmentFormModel> getConfiguredDepartmentsMultiSelectedComboBox() {
        MultiSelectComboBox<DepartmentFormModel> departments = new MultiSelectComboBox<>();
        departments.setItemLabelGenerator(DepartmentFormModel::getName);
        return departments;
    }

    private QualificationAccordion getConfiguredQualificationsAccordion() {
        final var qualificationAccordion = new QualificationAccordion();

        qualificationAccordion.addUpdateQualificationsListener(event -> {
            var employeeFormModel = EmployeeFormModel.toEmployeeFormModel(event.getEmployeeDTO());
            setEmployee(employeeFormModel);
            validateAndUpdate();
        });

        return qualificationAccordion;
    }

    private MachineAccordion getConfiguredMachinesAccordion() {
        final var machineAccordion = new MachineAccordion();
        machineAccordion.addUpdateMachineListener(event -> {
            var employeeFormModel = EmployeeFormModel.toEmployeeFormModel(event.getEmployeeDTO());
            setEmployee(employeeFormModel);
            validateAndUpdate();
        });

        return machineAccordion;
    }

    private RoleAccordion getConfiguredRoleAccordion() {
        final var roleAccordion = new RoleAccordion();

        roleAccordion.addUpdateEvenListener(event -> {
            setEmployee(event.getEmployeeFormModel());
            validateAndUpdate();
        });

        return roleAccordion;
    }

    private void localize() {
        firstName.setLabel(getTranslation("employeeForm.firstName"));
        lastName.setLabel(getTranslation("employeeForm.lastName"));
        birthDate.setLabel(getTranslation("employeeForm.birthDate"));
        phoneNumber.setLabel(getTranslation("employeeForm.phoneNumber"));
        departments.setLabel(getTranslation("employeeForm.department"));

        save.setText(getTranslation("employeeForm.save"));
        update.setText(getTranslation("employeeForm.update"));
        delete.setText(getTranslation("employeeForm.delete"));
        close.setText(getTranslation("employeeForm.close"));
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        update.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        update.addClickListener(event -> validateAndUpdate());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, update, delete, close);
    }

    public void setFormModeSave() {
        save.setVisible(true);
        update.setVisible(false);
        delete.setVisible(false);
        qualifications.setVisible(false);
        machines.setVisible(false);
        roleAccordion.setVisible(false);

        firstName.setReadOnly(false);
        firstName.setEnabled(true);
        lastName.setReadOnly(false);
        lastName.setEnabled(true);
        birthDate.setReadOnly(false);
        birthDate.setEnabled(true);
    }

    public void setFormModeUpdate() {
        save.setVisible(false);
        update.setVisible(true);
        delete.setVisible(true);
        qualifications.setVisible(true);
        machines.setVisible(true);
        roleAccordion.setVisible(true);

        firstName.setReadOnly(true);
        firstName.setEnabled(false);
        lastName.setReadOnly(true);
        lastName.setEnabled(false);
        birthDate.setReadOnly(true);
        birthDate.setEnabled(false);
    }

    private void validateAndSave() {
        var employeeFormModel = EmployeeFormModel.builder()
                .firstName(firstName.getValue())
                .lastName(lastName.getValue())
                .birthDate(birthDate.getValue())
                .phoneNumber(phoneNumber.getValue())
                //                todo
                .departments(departments.getValue())
                .machinesSet(Set.of())
                .qualificationsSet(Set.of())
                .roles(Set.of())
                .build();

        if (binder.writeBeanIfValid(employeeFormModel)) {
            fireEvent(new SaveEvent(this, employeeFormModel));
        }
    }

    private void validateAndUpdate() {
        if (binder.validate().isOk()) {
            fireEvent(new UpdateEvent(this, binder.getBean()));
        }
    }

    // TODO: consider rename this method in a way like 'setBinderBean' because its starts being confusing even for me
    public void setEmployee(EmployeeFormModel employeeFormModel) {
        // this method is aimed to configure items of 'departments' component in a way to displaying preselected items
        // on the top of selector and above rest of possible options
        List<DepartmentFormModel> allItems = List.of();
        Set<DepartmentFormModel> selectedFormModels = Set.of();

        try {
            var coreAPI = SpringContextBridge.getBean(CoreAPI.class);
            List<DepartmentDTO> allDepartments = coreAPI.getAllDepartments();

            allItems = orderedMapToDepartmentForms(allDepartments);

            if (employeeFormModel != null && employeeFormModel.getDepartments() != null) {
                selectedFormModels = employeeFormModel.getDepartments();

                var byId = allItems.stream().collect(Collectors.toMap(DepartmentFormModel::getId, d -> d));

                selectedFormModels = selectedFormModels.stream()
                        .map(s -> byId.getOrDefault(s.getId(), s))
                        .collect(Collectors.toCollection(LinkedHashSet::new));
            }
        } catch (NotAuthenticatedException e) {
            new FailNotification(e.getMessage());
        }

        //
        Set<DepartmentFormModel> finalSelectedFormModels = selectedFormModels;
        List<DepartmentFormModel> sorted = allItems.stream()
                .sorted((d1, d2) -> {
                    boolean s1 = finalSelectedFormModels.contains(d1);
                    boolean s2 = finalSelectedFormModels.contains(d2);
                    if (s1 && !s2) return -1;
                    if (!s1 && s2) return 1;
                    return d1.getName().compareToIgnoreCase(d2.getName());
                })
                .toList();

        departments.setItems(sorted);
        binder.setBean(employeeFormModel);

        if (!selectedFormModels.isEmpty()) {
            departments.setValue(selectedFormModels);
        } else {
            departments.clear();
        }

        // other components that depend on employeeFormModel
        if (employeeFormModel != null) {
            qualifications.setValues(employeeFormModel);
            machines.setValues(employeeFormModel);
            roleAccordion.setValues(employeeFormModel);
        }
    }

    // Events
    public abstract static class EmployeeFormEvent extends ComponentEvent<EmployeeForm> {

        private final EmployeeFormModel employeeFormModel;

        protected EmployeeFormEvent(EmployeeForm source, EmployeeFormModel employeeFormModel) {
            super(source, false);
            this.employeeFormModel = employeeFormModel;
        }

        public EmployeeFormModel getEmployee() {
            return employeeFormModel;
        }
    }

    public static class SaveEvent extends EmployeeFormEvent {
        SaveEvent(EmployeeForm source, EmployeeFormModel employeeFormModel) {
            super(source, employeeFormModel);
        }
    }

    public static class UpdateEvent extends EmployeeFormEvent {
        UpdateEvent(EmployeeForm source, EmployeeFormModel employeeFormModel) {
            super(source, employeeFormModel);
        }
    }

    public static class DeleteEvent extends EmployeeFormEvent {
        DeleteEvent(EmployeeForm source, EmployeeFormModel employeeFormModel) {
            super(source, employeeFormModel);
        }
    }

    public static class CloseEvent extends EmployeeFormEvent {
        CloseEvent(EmployeeForm source) {
            super(source, null);
        }
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addUpdateListener(ComponentEventListener<UpdateEvent> listener) {
        return addListener(UpdateEvent.class, listener);
    }

    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        return addListener(DeleteEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }
}
