package pl.crewops.view.component.notification;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import pl.crewops.dto.employee.EmployeeDTO;

public class UpdateEmployeeNotification extends Notification {

    public UpdateEmployeeNotification(EmployeeDTO employeeDTO) {
        addClassName("update-employee-notification");
        addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        setPosition(Notification.Position.TOP_END);
        setDuration(5000);

        var div = new Div();
        div.addClassName("update-employee-notification-div");
        div.setText(getTranslation("updateEmployeeNotification.messagePrefix") + employeeDTO.firstName() + " "
                + employeeDTO.lastName());

        add(div);
        open();
    }
}
