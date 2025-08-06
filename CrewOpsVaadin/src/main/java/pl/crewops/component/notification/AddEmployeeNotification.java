package pl.crewops.component.notification;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import pl.crewops.dto.employee.EmployeeDTO;

public class AddEmployeeNotification extends Notification {

    public AddEmployeeNotification(EmployeeDTO employeeDTO) {
        addClassName("add-employee-notification");
        addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        setPosition(Notification.Position.TOP_END);
        setDuration(5000);

        var div = new Div();
        div.addClassName("add-employee-notification-div");
        div.setText(getTranslation("addEmployeeNotification.successAddEmployee") + employeeDTO.firstName() + " "
                + employeeDTO.lastName());

        add(div);
        open();
    }
}
