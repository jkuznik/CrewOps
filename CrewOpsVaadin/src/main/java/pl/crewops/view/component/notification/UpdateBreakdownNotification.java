package pl.crewops.view.component.notification;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class UpdateBreakdownNotification extends Notification {
    public UpdateBreakdownNotification() {
        addClassName("update-breakdown-notification");
        addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        setPosition(Notification.Position.TOP_END);
        setDuration(5000);

        var div = new Div();
        div.addClassName("update-breakdown-notification-div");
        div.setText(getTranslation("updateBreakdownNotification.messagePrefix"));

        add(div);
        open();
    }
}
