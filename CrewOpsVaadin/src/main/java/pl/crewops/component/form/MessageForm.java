package pl.crewops.component.form;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import java.util.ArrayList;
import lombok.Getter;
import pl.crewops.component.notification.FailNotification;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.DepartmentFormModel;
import pl.crewops.model.MessageFormModel;
import pl.crewops.model.dto.department.DepartmentDTO;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.model.dto.machine.MachineDTO;
import pl.crewops.model.dto.message.RecipientSelection;
import pl.crewops.util.AuthenticationResolver;
import pl.crewops.util.SpringContextBridge;

public class MessageForm extends FormLayout {

    private final Span currentModeDescription = new Span();
    private final RecipientSelectionField recipientSelectionField = new RecipientSelectionField();
    private final TextField sender = new TextField();
    private final TextField subject = new TextField();
    private final TextArea description = new TextArea();
    private final Button sendButton = new Button();
    private final Button closeButton = new Button();

    private final Binder<MessageFormModel> binder = new BeanValidationBinder<>(MessageFormModel.class);

    public MessageForm() {
        addClassName("message-form");

        localize();

        configureBinder(recipientSelectionField);

        description.setHeight("10em");

        add(currentModeDescription, recipientSelectionField, sender, subject, description, createButtonLayout());
    }

    public void setSendMessageMode() {
        currentModeDescription.setText((getTranslation("messageForm.sendMode")));
        sender.setVisible(false);
        recipientSelectionField.displayOptionsByPermissions();
        recipientSelectionField.setVisible(true);

        subject.setEnabled(true);
        description.setEnabled(true);

        sendButton.setVisible(true);
    }

    public void setReadMessageMode() {
        currentModeDescription.setText((getTranslation("messageForm.readMode")));
        sender.setVisible(true);
        recipientSelectionField.setVisible(false);

        subject.setEnabled(false);
        description.setEnabled(false);

        sendButton.setVisible(false);
    }

    public void setBinderValue(MessageFormModel model, String senderName) {
        if (model != null) {
            binder.setBean(model);
            sender.setValue(senderName);
            if (model.getSubject() != null) {
                subject.setValue(model.getSubject());
            }
            if (model.getDescription() != null) {
                description.setValue(model.getDescription());
            }
        }
    }

    public void clearBinderValue() {
        var model = MessageFormModel.builder()
                .subject(null)
                .description(null)
                .senderEmployeeId(null)
                .build();
        binder.setBean(model);
    }

    private void configureBinder(RecipientSelectionField recipientSelectionField) {
        binder.bindInstanceFields(this);
        binder.setBean(new MessageFormModel());

        binder.forField(recipientSelectionField)
                .asRequired(getTranslation("messageForm.recipientRequired"))
                .bind(MessageFormModel::getRecipientSelection, MessageFormModel::setRecipientSelection);

        binder.forField(description)
                .asRequired(getTranslation("messageForm.descriptionRequired"))
                .withValidator(
                        description ->
                                description != null && !description.trim().isEmpty(),
                        getTranslation("messageForm.descriptionInvalid"))
                .bind(MessageFormModel::getDescription, MessageFormModel::setDescription);
    }

    private void localize() {
        subject.setLabel(getTranslation("messageForm.title"));
        description.setLabel(getTranslation("messageForm.description"));

        sendButton.setText(getTranslation("messageForm.sendButton"));
        closeButton.setText(getTranslation("messageForm.closeButton"));
    }

    private Component createButtonLayout() {
        sendButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        sendButton.addClickShortcut(Key.ENTER);
        closeButton.addClickShortcut(Key.ESCAPE);

        sendButton.addClickListener(event -> {
            if (binder.validate().isOk()) {
                fireEvent(new SendEvent(this, binder.getBean()));
            }
        });
        closeButton.addClickListener(event -> fireEvent(new CloseEvent(this)));

        return new HorizontalLayout(sendButton, closeButton);
    }

    public abstract static class MessageFormEvent extends ComponentEvent<MessageForm> {
        public MessageFormEvent(MessageForm source) {
            super(source, false);
        }
    }

    public static class SendEvent extends MessageFormEvent {
        @Getter
        private final MessageFormModel messageFormModel;

        public SendEvent(MessageForm source, MessageFormModel messageFormModel) {
            super(source);
            this.messageFormModel = messageFormModel;
        }
    }

    public static class CloseEvent extends MessageFormEvent {
        public CloseEvent(MessageForm source) {
            super(source);
        }
    }

    public Registration addSendListener(ComponentEventListener<SendEvent> listener) {
        return addListener(SendEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }

    /**
     * Custom field that combines three ComboBoxes to produce a RecipientSelection value.
     */
    @CssImport("./styles/component/combo-box.css")
    private class RecipientSelectionField extends CustomField<RecipientSelection> {
        static final String ALL = "ALL";

        private final ComboBox<DepartmentFormModel> recipientDepartment = new ComboBox<>();
        private final ComboBox<MachineDTO> recipientMachineOperators = new ComboBox<>();
        private final ComboBox<EmployeeDTO> recipientEmployeeId = new ComboBox<>();

        RecipientSelectionField() {
            recipientDepartment.addClassName("recipient-departments-combobox");
            recipientDepartment.getElement().setAttribute("theme", "recipient-department");

            recipientMachineOperators.addClassName("recipient-machine-combobox");
            recipientMachineOperators.getElement().setAttribute("theme", "recipient-machine");

            recipientEmployeeId.addClassName("recipient-employee-combobox");
            recipientEmployeeId.getElement().setAttribute("theme", "recipient-employee");

            recipientDepartment.setPlaceholder(getTranslation("messageForm.recipientDepartment"));
            recipientMachineOperators.setPlaceholder(getTranslation("messageForm.recipientMachineOperators"));
            recipientEmployeeId.setPlaceholder(getTranslation("messageForm.recipientEmployeeId"));

            configureSendByOptions();

            var layout = new VerticalLayout(recipientDepartment, recipientMachineOperators, recipientEmployeeId);
            layout.setPadding(false);
            layout.setSpacing(true);
            layout.setWidthFull();
            layout.getStyle().set("align-items", "start");

            add(layout);
        }

        public void displayOptionsByPermissions() {
            var authenticationResolver = SpringContextBridge.getBean(AuthenticationResolver.class);
            recipientDepartment.setVisible(authenticationResolver.principalHasManagerPermission());
            recipientMachineOperators.setVisible(authenticationResolver.principalHasManagerPermission());
        }

        private void configureSendByOptions() {
            var coreAPI = SpringContextBridge.getBean(CoreAPI.class);
            try {
                var allOptionLabel = getTranslation("messageForm.allOptionLabel");

                var allDepartments = new ArrayList<DepartmentDTO>();
                allDepartments.add(DepartmentDTO.builder().name(allOptionLabel).build());
                allDepartments.addAll(coreAPI.getAllDepartments());
                recipientDepartment.setItems(DepartmentFormModel.mapToDepartmentFormsOrderedResult(allDepartments));
                recipientDepartment.setItemLabelGenerator(DepartmentFormModel::getName);
                recipientDepartment.addValueChangeListener(event -> {
                    if (event.getValue() != null) {
                        recipientMachineOperators.clear();
                        recipientEmployeeId.clear();
                    }
                    updateValue();
                });

                // todo: prepare method that allow to fetch all machines without pagination
                recipientMachineOperators.setItems(coreAPI.getAllMachines());
                recipientMachineOperators.setItemLabelGenerator(MachineDTO::registerNumber);

                // all those listeners are required to ensure which RecipientSelection type will be selected in
                // generateModelValue() method

                recipientMachineOperators.addValueChangeListener(event -> {
                    if (event.getValue() != null) {
                        recipientDepartment.clear();
                        recipientEmployeeId.clear();
                    }
                    updateValue();
                });

                recipientEmployeeId.setItems(coreAPI.getAllEmployees());
                recipientEmployeeId.setItemLabelGenerator(employee -> employee.firstName() + " " + employee.lastName());
                recipientEmployeeId.addValueChangeListener(event -> {
                    if (event.getValue() != null) {
                        recipientDepartment.clear();
                        recipientMachineOperators.clear();
                    }
                    updateValue();
                });
            } catch (NotAuthenticatedException e) {
                new FailNotification(e.getMessage());
            }
        }

        @Override
        public void setErrorMessage(String errorMessage) {
            super.setErrorMessage(errorMessage);
        }

        @Override
        public void setInvalid(boolean invalid) {
            super.setInvalid(invalid);
        }

        @Override
        protected RecipientSelection generateModelValue() {
            if (recipientDepartment.getValue() != null) {
                var allOptionLabel = getTranslation("messageForm.allOptionLabel");
                if (allOptionLabel.equalsIgnoreCase(
                        recipientDepartment.getValue().getName())) {
                    return new RecipientSelection(
                            RecipientSelection.RecipientOptionType.ALL,
                            recipientDepartment.getValue().getName());
                } else {
                    return new RecipientSelection(
                            RecipientSelection.RecipientOptionType.DEPARTMENT,
                            recipientDepartment.getValue().getId().toString());
                }
            }
            if (recipientMachineOperators.getValue() != null) {
                return new RecipientSelection(
                        RecipientSelection.RecipientOptionType.MACHINE,
                        recipientMachineOperators.getValue().id().toString());
            }
            if (recipientEmployeeId.getValue() != null) {
                return new RecipientSelection(
                        RecipientSelection.RecipientOptionType.EMPLOYEE,
                        recipientEmployeeId.getValue().id().toString());
            }
            return null;
        }

        @Override
        protected void setPresentationValue(RecipientSelection recipientSelection) {
            recipientDepartment.clear();
            recipientMachineOperators.clear();
            recipientEmployeeId.clear();

            if (recipientSelection != null) {
                switch (recipientSelection.type()) {
                    case ALL -> {
                        recipientDepartment.setValue(
                                DepartmentFormModel.builder().name(ALL).build());
                    }
                    case DEPARTMENT -> {
                        recipientDepartment.setValue(DepartmentFormModel.builder()
                                .name(recipientSelection.value())
                                .build());
                    }
                }
            }
        }

        // TODO: i should refactor this class and RecipientSelectionField extends CustomField< MyNewWrapperClass> where
        //  new wrapper storage RecipientSelection, MachineDTO, EmployeeDTO
        /**
         * Use those method instead of Vaadin .setPresentationValue from CustomField class if RecipientSelection type
         * is MACHINE or EMPLOYEE
         * */
        public void setPresentationValue(MachineDTO machineDTO) {
            recipientDepartment.clear();
            recipientMachineOperators.clear();
            recipientEmployeeId.clear();
            if (machineDTO != null) {
                recipientMachineOperators.setValue(machineDTO);
            }
        }

        public void setPresentationValue(EmployeeDTO employeeDTO) {
            recipientDepartment.clear();
            recipientMachineOperators.clear();
            recipientEmployeeId.clear();
            if (employeeDTO != null) {
                recipientEmployeeId.setValue(employeeDTO);
            }
        }
    }
}
