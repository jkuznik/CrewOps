package pl.crewops.view.component.notification;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import pl.crewops.dto.vehicle.VehicleDTO;

public class UpdateVehicleNotification {
    private final Notification notification = new Notification();

    public UpdateVehicleNotification(VehicleDTO vehicleDTO) {
        notification.addClassName("update-vehicle-notification");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.TOP_END);
        notification.setDuration(5000);

        Div div = new Div();
        div.addClassName("update-vehicle-notification-div");
        div.setText(div.getTranslation("updateVehicleNotification.messagePrefixgit ") + vehicleDTO.registerNumber());
        notification.add(div);
        notification.open();
    }
}
