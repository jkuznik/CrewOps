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
import pl.crewops.dto.qualification.UpdateQualificationExpiredAtDTO;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.EmployeeFormModel;
import pl.crewops.model.QualificationFormModel;
import pl.crewops.util.SpringContextBridge;

public class EditQualificationForm extends FormLayout {

    private final EmployeeFormModel employeeFormModel;
    private final QualificationFormModel qualificationFormModel;
    private final CoreAPI coreAPI;
    // TODO: i18n
    private final TextField qualification = new TextField("Qualification");
    private final DatePicker expireAt = new DatePicker("Expiration date");

    private final Button save = new Button("Save");
    private final Button delete = new Button("Del");
    private final Button unset = new Button("Unset"); // ustaw
    private final Button cancel = new Button("Esc");

    public EditQualificationForm(EmployeeFormModel employeeFormModel, QualificationFormModel qualificationFormModel) {
        addClassName("edit-qualification-form");

        this.employeeFormModel = employeeFormModel;
        this.qualificationFormModel = qualificationFormModel;
        this.coreAPI = SpringContextBridge.getBean(CoreAPI.class);

        configureFields(qualificationFormModel);
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

    private void configureFields(QualificationFormModel qualificationFormModel) {
        qualification.setEnabled(false);
        qualification.setValue(qualificationFormModel.getDescription());
        expireAt.setValue(qualificationFormModel.getExpiredAt());
    }

    private void configureButtons() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(event -> {
            try {
                var date = expireAt.getValue();
                ZoneId zone = ZoneId.systemDefault();
                Instant instantExpireAt = date.atStartOfDay(zone).toInstant();
                coreAPI.updateQualificationExpireAt(new UpdateQualificationExpiredAtDTO(
                        employeeFormModel.getId(), qualificationFormModel.getId(), instantExpireAt));
                expireAt.setValue(null);
            } catch (NotAuthenticatedException e) {
                // TODO: implement notification + event to update parent components
            }
        });
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

    public static class CancelEvent extends EditQualificationFormEvent {
        public CancelEvent(EditQualificationForm source) {
            super(source);
        }
    }

    public Registration addCancelListener(ComponentEventListener<CancelEvent> listener) {
        return addListener(CancelEvent.class, listener);
    }
}
