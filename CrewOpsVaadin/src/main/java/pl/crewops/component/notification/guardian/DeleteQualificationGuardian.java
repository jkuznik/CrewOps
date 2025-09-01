package pl.crewops.component.notification.guardian;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import pl.crewops.model.QualificationFormModel;

public class DeleteQualificationGuardian extends Notification {

    private static final String CONFIRMATION_VALUE = "ok";

    public DeleteQualificationGuardian(QualificationFormModel qualificationFormModel, Runnable onDeleteConfirmed) {
        addClassName("delete-qualification-guardian");
        addThemeVariants(NotificationVariant.LUMO_ERROR);
        setPosition(Position.MIDDLE);
        setDuration(0);

        var qualification = qualificationFormModel.getDescription();

        var message = new Div();
        message.addClassName("delete-qualification-guardian-div");
        message.setText(getTranslation("deleteQualificationGuardian.message") + " " + qualification);

        var confirmTextField = new TextField(
                getTranslation("deleteQualificationGuardian.confirmTextField") + " " + CONFIRMATION_VALUE);
        confirmTextField.setWidthFull();
        confirmTextField.setValueChangeMode(ValueChangeMode.EAGER);

        var deleteButton = new Button(getTranslation("deleteQualificationGuardian.deleteButton"));
        deleteButton.setEnabled(false);
        deleteButton.addClickListener(e -> {
            onDeleteConfirmed.run();
            close();
        });

        var cancelButton = new Button(getTranslation("deleteQualificationGuardian.cancelButton"));
        cancelButton.addClickListener(e -> {
            close();
        });

        confirmTextField.addValueChangeListener(e -> {
            var value = e.getValue() != null ? e.getValue().trim() : "";
            deleteButton.setEnabled(value.equalsIgnoreCase(CONFIRMATION_VALUE));
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
