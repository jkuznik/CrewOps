package pl.crewops.view.component.notification;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import pl.crewops.dto.employee.EmployeeDTO;

public class SaveEmployeeNotification {

    public SaveEmployeeNotification(EmployeeDTO employeeDTO) {
        Notification notification = new Notification();
        notification.addClassName("save-employee-notification");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.TOP_END);
        notification.setDuration(5000);

        Div div = new Div();
        div.addClassName("save-employee-notification-div");
        div.setText(div.getTranslation("saveEmployeeNotification.successAddEmployee") + employeeDTO.firstName() + " "
                + employeeDTO.lastName());
        notification.add(div);
        notification.open();
    }
}
