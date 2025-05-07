package pl.crewops.view.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtInfoService;
import pl.crewops.view.HomeView;
import pl.crewops.view.component.notification.EndSessionNotification;
import pl.crewops.view.form.LoginForm;

@SpringComponent
public class LoggedUserInfoComponent extends HorizontalLayout {
    private static final Map<UI, Boolean> startedMap = new WeakHashMap<>();
    private boolean sessionEnded = false;

    public LoggedUserInfoComponent(CoreAPI coreAPI, JwtInfoService jwtInfoService) {
        addClassName("logged-user-info");

        if (jwtInfoService.validToken()) {
            add(loggedUserInfo(jwtInfoService));
        } else {
            LoginForm loginForm = new LoginForm(coreAPI, jwtInfoService);
            add(loginForm);
        }
    }

    private Component loggedUserInfo(JwtInfoService jwtInfoService) {
        var infoLayout = new HorizontalLayout();
        infoLayout.setWidthFull();
        infoLayout.setSpacing(true);

        H1 title = new H1("You are logged as ");

        Button logoutButton = new Button("Logout");
        logoutButton.addClickListener(event -> logout(jwtInfoService));
        logoutButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        infoLayout.add(title, getInfo(jwtInfoService), logoutButton);
        return infoLayout;
    }

    private Component getInfo(JwtInfoService jwtInfoService) {
        Span userInfo = new Span(jwtInfoService.getFirstName() + " " + jwtInfoService.getLastName());
        Span countdown = new Span();
        countdown.getStyle().set("font-weight", "bold");
        countdown.getStyle().set("margin-left", "1rem");

        Div container = new Div(userInfo, countdown);
        container.getStyle().set("display", "flex");
        container.getStyle().set("align-items", "center");
        container.getStyle().set("gap", "1rem");

        long expiryEpoch = jwtInfoService.getExpires().toInstant().getEpochSecond();
        UI ui = UI.getCurrent();

        // 🔒 Zapobiegaj wielokrotnemu uruchamianiu timera dla jednej sesji
        if (startedMap.getOrDefault(ui, false)) {
            return container;
        }
        startedMap.put(ui, true);

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(
                () -> {
                    long now = System.currentTimeMillis() / 1000;
                    long secondsLeft = expiryEpoch - now;

                    if (secondsLeft <= 0 && !sessionEnded) {
                        sessionEnded = true;
                        scheduler.shutdown();
                        ui.access(() -> {
                            countdown.setText("Token expired");
                            new EndSessionNotification(ui, () -> {
                                        jwtInfoService.resetAuthentication();
                                        String currentLocation = ui.getInternals()
                                                .getActiveViewLocation()
                                                .getPath();
                                        if (currentLocation.equals("")) {
                                            ui.getPage().reload();
                                        } else {
                                            ui.navigate(HomeView.class);
                                        }
                                    })
                                    .show();
                        });
                        return;
                    }

                    String timeFormatted = formatDuration(secondsLeft);
                    ui.access(() -> countdown.setText("Token expires in: " + timeFormatted));
                },
                0,
                1,
                TimeUnit.SECONDS);

        return container;
    }

    private String formatDuration(long seconds) {
        long mins = seconds / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }

    private void logout(JwtInfoService jwtInfoService) {
        jwtInfoService.resetAuthentication();
        UI.getCurrent().navigate(HomeView.class);
    }
}
