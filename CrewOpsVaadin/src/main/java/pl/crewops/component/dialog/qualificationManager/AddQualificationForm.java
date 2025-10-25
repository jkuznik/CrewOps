package pl.crewops.component.dialog.qualificationManager;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.shared.Registration;
import java.util.Comparator;
import lombok.Getter;
import pl.crewops.component.custom.ComboBoxCustom;
import pl.crewops.component.notification.FailNotification;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.exceptions.UpdateQualificationException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.EmployeeFormModel;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.model.dto.qualification.QualificationDTO;
import pl.crewops.util.SpringContextBridge;
import pl.crewops.view.HomeView;

public class AddQualificationForm extends FormLayout {

    private final ComboBoxCustom<QualificationDTO> qualifications = new ComboBoxCustom<>();
    private final Button add = new Button(getTranslation("addQualificationForm.addButton"));

    public AddQualificationForm(EmployeeFormModel employeeFormModel) {
        addClassName("add-qualification-form");

        var coreAPI = SpringContextBridge.getBean(CoreAPI.class);

        configureQualifications(coreAPI);
        configureAddButtonClickListener(employeeFormModel, coreAPI);

        add(qualifications, add);
    }

    private void configureQualifications(CoreAPI coreAPI) {
        qualifications.setItemLabelGenerator(QualificationDTO::description);
        qualifications.setPlaceholder(getTranslation("addQualificationForm.qualificationPlaceholder"));

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
        add.setMaxWidth("200px");
        add.setWidth("100%");

        add.addClickListener(event -> {
            try {
                EmployeeDTO employeeDTO = coreAPI.addEmployeeQualification(
                                employeeFormModel.getId(),
                                qualifications.getValue().id())
                        .orElseThrow(UpdateQualificationException::new);
                fireEvent(new AddQualificationsEvent(this, employeeDTO));
            } catch (UpdateQualificationException e) {
                new FailNotification(e.getMessage());
            } catch (NotAuthenticatedException e) {
                new FailNotification(e.getMessage());
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

    public abstract static class AddQualificationFormEvent extends ComponentEvent<AddQualificationForm> {
        public AddQualificationFormEvent(AddQualificationForm source) {
            super(source, false);
        }
    }

    public static class AddQualificationsEvent extends AddQualificationFormEvent {
        @Getter
        private final EmployeeDTO employeeDTO;

        AddQualificationsEvent(AddQualificationForm source, EmployeeDTO employeeDTO) {
            super(source);
            this.employeeDTO = employeeDTO;
        }
    }

    public Registration addUpdateQualificationsListener(ComponentEventListener<AddQualificationsEvent> listener) {
        return addListener(AddQualificationsEvent.class, listener);
    }
}
