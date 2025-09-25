package pl.crewops.component.notification;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class SuccessNotification extends Notification {
    public SuccessNotification(String message) {
        addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        setPosition(Notification.Position.TOP_CENTER);
        setDuration(5000);

        setText(message);

        open();
    }
}
