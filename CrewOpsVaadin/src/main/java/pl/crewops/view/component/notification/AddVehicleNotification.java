package pl.crewops.view.component.notification;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import pl.crewops.dto.vehicle.VehicleDTO;

public class AddVehicleNotification extends Notification {

    public AddVehicleNotification(VehicleDTO vehicleDTO) {
        addClassName("add-vehicle-notification");
        addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        setPosition(Notification.Position.TOP_END);
        setDuration(5000);

        var div = new Div();
        div.addClassName("add-vehicle-notification-div");
        div.setText(getTranslation("addVehicleNotification.messagePrefix") + vehicleDTO.registerNumber());

        add(div);
        open();
    }
}
