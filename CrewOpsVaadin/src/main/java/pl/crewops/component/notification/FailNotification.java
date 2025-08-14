package pl.crewops.component.notification;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class FailNotification extends Notification {

    public FailNotification(String exceptionMessage) {
        addClassName("fail-notification");
        addThemeVariants(NotificationVariant.LUMO_ERROR);
        setPosition(Notification.Position.TOP_END);
        setDuration(5000);

        var div = new Div();
        div.setText(exceptionMessage);

        add(div);
        open();
    }
}
