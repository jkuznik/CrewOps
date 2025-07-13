package pl.crewops.view.component.navbarComponents;

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
import pl.crewops.security.jwt.JwtService;
import pl.crewops.view.HomeView;
import pl.crewops.view.component.form.LoginForm;
import pl.crewops.view.component.notification.EndSessionNotification;

public class LoggedUserInfoComponent extends HorizontalLayout {
    private static final Map<UI, Boolean> startedMap = new WeakHashMap<>();
    private final Authentication authentication;
    private boolean sessionEnded = false;
    private UserPrincipal principal;

    public LoggedUserInfoComponent(CoreAPI coreAPI, JwtService jwtService) {
        addClassName("logged-user-info");

        this.authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserPrincipal userPrincipal
                && jwtService.validToken(userPrincipal.getToken())) {

            this.principal = userPrincipal;
            add(loggedUserInfo(coreAPI, jwtService), new LanguageSelectorComponent());

        } else {
            add(new LoginForm(coreAPI, jwtService), new LanguageSelectorComponent());
        }
    }

    private Component loggedUserInfo(CoreAPI coreAPI, JwtService jwtService) {
        var infoLayout = new HorizontalLayout();
        infoLayout.setWidthFull();
        infoLayout.setSpacing(true);

        Button logoutButton = new Button(getTranslation("loggedUserInfo.logout"));
        logoutButton.addClickListener(event -> logout(coreAPI));
        logoutButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        UserInformation userInformation = getInfo(coreAPI, jwtService);
        if (principal.getAuthorities().contains(new RoleGrantedAuthority("ROLE_ADMIN"))) {
            infoLayout.add(new CompanyCreator(coreAPI));
        }
        infoLayout.add(displayUserInfo(userInformation), logoutButton);
        return infoLayout;
    }

    private UserInformation getInfo(CoreAPI coreAPI, JwtService jwtService) {

        String companyName = "";
        try {
            CompanyDTO companyDTO = coreAPI.getCompanyById(jwtService.getTenantCompanyId(principal.getToken()))
                    .orElseThrow(() -> new RuntimeException("Can't retrieve company name for logged user info"));
            companyName = companyDTO.name();

        } catch (NotAuthenticatedException e) {
            System.out.println("JWT token not authenticated during retrieve user info");
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        try {
            var employeeDTO = coreAPI.getEmployeeById(jwtService.getEmployeeId(principal.getToken()))
                    .orElseThrow(NoSuchElementException::new);
            return new UserInformation(
                    companyName,
                    employeeDTO.firstName(),
                    employeeDTO.lastName(),
                    jwtService.getExpiration(principal.getToken()).toInstant().getEpochSecond());
        } catch (NotAuthenticatedException e) {
            System.out.println("Some problem occurred while retrieving employee information");
            return new UserInformation(
                    companyName,
                    "system",
                    "issue",
                    jwtService.getExpiration(principal.getToken()).toInstant().getEpochSecond());
        }
    }

    private Component displayUserInfo(UserInformation userInformation) {
        Span companyName = new Span(userInformation.companyName);
        Span userName = new Span(userInformation.userName + " " + userInformation.userLastname);
        Span countdown = new Span();
        countdown.getStyle().set("font-weight", "bold");
        countdown.getStyle().set("margin-left", "1rem");

        Div container = new Div(companyName, userName, countdown);
        container.getStyle().set("display", "flex");
        container.getStyle().set("align-items", "center");
        container.getStyle().set("gap", "1rem");

        long expiryEpoch = userInformation.expiryEpoch;
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
                            countdown.setText(getTranslation("loggedUserInfo.tokenExpired"));
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
                        return;
                    }

                    String timeFormatted = formatDuration(secondsLeft);
                    ui.access(() ->
                            countdown.setText(getTranslation("loggedUserInfo.tokenCountdownPrefix") + timeFormatted));
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
