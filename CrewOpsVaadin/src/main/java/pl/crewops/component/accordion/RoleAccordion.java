package pl.crewops.component.accordion;

import static pl.crewops.model.auth.RoleType.*;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import pl.crewops.component.notification.FailNotification;
import pl.crewops.dto.auth.AuthUserDTO;
import pl.crewops.dto.auth.RoleDTO;
import pl.crewops.dto.auth.UpdateAuthUserDTO;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.exceptions.UpdateQualificationException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.EmployeeFormModel;
import pl.crewops.model.auth.RoleType;
import pl.crewops.util.RoleResolver;
import pl.crewops.util.SpringContextBridge;

public class RoleAccordion extends FormLayout {
    private final CoreAPI coreAPI;
    private final RoleResolver roleResolver;

    public RoleAccordion() {
        addClassName("roles-accordion");

        this.coreAPI = SpringContextBridge.getBean(CoreAPI.class);
        this.roleResolver = SpringContextBridge.getBean(RoleResolver.class);
    }

    public void setValues(EmployeeFormModel employeeFormModel) {
        removeAll();

        var edit = new Button(getTranslation("qualificationAccordion.editButton"));

        CheckboxGroup<RoleType> roleTypeCheckboxGroup = configureRolesCheckbox(employeeFormModel, edit);
        configureEditButton(employeeFormModel, edit, roleTypeCheckboxGroup);

        var rolesDisplayLayout = new VerticalLayout(roleTypeCheckboxGroup, edit);
        rolesDisplayLayout.setSpacing(false);
        rolesDisplayLayout.setPadding(false);

        var accordion = new Accordion();
        accordion.setVisible(true);
        accordion.close();

        add(accordion);
        accordion.add(getTranslation("employeeForm.roles"), rolesDisplayLayout);
    }

    private void configureEditButton(
            EmployeeFormModel employeeFormModel, Button edit, CheckboxGroup<RoleType> checkboxGroup) {
        edit.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        edit.setEnabled(false);

        edit.addClickListener(event -> {
            Set<RoleType> selectedRoles = checkboxGroup.getValue();
            selectedRoles.add(EMPLOYEE);

            try {
                AuthUserDTO authUserDTO = coreAPI.updateAuthUserRoles(UpdateAuthUserDTO.builder()
                                .employeeId(employeeFormModel.getId())
                                .roles(selectedRoles.stream()
                                        .map(role -> RoleDTO.builder()
                                                .name(role.name())
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build())
                        .orElseThrow(UpdateQualificationException::new);
                var updatedEmployeeFormModel = EmployeeFormModel.builder()
                        .id(employeeFormModel.getId())
                        .firstName(employeeFormModel.getFirstName())
                        .lastName(employeeFormModel.getLastName())
                        .department(employeeFormModel.getDepartment())
                        .birthDate(employeeFormModel.getBirthDate())
                        .phoneNumber(employeeFormModel.getPhoneNumber())
                        .qualificationsSet(employeeFormModel.getQualificationsSet())
                        .machinesSet(employeeFormModel.getMachinesSet())
                        .roles(authUserDTO.roles().stream()
                                .map(role -> RoleType.valueOf(role.name()))
                                .collect(Collectors.toSet()))
                        .build();

                fireEvent(new UpdateEvent(this, updatedEmployeeFormModel));
            } catch (NotAuthenticatedException e) {
                new FailNotification(e.getMessage());
            }
        });
    }

    private CheckboxGroup<RoleType> configureRolesCheckbox(EmployeeFormModel employeeFormModel, Button edit) {
        RoleType[] allRoles = values();

        var checkboxGroup = new CheckboxGroup<RoleType>();

        checkboxGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);

        if (roleResolver.principalHasCompanyAdminRole()) {
            checkboxGroup.setItems(Arrays.stream(allRoles)
                    .filter(roleType -> !roleType.equals(EMPLOYEE) && !roleType.equals(SYSTEM_ADMIN))
                    .collect(Collectors.toSet()));
        } else {
            checkboxGroup.setItems(Arrays.stream(allRoles)
                    .filter(roleType -> !roleType.equals(EMPLOYEE)
                            && !roleType.equals(COMPANY_ADMIN)
                            && !roleType.equals(SYSTEM_ADMIN))
                    .collect(Collectors.toSet()));
        }

        checkboxGroup.setItemLabelGenerator(role -> {
            var translatedRoleName = "";

            switch (role) {
                case EMPLOYEE -> translatedRoleName = getTranslation("roleType.employee");
                case MECHANIC -> translatedRoleName = getTranslation("roleType.mechanic");
                case SHIFT_LEADER -> translatedRoleName = getTranslation("roleType.shiftLeader");
                case MANAGER -> translatedRoleName = getTranslation("roleType.manager");
                case COMPANY_ADMIN -> translatedRoleName = getTranslation("roleType.companyAdmin");
                case SYSTEM_ADMIN -> translatedRoleName = getTranslation("roleType.systemAdmin");
            }
            return translatedRoleName;
        });

        if (employeeFormModel.getRoles() != null) {
            checkboxGroup.setValue(employeeFormModel.getRoles());
        }

        checkboxGroup.addValueChangeListener(event -> {
            edit.setEnabled(!checkboxGroup.getValue().equals(employeeFormModel.getRoles()));
        });

        return checkboxGroup;
    }

    public abstract static class RoleAccordionEvent extends ComponentEvent<RoleAccordion> {
        public RoleAccordionEvent(RoleAccordion source) {
            super(source, false);
        }
    }

    public static class UpdateEvent extends RoleAccordionEvent {
        @Getter
        private final EmployeeFormModel employeeFormModel;

        public UpdateEvent(RoleAccordion source, EmployeeFormModel employeeFormModel) {
            super(source);
            this.employeeFormModel = employeeFormModel;
        }
    }

    public Registration addUpdateEvenListener(ComponentEventListener<UpdateEvent> listener) {
        return addListener(UpdateEvent.class, listener);
    }
}
