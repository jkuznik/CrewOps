package pl.crewops.component.dialog.qualificationManager;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import pl.crewops.component.notification.FailNotification;
import pl.crewops.dto.auth.RoleDTO;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.qualification.QualificationDTO;
import pl.crewops.dto.qualification.UpdateQualificationExpiredAtDTO;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.exceptions.UpdateQualificationException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.EmployeeFormModel;
import pl.crewops.model.QualificationFormModel;
import pl.crewops.util.SpringContextBridge;

public class EditQualificationForm extends FormLayout {

    private final CoreAPI coreAPI;
    private final TextField qualification = new TextField();
    private final DatePicker expireAt = new DatePicker();

    private final Button save = new Button();
    private final Button delete = new Button();
    private final Button unset = new Button();
    private final Button cancel = new Button();

    @Setter
    private EmployeeFormModel employeeFormModel;

    private QualificationFormModel qualificationFormModel;

    public EditQualificationForm() {
        addClassName("edit-qualification-form");

        this.coreAPI = SpringContextBridge.getBean(CoreAPI.class);

        localize();

        qualification.setEnabled(false);
        expireAt.setMin(LocalDate.now());
        configureButtons();

        var fields = new HorizontalLayout();
        fields.setSizeFull();
        fields.setSpacing(true);
        fields.add(qualification, expireAt);

        var buttons = new HorizontalLayout();
        buttons.setSizeFull();
        buttons.setSpacing(true);
        buttons.add(save, unset, cancel, delete);

        var verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.add(fields, buttons);

        add(verticalLayout);
    }

    private void localize() {
        qualification.setLabel(getTranslation("editQualificationForm.qualification"));
        expireAt.setLabel(getTranslation("editQualificationForm.expireAt"));
        save.setText(getTranslation("editQualificationForm.save"));
        delete.setText(getTranslation("editQualificationForm.delete"));
        unset.setText(getTranslation("editQualificationForm.unset"));
        cancel.setText(getTranslation("editQualificationForm.cancel"));
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
        save.addClickShortcut(Key.ENTER);
        save.addClickListener(event -> {
            try {
                var date = expireAt.getValue();
                if (date.isBefore(LocalDate.now())) {
                    throw new RuntimeException(getTranslation("editQualificationForm.errorDateBefore"));
                } else {
                    ZoneId zone = ZoneId.systemDefault();
                    Instant instantExpireAt = date.atStartOfDay(zone).toInstant();
                    var employeeDTO = coreAPI.updateQualificationExpireAt(new UpdateQualificationExpiredAtDTO(
                                    employeeFormModel.getId(), qualificationFormModel.getId(), instantExpireAt))
                            .orElseThrow(UpdateQualificationException::new);
                    expireAt.setValue(null);
                    fireEvent(new UpdateEvent(this, employeeDTO));
                }
            } catch (UpdateQualificationException e) {
                new FailNotification(e.getMessage());
            } catch (NotAuthenticatedException e) {
                new FailNotification(e.getMessage());
                UI.getCurrent().getPage().setLocation("/");
            }
        });
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        delete.addClickListener(event -> {
            try {
                coreAPI.removeEmployeeQualification(employeeFormModel.getId(), qualificationFormModel.getId());

                fireEvent(new UpdateEvent(this, processedEmployeeDTO(employeeFormModel, qualificationFormModel)));
            } catch (NotAuthenticatedException e) {
                new FailNotification(e.getMessage());
                UI.getCurrent().getPage().setLocation("/");
            }
        });
        unset.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        unset.addClickListener(event -> {
            try {
                var employeeDTO = coreAPI.updateQualificationExpireAt(new UpdateQualificationExpiredAtDTO(
                                employeeFormModel.getId(), qualificationFormModel.getId(), null))
                        .orElseThrow(UpdateQualificationException::new);
                expireAt.setValue(null);
                fireEvent(new UpdateEvent(this, employeeDTO));
            } catch (UpdateQualificationException e) {
                new FailNotification(e.getMessage());
            } catch (NotAuthenticatedException e) {
                new FailNotification(e.getMessage());
                UI.getCurrent().getPage().setLocation("/");
            }
        });
        cancel.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        cancel.addClickShortcut(Key.ESCAPE);
        cancel.addClickListener(event -> fireEvent(new CancelEvent(this)));
    }

    private EmployeeDTO processedEmployeeDTO(
            EmployeeFormModel employeeFormModel, QualificationFormModel qualificationFormModel) {

        var employeeQualifications = employeeFormModel.getQualificationsSet();
        var qualificationToRemove = qualificationFormModel.getId();
        Set<QualificationDTO> processedQualifications = employeeQualifications.stream()
                .filter(qualificationDTO -> !qualificationDTO.id().equals(qualificationToRemove))
                .collect(Collectors.toSet());

        // this complete object builder is required to satisfy binder in EmployeeForm
        return EmployeeDTO.builder()
                .id(employeeFormModel.getId())
                .firstName(employeeFormModel.getFirstName())
                .lastName(employeeFormModel.getLastName())
                .birthDate(employeeFormModel.getBirthDate())
                .departments(employeeFormModel.getDepartments())
                .phoneNumber(employeeFormModel.getPhoneNumber())
                .roles(employeeFormModel.getRoles().stream()
                        .map(r -> new RoleDTO(r.name()))
                        .collect(Collectors.toSet()))
                .qualifications(processedQualifications)
                .machines(employeeFormModel.getMachinesSet())
                .build();
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
