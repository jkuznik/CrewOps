package pl.crewops.component.form;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import java.util.UUID;
import lombok.Getter;
import pl.crewops.model.MessageFormModel;

public class MessageForm extends FormLayout {

    //    TODO: implement this later
    //    private final Checkbox sendToAll = new Checkbox();
    //    private final ComboBox<String> recipientDepartment = new ComboBox<>();
    //    private final ComboBox<String> recipientMachineOperators = new ComboBox<>();
    private final ComboBox<UUID> recipientEmployeeId = new ComboBox<>();
    private final TextField title = new TextField();
    private final TextField description = new TextField();

    private final Button send = new Button();
    private final Button close = new Button("Close");

    private final Binder<MessageFormModel> binder = new BeanValidationBinder<>(MessageFormModel.class);

    public MessageForm() {
        addClassName("message-form");

        binder.bindInstanceFields(this);

        localize();

        add(recipientEmployeeId, title, description, createButtonLayout());
    }

    private void localize() {}

    private Component createButtonLayout() {
        send.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        send.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        send.addClickListener(event -> fireEvent(new SendEvent(this, binder.getBean())));

        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(event -> send.setEnabled(binder.isValid()));
        return new HorizontalLayout(send, close);
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
}
