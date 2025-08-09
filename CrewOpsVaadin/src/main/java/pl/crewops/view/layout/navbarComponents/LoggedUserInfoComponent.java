package pl.crewops.view.layout.navbarComponents;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.WeakHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.log4j.Log4j2;
import pl.crewops.component.form.LoginForm;
import pl.crewops.component.notification.FailNotification;
import pl.crewops.component.notification.auth.EndSessionNotification;
import pl.crewops.dto.company.CompanyDTO;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtServiceVaadin;
import pl.crewops.util.RoleResolver;
import pl.crewops.view.EmployeeView;
import pl.crewops.view.HomeView;

@Log4j2
public class LoggedUserInfoComponent extends HorizontalLayout {
    private static final Map<UI, Boolean> startedMap = new WeakHashMap<>();
    private boolean sessionEnded = false;

    public LoggedUserInfoComponent(CoreAPI coreAPI, JwtServiceVaadin jwtService, RoleResolver roleResolver) {
        addClassName("logged-user-info");

        if (roleResolver.principalIsAuthenticated()) {
            add(loggedUserInfo(coreAPI, jwtService, roleResolver));
        } else {
            add(new LoginForm(coreAPI, jwtService));
        }
    }

    private Component loggedUserInfo(CoreAPI coreAPI, JwtServiceVaadin jwtService, RoleResolver roleResolver) {
        var infoLayout = new VerticalLayout();
        infoLayout.setWidthFull();
        infoLayout.setSpacing(true);
        infoLayout.setAlignItems(FlexComponent.Alignment.END);

        if (roleResolver.principalHasSystemAdminRole()) {
            infoLayout.add(new CustomerRegistryButton(coreAPI, roleResolver));
        }

        Button logoutButton = new Button(getTranslation("loggedUserInfo.logout"));
        logoutButton.addClickListener(event -> logout(coreAPI, roleResolver));
        logoutButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        var buttonAndLanguageSelector = new HorizontalLayout();
        buttonAndLanguageSelector.setSpacing(true);
        buttonAndLanguageSelector.add(logoutButton, new LanguageSelectorComponent());

        final var token = roleResolver.getPrincipal().getToken();
        UserInformation userInformation = getInfo(coreAPI, jwtService, token);
        infoLayout.add(displayUserInfo(coreAPI, userInformation, roleResolver), buttonAndLanguageSelector);
        return infoLayout;
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
            //        } catch (NoSuchElementException e) {
            //            log.error("JWT token not authenticated during retrieve user info" + e.getMessage());
            //            return new UserInformation(
            //                    null,
            //                    "system",
            //                    "issue",
            //                    jwtService.extractExpiresAt(token).toInstant().getEpochSecond());
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

    private VerticalLayout displayUserInfo(
            CoreAPI coreAPI, UserInformation userInformation, RoleResolver roleResolver) {
        Span companyName = new Span(userInformation.companyName);
        Span userName = new Span(userInformation.userName + " " + userInformation.userLastname);

        var container = new VerticalLayout(companyName, userName);
        container.getStyle().set("display", "flex");
        container.getStyle().set("align-items", "center");
        container.getStyle().set("gap", "1rem");

        long expiryEpoch = userInformation.expiryEpoch;
        UI ui = UI.getCurrent();

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

                        // TODO: fix this logic to enforce redirect user to HomeView in case if currently user use
                        // dialog component
                        ui.access(() -> {
                            new EndSessionNotification(ui, () -> {
                                        roleResolver.unauthenticatePrincipal();
                                        coreAPI.setAuthentication(false);
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

        return container;
    }

    private void logout(CoreAPI coreAPI, RoleResolver roleResolver) {
        UI ui = UI.getCurrent();
        roleResolver.unauthenticatePrincipal();
        coreAPI.setAuthentication(false);
        String currentLocation = ui.getInternals().getActiveViewLocation().getPath();
        if (currentLocation.isEmpty()) {
            ui.getPage().reload();
        } else {
            ui.navigate(HomeView.class);
        }
    }

    private record UserInformation(String companyName, String userName, String userLastname, long expiryEpoch) {}
}
