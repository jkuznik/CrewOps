package pl.crewops.component.notification.auth;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class EndSessionNotification extends Notification {

    public EndSessionNotification(UI ui, Runnable onSessionEnd) {
        addClassName("end-session-notification");

        setDuration(0);
        addThemeVariants(NotificationVariant.LUMO_ERROR);
        setPosition(Notification.Position.MIDDLE);

        Div message = new Div();
        message.setText(message.getTranslation("endSessionNotification.message"));
        message.getStyle().set("color", "white").set("white-space", "pre-line").set("text-align", "center");

        Button accept = new Button();
        accept.setText(accept.getTranslation("endSessionNotification.accept"));
        accept.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        accept.addClickListener(e -> {
            close();
            onSessionEnd.run();
            enableUI(ui);
        });

        add(message, accept);

        disableUI(ui);
    }

    public void show() {
        open();
    }

    private void disableUI(UI ui) {
        ui.access(() -> ui.getElement().getStyle().set("pointer-events", "none"));
    }

    private void enableUI(UI ui) {
        ui.access(() -> ui.getElement().getStyle().remove("pointer-events"));
    }
}
