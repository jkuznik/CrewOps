package pl.crewops.view.component.navbarComponents;

import static pl.crewops.model.auth.RoleType.*;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.VaadinSession;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.WeakHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.crewops.dto.company.CompanyDTO;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.auth.RoleGrantedAuthority;
import pl.crewops.security.custom.UserPrincipal;
import pl.crewops.security.jwt.JwtServiceVaadin;
import pl.crewops.view.HomeView;
import pl.crewops.view.component.form.LoginForm;
import pl.crewops.view.component.notification.EndSessionNotification;

public class LoggedUserInfoComponent extends HorizontalLayout {
    private static final Map<UI, Boolean> startedMap = new WeakHashMap<>();
    private final Authentication authentication;
    private boolean sessionEnded = false;
    private UserPrincipal principal;

    public LoggedUserInfoComponent(CoreAPI coreAPI, JwtServiceVaadin jwtService) {
        addClassName("logged-user-info");

        this.authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {

            this.principal = userPrincipal;
            add(loggedUserInfo(coreAPI, jwtService));

        } else {
            add(new LoginForm(coreAPI, jwtService));
        }
    }

    private Component loggedUserInfo(CoreAPI coreAPI, JwtServiceVaadin jwtService) {
        var infoLayout = new HorizontalLayout();
        infoLayout.setWidthFull();
        infoLayout.setSpacing(true);

        Button logoutButton = new Button(getTranslation("loggedUserInfo.logout"));
        logoutButton.addClickListener(event -> logout(coreAPI));
        logoutButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        UserInformation userInformation = getInfo(coreAPI, jwtService);
        if (principal.getAuthorities().contains(new RoleGrantedAuthority(SYSTEM_ADMIN))) {
            infoLayout.add(new CustomerRegistryButton(coreAPI));
        }
        infoLayout.add(displayUserInfo(userInformation), logoutButton);
        return infoLayout;
    }

    private UserInformation getInfo(CoreAPI coreAPI, JwtServiceVaadin jwtService) {

        String companyName = "";
        try {
            CompanyDTO companyDTO = coreAPI.getCompanyById(jwtService.extractCompanyId(principal.getToken()))
                    .orElseThrow(() -> new RuntimeException("Can't retrieve company name for logged user info"));
            companyName = companyDTO.name();

        } catch (NotAuthenticatedException e) {
            System.out.println("JWT token not authenticated during retrieve user info");
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        try {
            var employeeDTO = coreAPI.getEmployeeById(jwtService.extractEmployeeId(principal.getToken()))
                    .orElseThrow(NoSuchElementException::new);
            return new UserInformation(
                    companyName,
                    employeeDTO.firstName(),
                    employeeDTO.lastName(),
                    jwtService
                            .extractExpiresAt(principal.getToken())
                            .toInstant()
                            .getEpochSecond());
        } catch (NotAuthenticatedException e) {
            System.out.println("Some problem occurred while retrieving employee information");
            return new UserInformation(
                    companyName,
                    "system",
                    "issue",
                    jwtService
                            .extractExpiresAt(principal.getToken())
                            .toInstant()
                            .getEpochSecond());
        }
    }

    private Component displayUserInfo(UserInformation userInformation) {
        Span companyName = new Span(userInformation.companyName);
        Span userName = new Span(userInformation.userName + " " + userInformation.userLastname);

        Div container = new Div(companyName, userName);
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

                        ui.access(() -> {
                            new EndSessionNotification(ui, () -> {
                                        authentication.setAuthenticated(false);
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
                    }
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

    private void logout(CoreAPI coreAPI) {
        authentication.setAuthenticated(false);
        coreAPI.setAuthentication(false);
        UI ui = UI.getCurrent();
        ui.access(() -> {
            SecurityContextHolder.clearContext();
            VaadinSession.getCurrent().close();
            ui.getPage().reload();
        });
    }

    private record UserInformation(String companyName, String userName, String userLastname, long expiryEpoch) {}
}
