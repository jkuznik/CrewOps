package pl.crewops.ui.component.navbarComponents;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.WeakHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.log4j.Log4j2;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.dto.company.CompanyDTO;
import pl.crewops.security.jwt.JwtServiceVaadin;
import pl.crewops.ui.component.form.LoginForm;
import pl.crewops.ui.component.notification.FailNotification;
import pl.crewops.ui.component.notification.auth.EndSessionNotification;
import pl.crewops.ui.view.EmployeeView;
import pl.crewops.ui.view.HomeView;
import pl.crewops.ui.view.MessageView;
import pl.crewops.ui.view.ProfileView;
import pl.crewops.util.AuthenticationResolver;

@Log4j2
public class LoggedUserInfoComponent extends HorizontalLayout {
    private static final Map<UI, Boolean> startedMap = new WeakHashMap<>();
    private boolean sessionEnded = false;

    public LoggedUserInfoComponent(
            CoreAPI coreAPI, JwtServiceVaadin jwtService, AuthenticationResolver authenticationResolver) {
        addClassName("logged-user-info");
        setMargin(true);

        if (authenticationResolver.principalIsAuthenticated()) {
            loggedUserInfo(coreAPI, jwtService, authenticationResolver);
        } else {
            add(new LoginForm(coreAPI, jwtService));
        }
    }

    private void loggedUserInfo(
            CoreAPI coreAPI, JwtServiceVaadin jwtService, AuthenticationResolver authenticationResolver) {
        //        var infoLayout = new VerticalLayout();
        //        infoLayout.setSpacing(true);
        //        infoLayout.setAlignItems(FlexComponent.Alignment.END);
        //        infoLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        Button logoutButton = new Button(getTranslation("loggedUserInfo.logout"));
        logoutButton.addClickListener(event -> logout(authenticationResolver));
        logoutButton.addThemeVariants(ButtonVariant.LUMO_WARNING);
        logoutButton.setWidth("160px");

        var logoutButtonAndLanguageSelector = new HorizontalLayout();
        logoutButtonAndLanguageSelector.setSpacing(true);
        logoutButtonAndLanguageSelector.add(logoutButton, new LanguageSelectorComponent());

        final var token = authenticationResolver.getPrincipal().getToken();
        UserInformation userInformation = getInfo(coreAPI, jwtService, token);
        add(userProfileComponent(userInformation, authenticationResolver), logoutButtonAndLanguageSelector);
    }

    private UserInformation getInfo(CoreAPI coreAPI, JwtServiceVaadin jwtService, String token) {
        log.info("Getting loggedUserInfo");

        try {
            CompanyDTO companyDTO = coreAPI.getCompanyById(jwtService.extractCompanyId(token))
                    .orElseThrow(() -> new RuntimeException("Can't retrieve company name for logged user info"));
            var companyName = companyDTO.name();

            var employeeDTO = coreAPI.getEmployeeById(jwtService.extractEmployeeId(token))
                    .orElseThrow(NoSuchElementException::new);
            return new UserInformation(
                    companyName,
                    employeeDTO.firstName(),
                    employeeDTO.lastName(),
                    jwtService.extractExpiresAt(token).toInstant().getEpochSecond());
        } catch (RuntimeException e) {
            new FailNotification(e.getMessage());
            return new UserInformation(
                    null,
                    "system",
                    "issue",
                    jwtService.extractExpiresAt(token).toInstant().getEpochSecond());
        } catch (NotAuthenticatedException e) {
            log.error("JWT token not authenticated during retrieve user info" + e.getMessage());
            UI.getCurrent().navigate(EmployeeView.class);
            return new UserInformation(
                    null,
                    "system",
                    "issue",
                    jwtService.extractExpiresAt(token).toInstant().getEpochSecond());
        }
    }

    private Component userProfileComponent(
            UserInformation userInformation, AuthenticationResolver authenticationResolver) {
        // Message button
        Button messageButton = new Button();
        messageButton.setIcon(VaadinIcon.ENVELOPE.create());
        messageButton.getStyle().set("margin-right", "0.5rem");

        messageButton.addClickListener(event -> {
            UI.getCurrent().navigate(MessageView.class);
        });

        // User profile button
        Button profileButton = new Button();
        profileButton.setIcon(VaadinIcon.USER.create());
        profileButton.getStyle().set("margin-right", "0.5rem");

        profileButton.addClickListener(event -> {
            UI.getCurrent().navigate(ProfileView.class);
        });

        Span userName = new Span(userInformation.userName + " " + userInformation.userLastname);
        HorizontalLayout userLayout = new HorizontalLayout(messageButton, profileButton, userName);
        userLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        userLayout.setSpacing(true);

        long expiryEpoch = userInformation.expiryEpoch;
        UI ui = UI.getCurrent();

        if (startedMap.getOrDefault(ui, false)) {
            return userLayout;
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
                            new EndSessionNotification(ui, () -> {
                                        authenticationResolver.unauthenticatePrincipal();
                                        ui.navigate(HomeView.class);
                                        ui.getPage().reload();
                                    })
                                    .show();
                        });
                    }
                },
                0,
                1,
                TimeUnit.SECONDS);

        return userLayout;
    }

    private void logout(AuthenticationResolver authenticationResolver) {
        UI ui = UI.getCurrent();
        authenticationResolver.unauthenticatePrincipal();
        String currentLocation = ui.getInternals().getActiveViewLocation().getPath();
        if (currentLocation.isEmpty()) {
            ui.getPage().reload();
        } else {
            ui.navigate(HomeView.class);
        }
    }

    private record UserInformation(String companyName, String userName, String userLastname, long expiryEpoch) {}
}
