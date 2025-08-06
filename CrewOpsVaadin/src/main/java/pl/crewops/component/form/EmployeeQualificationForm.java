package pl.crewops.component.form;

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
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.qualification.QualificationDTO;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.EmployeeFormModel;
import pl.crewops.util.SpringContextBridge;
import pl.crewops.view.HomeView;

public class EmployeeQualificationForm extends FormLayout {

    // TODO: i18n , comboBox style
    private final ComboBox<QualificationDTO> qualifications = new ComboBox<>();
    private final Button add = new Button("Dodaj");

    public EmployeeQualificationForm(EmployeeFormModel employeeFormModel) {
        addClassName("employee-qualification-form");

        var coreAPI = SpringContextBridge.getBean(CoreAPI.class);

        configureQualifications(coreAPI);
        configureAddButtonClickListener(employeeFormModel, coreAPI);

        add(qualifications, add);
    }

    private void configureQualifications(CoreAPI coreAPI) {
        qualifications.setItemLabelGenerator(QualificationDTO::description);

        qualifications.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                add.setEnabled(true);
            }
        });

        populateQualifications(coreAPI);
    }

    private void configureAddButtonClickListener(EmployeeFormModel employeeFormModel, CoreAPI coreAPI) {
        add.setEnabled(false);
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add.addClickListener(event -> {
            try {
                EmployeeDTO employeeDTO = coreAPI.addEmployeeQualification(
                                employeeFormModel.getId(),
                                qualifications.getValue().id())
                        // TODO: consider about custom exception
                        .orElseThrow(() -> new RuntimeException("Error during adding qualification to employee"));
                fireEvent(new UpdateQualificationsEvent(this, employeeDTO));
            } catch (NotAuthenticatedException e) {
                UI.getCurrent().navigate(HomeView.class);
            }
        });
    }

    private void populateQualifications(CoreAPI coreAPI) {
        try {
            qualifications.setItems(coreAPI.getAllQualifications().stream()
                    .sorted(Comparator.comparing(QualificationDTO::description))
                    .toList());
        } catch (NotAuthenticatedException e) {
            UI.getCurrent().navigate(HomeView.class);
        }
    }

    public abstract static class EmployeeQualificationFormEvent extends ComponentEvent<EmployeeQualificationForm> {
        public EmployeeQualificationFormEvent(EmployeeQualificationForm source) {
            super(source, false);
        }
    }

    public static class UpdateQualificationsEvent extends EmployeeQualificationFormEvent {
        @Getter
        private final EmployeeDTO employeeDTO;

        UpdateQualificationsEvent(EmployeeQualificationForm source, EmployeeDTO employeeDTO) {
            super(source);
            this.employeeDTO = employeeDTO;
        }
    }

    public Registration addUpdateQualificationsListener(ComponentEventListener<UpdateQualificationsEvent> listener) {
        return addListener(UpdateQualificationsEvent.class, listener);
    }
}
