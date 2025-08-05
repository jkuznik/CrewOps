package pl.crewops.view.component.notification.auth;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class LoginFailNotification extends Notification {

    public LoginFailNotification() {
        addClassName("login-failed-notification");
        addThemeVariants(NotificationVariant.LUMO_ERROR);
        setPosition(Notification.Position.TOP_END);
        setDuration(5000);

        var div = new Div();
        div.addClassName("login-failed-notification-div");
        div.setText(getTranslation("loginFailedNotification.message"));

        add(div);
        open();
    }
}
