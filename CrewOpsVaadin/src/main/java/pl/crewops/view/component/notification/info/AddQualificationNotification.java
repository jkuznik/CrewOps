package pl.crewops.view.component.notification.info;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import pl.crewops.dto.qualification.QualificationDTO;

public class AddQualificationNotification extends Notification {

    public AddQualificationNotification(QualificationDTO qualificationDTO) {
        addClassName("add-qualification-notification");
        addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        setPosition(Notification.Position.TOP_END);
        setDuration(5000);

        var div = new Div();
        div.addClassName("add-qualification-notification-div");
        div.setText(getTranslation("addQualificationNotification.successAddQualification")
                + qualificationDTO.description());

        add(div);
        open();
    }
}
