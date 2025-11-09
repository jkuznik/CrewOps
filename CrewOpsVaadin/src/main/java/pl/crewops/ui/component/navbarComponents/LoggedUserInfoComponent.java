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
import pl.crewops.ui.component.form.LoginForm;
import pl.crewops.ui.component.notification.FailNotification;
import pl.crewops.ui.component.notification.auth.EndSessionNotification;
import pl.crewops.ui.view.HomeView;
import pl.crewops.ui.view.MessageView;
import pl.crewops.ui.view.ProfileView;
import pl.crewops.util.AuthenticationResolver;

@Log4j2
public class LoggedUserInfoComponent extends HorizontalLayout {
    private final CoreAPI coreAPI;
    private final AuthenticationResolver authenticationResolver;

    private static final Map<UI, Boolean> startedMap = new WeakHashMap<>();
    private boolean sessionEnded = false;

    public LoggedUserInfoComponent(CoreAPI coreAPI, AuthenticationResolver authenticationResolver) {
        this.coreAPI = coreAPI;
        this.authenticationResolver = authenticationResolver;

        addClassName("logged-user-info");
        setMargin(true);

        if (authenticationResolver.principalIsAuthenticated()) {
            loggedUserInfo();
        } else {
            add(new LoginForm(coreAPI, authenticationResolver));
        }
    }

    private void loggedUserInfo() {
        Button logoutButton = new Button(getTranslation("loggedUserInfo.logout"));
        logoutButton.addClickListener(event -> logout());
        logoutButton.addThemeVariants(ButtonVariant.LUMO_WARNING);
        logoutButton.setWidth("160px");

        var logoutButtonAndLanguageSelector = new HorizontalLayout();
        logoutButtonAndLanguageSelector.setSpacing(true);
        logoutButtonAndLanguageSelector.add(logoutButton, new LanguageSelectorComponent());

        final var token = authenticationResolver.getPrincipal().getToken();
        UserInformation userInformation = getInfo(token);
        add(userProfileComponent(userInformation), logoutButtonAndLanguageSelector);
    }

    private UserInformation getInfo(String token) {
        log.info("Getting loggedUserInfo");

        try {
            CompanyDTO companyDTO = coreAPI.getCompanyById(authenticationResolver.extractCompanyIdFromToken(token))
                    .orElseThrow(() -> new RuntimeException("Can't retrieve company name for logged user info"));
            var companyName = companyDTO.name();

            var employeeDTO = coreAPI.getEmployeeById(authenticationResolver.extractEmployeeIdFromToken(token))
                    .orElseThrow(NoSuchElementException::new);
            return new UserInformation(
                    companyName,
                    employeeDTO.firstName(),
                    employeeDTO.lastName(),
                    authenticationResolver
                            .extractExpiresAtFromToken(token)
                            .toInstant()
                            .getEpochSecond());
        } catch (RuntimeException e) {
            new FailNotification(e.getMessage());
            return new UserInformation(
                    null,
                    "system",
                    "issue",
                    authenticationResolver
                            .extractExpiresAtFromToken(token)
                            .toInstant()
                            .getEpochSecond());
        } catch (NotAuthenticatedException e) {
            log.error("JWT token not authenticated during retrieve user info" + e.getMessage());
            return new UserInformation(
                    null,
                    "system",
                    "issue",
                    authenticationResolver
                            .extractExpiresAtFromToken(token)
                            .toInstant()
                            .getEpochSecond());
        }
    }

    private Component userProfileComponent(UserInformation userInformation) {
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

    private void logout() {
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
