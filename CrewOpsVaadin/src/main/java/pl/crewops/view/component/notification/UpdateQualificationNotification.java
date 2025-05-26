package pl.crewops.view.component.notification;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import pl.crewops.dto.qualification.QualificationDTO;

public class UpdateQualificationNotification {

    private final Notification notification = new Notification();

    public UpdateQualificationNotification(QualificationDTO qualificationDTO) {
        notification.addClassName("update-employee-notification");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.TOP_END);
        notification.setDuration(5000);

        Div div = new Div();
        div.addClassName("update-employee-notification-div");
        div.setText(
                div.getTranslation("updateQualificationNotification.messagePrefix") + qualificationDTO.description());
        notification.add(div);
        notification.open();
    }
}
