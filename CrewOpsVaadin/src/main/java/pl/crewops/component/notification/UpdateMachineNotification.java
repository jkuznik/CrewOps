package pl.crewops.component.notification;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import pl.crewops.dto.machine.MachineDTO;

public class UpdateMachineNotification extends Notification {

    public UpdateMachineNotification(MachineDTO machineDTO) {
        addClassName("update-machine-notification");
        addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        setPosition(Notification.Position.TOP_END);
        setDuration(5000);

        var div = new Div();
        div.addClassName("update-machine-notification-div");
        div.setText(getTranslation("updateMachineNotification.messagePrefix") + machineDTO.registerNumber());

        add(div);
        open();
    }
}
