package pl.crewops.view.component.notification;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import pl.crewops.dto.breakdown.BreakdownDTO;

public class AddBreakdownNotification extends Notification {

    public AddBreakdownNotification(BreakdownDTO breakdownDTO) {
        addClassName("add-breakdown-notification");
        addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        setPosition(Notification.Position.TOP_END);
        setDuration(5000);

        var div = new Div();
        div.addClassName("add-breakdown-notification-div");
        div.setText(getTranslation("addBreakdownNotification.successAddBreakdown") + " "
                + breakdownDTO.machine().registerNumber());

        add(div);
        open();
    }
}
