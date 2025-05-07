package pl.crewops.view.component.notification;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import pl.crewops.dto.employee.EmployeeDTO;

public class SaveEmployeeNotification {

    private final Notification notification = new Notification();

    public SaveEmployeeNotification(EmployeeDTO employeeDTO) {
        notification.addClassName("save-employee-notification");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.TOP_END);
        notification.setDuration(5000);

        Div div = new Div();
        div.addClassName("save-employee-notification-div");
        div.setText("Successfully add employee " + employeeDTO.firstName() + " " + employeeDTO.lastName());
        notification.add(div);
        notification.open();
    }
}
