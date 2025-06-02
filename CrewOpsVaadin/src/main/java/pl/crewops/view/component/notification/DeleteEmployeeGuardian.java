package pl.crewops.view.component.notification;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import pl.crewops.model.EmployeeFormModel;

public class DeleteEmployeeGuardian extends Notification {

    public DeleteEmployeeGuardian(EmployeeFormModel employeeFormModel, Runnable onDeleteConfirmed) {
        addClassName("delete-employee-guardian");
        addThemeVariants(NotificationVariant.LUMO_ERROR);
        setPosition(Position.MIDDLE);
        setDuration(0);

        String fullName = employeeFormModel.getFirstName() + " " + employeeFormModel.getLastName();

        var message = new Div();
        message.addClassName("delete-employee-guardian-div");
        message.setText(getTranslation("deleteEmployeeGuardian.warningMessage") + " " + fullName);

        var confirmTextField = new TextField(getTranslation("deleteEmployeeGuardian.confirmTextField"));
        confirmTextField.setWidthFull();
        confirmTextField.setValueChangeMode(ValueChangeMode.EAGER);

        var deleteButton = new Button(getTranslation("deleteEmployeeGuardian.deleteButton"));
        deleteButton.setEnabled(false);
        deleteButton.addClickListener(e -> {
            onDeleteConfirmed.run();
            close();
        });

        var cancelButton = new Button(getTranslation("deleteEmployeeGuardian.cancelButton"));
        cancelButton.addClickListener(e -> {
            close();
        });

        confirmTextField.addValueChangeListener(e -> {
            String value = e.getValue() != null ? e.getValue().trim() : "";
            deleteButton.setEnabled(value.equalsIgnoreCase(fullName));
        });

        var buttons = new HorizontalLayout(deleteButton, cancelButton);
        buttons.setSpacing(true);

        var layout = new VerticalLayout(message, confirmTextField, buttons);
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setWidth("400px");

        add(layout);
        open();
    }
}
