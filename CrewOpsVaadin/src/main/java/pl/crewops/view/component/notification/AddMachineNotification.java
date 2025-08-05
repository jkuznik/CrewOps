package pl.crewops.view.component.notification;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import pl.crewops.dto.machine.MachineDTO;

public class AddMachineNotification extends Notification {

    public AddMachineNotification(MachineDTO machineDTO) {
        addClassName("add-machine-notification");
        addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        setPosition(Notification.Position.TOP_END);
        setDuration(5000);

        var div = new Div();
        div.addClassName("add-machine-notification-div");
        div.setText(getTranslation("addMachineNotification.messagePrefix") + machineDTO.registerNumber());

        add(div);
        open();
    }
}
