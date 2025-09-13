package pl.crewops.component.accordion;

import static pl.crewops.model.auth.RoleType.*;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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
import pl.crewops.util.AuthenticationResolver;
import pl.crewops.util.SpringContextBridge;

public class RoleAccordion extends FormLayout {
    private final CoreAPI coreAPI;
    private final AuthenticationResolver authenticationResolver;

    public RoleAccordion() {
        addClassName("roles-accordion");
        this.coreAPI = SpringContextBridge.getBean(CoreAPI.class);
        this.authenticationResolver = SpringContextBridge.getBean(AuthenticationResolver.class);
    }

    public void setValues(EmployeeFormModel employeeFormModel) {
        removeAll();

        Button edit = new Button(getTranslation("qualificationAccordion.editButton"));
        edit.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
        edit.setEnabled(false);

        CheckboxGroup<RoleType> roleTypeCheckboxGroup = configureRolesCheckbox(employeeFormModel, edit);

        // Collapsible content
        VerticalLayout rolesDisplay = new VerticalLayout(roleTypeCheckboxGroup);
        rolesDisplay.setSpacing(false);
        rolesDisplay.setPadding(false);
        rolesDisplay.setVisible(false);

        configureEditButton(employeeFormModel, edit, roleTypeCheckboxGroup);

        // Toggle button with icons
        Icon closedIcon = VaadinIcon.CHEVRON_RIGHT.create();
        Icon openIcon = VaadinIcon.CHEVRON_DOWN.create();

        Button toggle = new Button(closedIcon);
        toggle.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL);
        toggle.getElement().getStyle().set("min-width", "2.2rem");

        toggle.addClickListener(ev -> {
            boolean expand = !rolesDisplay.isVisible();
            rolesDisplay.setVisible(expand);
            toggle.setIcon(expand ? openIcon : closedIcon);
        });

        // Title
        Span title = new Span(getTranslation("employeeForm.roles"));
        title.getStyle().set("font-weight", "600");

        // Header row: toggle + title (left), edit (right)
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.add(toggle, title);
        header.addAndExpand(new Span()); // flexible spacer
        header.add(edit);

        // Add header + collapsible section
        add(header, rolesDisplay);
    }

    private void configureEditButton(
            EmployeeFormModel employeeFormModel, Button edit, CheckboxGroup<RoleType> checkboxGroup) {
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
                        .departments(employeeFormModel.getDepartments())
                        .birthDate(employeeFormModel.getBirthDate())
                        .phoneNumber(employeeFormModel.getPhoneNumber())
                        .qualificationsSet(employeeFormModel.getQualificationsSet())
                        .machinesSet(employeeFormModel.getMachinesSet())
                        .roles(authUserDTO.roles().stream()
                                .map(role -> RoleType.valueOf(role.name()))
                                .collect(Collectors.toSet()))
                        .build();

                fireEvent(new UpdateEvent(this, updatedEmployeeFormModel));
                edit.setEnabled(false); // disable again until change
            } catch (NotAuthenticatedException e) {
                new FailNotification(e.getMessage());
            }
        });
    }

    private CheckboxGroup<RoleType> configureRolesCheckbox(EmployeeFormModel employeeFormModel, Button edit) {
        RoleType[] allRoles = values();
        var checkboxGroup = new CheckboxGroup<RoleType>();
        checkboxGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);

        if (authenticationResolver.principalHasCompanyAdminPermission()) {
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

        checkboxGroup.setItemLabelGenerator(role -> switch (role) {
            case EMPLOYEE -> getTranslation("roleType.employee");
            case MECHANIC -> getTranslation("roleType.mechanic");
            case SHIFT_LEADER -> getTranslation("roleType.shiftLeader");
            case MANAGER -> getTranslation("roleType.manager");
            case COMPANY_ADMIN -> getTranslation("roleType.companyAdmin");
            case SYSTEM_ADMIN -> getTranslation("roleType.systemAdmin");
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
