package pl.crewops.component.form;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import java.util.UUID;
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
    private final Button close = new Button();

    private final Binder<MessageFormModel> binder = new BeanValidationBinder<>(MessageFormModel.class);

    public MessageForm() {
        addClassName("message-form");

        binder.bindInstanceFields(this);

        add(recipientEmployeeId, title, description, createButtonLayout());
    }

    private Component createButtonLayout() {
        send.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        send.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        send.addClickListener(event -> {});

        close.addClickListener(event -> {});

        binder.addStatusChangeListener(event -> send.setEnabled(binder.isValid()));
        return new HorizontalLayout(send, close);
    }
}
