package pl.crewops.ui.component.notification;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class FailNotification extends Notification {

    public FailNotification(String exceptionMessage) {
        addThemeVariants(NotificationVariant.LUMO_ERROR);
        setPosition(Position.TOP_CENTER);
        setDuration(5000);

        setText(exceptionMessage);

        open();
    }
}
