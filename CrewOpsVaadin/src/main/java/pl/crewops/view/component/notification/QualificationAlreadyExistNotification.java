package pl.crewops.view.component.notification;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class QualificationAlreadyExistNotification {
    private final Notification notification = new Notification();

    public QualificationAlreadyExistNotification(String description) {
        notification.addClassName("update-vehicle-notification");
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.TOP_END);
        notification.setDuration(5000);

        Div div = new Div();
        div.addClassName("update-vehicle-notification-div");
        div.setText(div.getTranslation("qualificationAlreadyExistNotification.messagePrefix")
                + description
                + div.getTranslation("qualificationAlreadyExistNotification.messageSuffix"));
        notification.add(div);
        notification.open();
    }
}
