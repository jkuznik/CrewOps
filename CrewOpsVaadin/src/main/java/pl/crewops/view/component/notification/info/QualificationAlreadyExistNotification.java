package pl.crewops.view.component.notification.info;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class QualificationAlreadyExistNotification extends Notification {

    public QualificationAlreadyExistNotification(String description) {
        addClassName("update-qualification-notification");
        addThemeVariants(NotificationVariant.LUMO_ERROR);
        setPosition(Notification.Position.TOP_END);
        setDuration(5000);

        var div = new Div();
        div.addClassName("update-qualification-notification-div");
        div.setText(getTranslation("qualificationAlreadyExistNotification.messagePrefix")
                + description
                + getTranslation("qualificationAlreadyExistNotification.messageSuffix"));

        add(div);
        open();
    }
}
