package pl.crewops.component.form;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
import pl.crewops.component.navbarComponents.LanguageSelectorComponent;
import pl.crewops.component.notification.FailNotification;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.dto.auth.AuthRequest;
import pl.crewops.model.dto.auth.AuthResponse;
import pl.crewops.security.custom.UserPrincipal;
import pl.crewops.security.jwt.JwtServiceVaadin;

@SpringComponent
@CssImport("./styles/component/login-form.css")
@Slf4j
public class LoginForm extends FormLayout {

    private final TextField username = new TextField();
    private final PasswordField password = new PasswordField();
    private final Button login = new Button();

    public LoginForm(CoreAPI coreAPI, JwtServiceVaadin jwtService) {
        addClassName("login-form");

        username.addClassName("login-input");
        password.addClassName("login-input");
        login.addClassName("login-button");
        login.addClickShortcut(Key.ENTER);

        add(createLoginForm(coreAPI, jwtService));
    }

    private void localize() {
        username.setLabel(getTranslation("loginForm.username"));
        password.setLabel(getTranslation("loginForm.password"));
        login.setText(getTranslation("loginForm.login"));
    }

    private Component createLoginForm(CoreAPI coreAPI, JwtServiceVaadin jwtService) {
        var layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.addClassName("login-form-layout");

        var buttonAndLanguageSelector = new HorizontalLayout();
        buttonAndLanguageSelector.setSpacing(true);
        buttonAndLanguageSelector.add(login, new LanguageSelectorComponent());

        configureLoginButton(coreAPI, jwtService);
        layout.add(username, password, buttonAndLanguageSelector);
        layout.setAlignSelf(FlexComponent.Alignment.END, login);
        return layout;
    }

    private void configureLoginButton(CoreAPI coreAPI, JwtServiceVaadin jwtService) {
        login.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        login.addClickListener(event -> loginAction(coreAPI, jwtService));
    }

    private void loginAction(CoreAPI coreAPI, JwtServiceVaadin jwtService) {
        var authRequest = new AuthRequest(username.getValue(), password.getValue());

        try {
            log.info("Try login");

            AuthResponse authResponse = coreAPI.login(authRequest).orElseThrow(NotAuthenticatedException::new);

            String token = authResponse.token();

            UUID companyId = jwtService.extractCompanyId(token);
            UUID employeeId = jwtService.extractEmployeeId(token);
            var authorities = jwtService.extractAuthorities(token);

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
