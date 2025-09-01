package pl.crewops.component.notification;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class InfoNotification extends Notification {
    public InfoNotification(String message) {
        addThemeVariants(NotificationVariant.LUMO_PRIMARY);
        setPosition(Notification.Position.TOP_END);
        setDuration(5000);

        setText(message);
        open();
    }
}
