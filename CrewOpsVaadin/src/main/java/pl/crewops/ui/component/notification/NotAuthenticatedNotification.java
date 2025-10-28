package pl.crewops.ui.component.notification;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import pl.crewops.ui.view.HomeView;

public class NotAuthenticatedNotification extends Notification {

    public NotAuthenticatedNotification(String exceptionMessage) {

        addThemeVariants(NotificationVariant.LUMO_ERROR);
        setPosition(Position.TOP_CENTER);
        setDuration(5000);

        setText(exceptionMessage);

        UI.getCurrent().navigate(HomeView.class);
    }
}
