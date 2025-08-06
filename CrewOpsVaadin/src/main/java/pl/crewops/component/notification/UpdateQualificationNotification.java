package pl.crewops.component.notification;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import pl.crewops.dto.qualification.QualificationDTO;

public class UpdateQualificationNotification extends Notification {

    public UpdateQualificationNotification(QualificationDTO qualificationDTO) {
        addClassName("update-employee-notification");
        addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        setPosition(Notification.Position.TOP_END);
        setDuration(5000);

        var div = new Div();
        div.addClassName("update-employee-notification-div");
        div.setText(getTranslation("updateQualificationNotification.messagePrefix") + qualificationDTO.description());

        add(div);
        open();
    }
}
