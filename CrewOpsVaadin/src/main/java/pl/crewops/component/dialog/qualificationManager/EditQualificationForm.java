package pl.crewops.component.dialog.qualificationManager;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import java.time.Instant;
import java.time.ZoneId;
import lombok.Getter;
import lombok.Setter;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.qualification.UpdateQualificationExpiredAtDTO;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.EmployeeFormModel;
import pl.crewops.model.QualificationFormModel;
import pl.crewops.util.SpringContextBridge;

public class EditQualificationForm extends FormLayout {

    private final CoreAPI coreAPI;
    // TODO: i18n
    private final TextField qualification = new TextField("Qualification");
    private final DatePicker expireAt = new DatePicker("Expiration date");

    private final Button save = new Button("Save");
    private final Button delete = new Button("Del");
    private final Button unset = new Button("Unset"); // ustaw
    private final Button cancel = new Button("Esc");

    @Setter
    private EmployeeFormModel employeeFormModel;

    private QualificationFormModel qualificationFormModel;

    public EditQualificationForm() {
        addClassName("edit-qualification-form");

        this.coreAPI = SpringContextBridge.getBean(CoreAPI.class);

        qualification.setEnabled(false);
        configureButtons();

        var fields = new HorizontalLayout();
        fields.setSizeFull();
        fields.setSpacing(true);
        fields.add(qualification, expireAt);

        var buttons = new HorizontalLayout();
        buttons.setSizeFull();
        buttons.setSpacing(true);
        buttons.add(save, unset, delete, cancel);

        var verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.add(fields, buttons);

        add(verticalLayout);
    }

    public void setQualificationFormModel(QualificationFormModel qualificationFormModel) {
        this.qualificationFormModel = qualificationFormModel;
        if (qualificationFormModel != null) {
            qualification.setValue(qualificationFormModel.getDescription());
            expireAt.setValue(qualificationFormModel.getExpiredAt());
        }
    }

    private void configureButtons() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(event -> {
            try {
                var date = expireAt.getValue();
                ZoneId zone = ZoneId.systemDefault();
                Instant instantExpireAt = date.atStartOfDay(zone).toInstant();
                var employeeDTO = coreAPI.updateQualificationExpireAt(new UpdateQualificationExpiredAtDTO(
                                employeeFormModel.getId(), qualificationFormModel.getId(), instantExpireAt))
                        // TODO: custom exception
                        .orElseThrow(RuntimeException::new);
                expireAt.setValue(null);
                fireEvent(new UpdateEvent(this, employeeDTO));
            } catch (NotAuthenticatedException e) {
                // TODO: implement notification + event to update parent components
            }
        });
        // TODO: implement delete action
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        unset.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        unset.addClickListener(event -> {
            try {
                coreAPI.updateQualificationExpireAt(new UpdateQualificationExpiredAtDTO(
                        employeeFormModel.getId(), qualificationFormModel.getId(), null));
                expireAt.setValue(null);
            } catch (NotAuthenticatedException e) {
                // TODO: implement notification + event to update parent components
            }
        });
        cancel.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        cancel.addClickListener(event -> fireEvent(new CancelEvent(this)));
    }

    public abstract static class EditQualificationFormEvent extends ComponentEvent<EditQualificationForm> {

        public EditQualificationFormEvent(EditQualificationForm source) {
            super(source, false);
        }
    }

    public static class UpdateEvent extends EditQualificationFormEvent {
        @Getter
        private final EmployeeDTO employeeDTO;

        public UpdateEvent(EditQualificationForm source, EmployeeDTO employeeDTO) {
            super(source);
            this.employeeDTO = employeeDTO;
        }
    }

    public static class CancelEvent extends EditQualificationFormEvent {
        public CancelEvent(EditQualificationForm source) {
            super(source);
        }
    }

    public Registration addUpdateEventListener(ComponentEventListener<UpdateEvent> listener) {
        return addListener(UpdateEvent.class, listener);
    }

    public Registration addCancelEventListener(ComponentEventListener<CancelEvent> listener) {
        return addListener(CancelEvent.class, listener);
    }
}
