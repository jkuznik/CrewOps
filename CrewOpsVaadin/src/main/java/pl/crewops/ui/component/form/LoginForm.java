package pl.crewops.ui.component.form;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinServletResponse;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.client.RestClientException;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.dto.auth.AuthRequest;
import pl.crewops.model.dto.auth.AuthResponse;
import pl.crewops.security.custom.UserPrincipal;
import pl.crewops.ui.component.navbarComponents.LanguageSelectorComponent;
import pl.crewops.ui.component.notification.FailNotification;
import pl.crewops.util.AuthenticationResolver;

@SpringComponent
@Slf4j
public class LoginForm extends FormLayout {

    private final CoreAPI coreAPI;
    private final AuthenticationResolver authenticationResolver;

    private final TextField username = new TextField();
    private final PasswordField password = new PasswordField();
    private final Button login = new Button();
    private final LanguageSelectorComponent languageSelectorComponent = new LanguageSelectorComponent();

    public LoginForm(CoreAPI coreAPI, AuthenticationResolver authenticationResolver) {
        this.coreAPI = coreAPI;
        this.authenticationResolver = authenticationResolver;
        addClassName("login-form");

        username.addClassName("login-input");
        password.addClassName("login-input");
        login.addClassName("login-button");
        login.addClickShortcut(Key.ENTER);

        add(createLoginForm(coreAPI));
    }

    private void localize() {
        username.setLabel(getTranslation("loginForm.username"));
        password.setLabel(getTranslation("loginForm.password"));
        login.setText(getTranslation("loginForm.login"));
    }

    private Component createLoginForm(CoreAPI coreAPI) {
        var layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.setMargin(false);
        layout.addClassName("login-form-layout");

        configureLoginButton();
        layout.add(username, password, login, languageSelectorComponent);
        layout.setAlignItems(FlexComponent.Alignment.END);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        return layout;
    }

    private void configureLoginButton() {
        login.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        login.addClickListener(event -> loginAction());
    }

    private void loginAction() {
        var authRequest = new AuthRequest(username.getValue(), password.getValue());

        try {
            log.info("Try login");

            AuthResponse authResponse = coreAPI.login(authRequest).orElseThrow(NotAuthenticatedException::new);

            String token = authResponse.token();

            UUID companyId = authenticationResolver.extractCompanyIdFromToken(token);
            UUID employeeId = authenticationResolver.extractEmployeeIdFromToken(token);
            var authorities = authenticationResolver.extractAuthoritiesFromToken(token);

            var userPrincipal = new UserPrincipal(companyId, username.getValue(), authorities);
            userPrincipal.setToken(token);
            userPrincipal.setEmployeeId(employeeId);

            Authentication auth = new UsernamePasswordAuthenticationToken(userPrincipal, null, authorities);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);

            HttpServletRequest request =
                    ((VaadinServletRequest) VaadinService.getCurrentRequest()).getHttpServletRequest();
            HttpServletResponse response =
                    ((VaadinServletResponse) VaadinService.getCurrentResponse()).getHttpServletResponse();

            SecurityContextRepository repo = new HttpSessionSecurityContextRepository();
            repo.saveContext(context, request, response);

            log.info("Successfully logged in, token: {}", token);
            UI.getCurrent().getPage().reload();
        } catch (NotAuthenticatedException | RestClientException e) {
            log.error("Login failed: {}", e.getMessage());
            new FailNotification(getTranslation("loginFailedNotification.message"));
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        localize();
    }
}
