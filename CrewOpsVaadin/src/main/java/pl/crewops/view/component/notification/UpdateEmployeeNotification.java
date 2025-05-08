package pl.crewops.view.component.notification;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import pl.crewops.dto.employee.EmployeeDTO;

public class UpdateEmployeeNotification {
    private final Notification notification = new Notification();

    public UpdateEmployeeNotification(EmployeeDTO employeeDTO) {
        notification.addClassName("update-employee-notification");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.TOP_END);
        notification.setDuration(5000);

        Div div = new Div();
        div.addClassName("update-employee-notification-div");
        div.setText("Successfully update employee " + employeeDTO.firstName() + " " + employeeDTO.lastName());
        notification.add(div);
        notification.open();
    }
}
