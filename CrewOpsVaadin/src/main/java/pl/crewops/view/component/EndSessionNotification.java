package pl.crewops.view.component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class EndSessionNotification {
    private final Notification notification;
    private final Button accept = new Button("Accept");

    public EndSessionNotification(UI ui, Runnable onSessionEnd) {
        notification = new Notification();
        notification.setDuration(0);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.addClassName("custom-notification");

        Div message = new Div();
        message.setText("Your session has expired.\nPlease log in again.");
        message.getStyle()
                .set("color", "white")
                .set("white-space", "pre-line") // zachowaj \n jako nową linię
                .set("text-align", "center");

        accept.setText("Accept");
        accept.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        accept.addClickListener(e -> {
            notification.close();
            onSessionEnd.run();
            enableUI(ui);
        });

        VerticalLayout content = new VerticalLayout(message, accept);
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.addClassName("notification-layout");

        notification.add(content);
        disableUI(ui);
    }

    public void show() {
        notification.open();
    }

    private void disableUI(UI ui) {
        ui.access(() -> ui.getElement().getStyle().set("pointer-events", "none"));
    }

    private void enableUI(UI ui) {
        ui.access(() -> ui.getElement().getStyle().remove("pointer-events"));
    }
}
