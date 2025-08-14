package pl.crewops.component.accordion;

import static pl.crewops.model.auth.RoleType.*;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import pl.crewops.dto.auth.RoleDTO;
import pl.crewops.dto.auth.UpdateAuthUserDTO;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.EmployeeFormModel;
import pl.crewops.model.auth.RoleType;
import pl.crewops.util.RoleResolver;
import pl.crewops.util.SpringContextBridge;

public class RoleAccordion extends FormLayout {
    private final CoreAPI coreAPI;
    private final RoleResolver roleResolver;
    private final CheckboxGroup<RoleType> checkboxGroup;
    private final Button edit = new Button();

    public RoleAccordion() {
        addClassName("roles-accordion");

        this.coreAPI = SpringContextBridge.getBean(CoreAPI.class);
        this.roleResolver = SpringContextBridge.getBean(RoleResolver.class);
        checkboxGroup = new CheckboxGroup<>();
        edit.setEnabled(false);

        localize();
    }

    private void localize() {
        edit.setText(getTranslation("qualificationAccordion.editButton"));
    }

    public void setValues(EmployeeFormModel employeeFormModel) {
        removeAll();

        var accordion = new Accordion();

        edit.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        edit.setEnabled(false);
        edit.addClickListener(event -> {
            Set<RoleType> selectedRoles = checkboxGroup.getValue();
            selectedRoles.add(EMPLOYEE);

            coreAPI.updateAuthUserRoles(UpdateAuthUserDTO.builder()
                    .employeeId(employeeFormModel.getId())
                    .roles(selectedRoles.stream()
                            .map(role -> RoleDTO.builder().name(role.name()).build())
                            .collect(Collectors.toSet()))
                    .build());
        });

        configureRolesCheckbox(employeeFormModel);

        var rolesDisplay = new VerticalLayout(checkboxGroup, edit);
        rolesDisplay.setSpacing(false);
        rolesDisplay.setPadding(false);

        accordion.setVisible(true);
        //        accordion.close();

        add(accordion);
        accordion.add(getTranslation("employeeForm.roles"), rolesDisplay);
    }

    private void configureRolesCheckbox(EmployeeFormModel employeeFormModel) {
        RoleType[] allRoles = values();

        checkboxGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);

        if (roleResolver.principalHasManagerRole()) {
            checkboxGroup.setItems(Arrays.stream(allRoles)
                    .filter(roleType -> !roleType.equals(EMPLOYEE)
                            && !roleType.equals(COMPANY_ADMIN)
                            && !roleType.equals(SYSTEM_ADMIN))
                    .collect(Collectors.toSet()));
        } else {
            checkboxGroup.setItems(Arrays.stream(allRoles)
                    .filter(roleType -> !roleType.equals(EMPLOYEE) && !roleType.equals(SYSTEM_ADMIN))
                    .collect(Collectors.toSet()));
        }
        // todo: i18n
        checkboxGroup.setItemLabelGenerator(
                role -> role.name().replace("_", " ").toLowerCase());

        if (employeeFormModel.getRoles() != null) {
            checkboxGroup.setValue(employeeFormModel.getRoles());
        }

        checkboxGroup.addValueChangeListener(event -> {
            edit.setEnabled(!checkboxGroup.getValue().equals(employeeFormModel.getRoles()));
        });
    }

    public void setEnabled(boolean enabled) {
        edit.setEnabled(enabled);
    }
}
