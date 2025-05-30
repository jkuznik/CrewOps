package pl.crewops.view.component.notification;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import pl.crewops.dto.vehicle.VehicleDTO;

public class UpdateVehicleNotification extends Notification {

    public UpdateVehicleNotification(VehicleDTO vehicleDTO) {
        addClassName("update-vehicle-notification");
        addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        setPosition(Notification.Position.TOP_END);
        setDuration(5000);

        var div = new Div();
        div.addClassName("update-vehicle-notification-div");
        div.setText(getTranslation("updateVehicleNotification.messagePrefix") + vehicleDTO.registerNumber());

        add(div);
        open();
    }
}
