package pl.crewops.view.component.form;

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
import pl.crewops.auth.AuthRequest;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.custom.UserPrincipal;
import pl.crewops.security.jwt.JwtService;
import pl.crewops.view.component.notification.LoginFailNotification;

@SpringComponent
@CssImport("./styles/component/login-form.css")
@Slf4j
public class LoginForm extends FormLayout {

    private final TextField username = new TextField();
    private final PasswordField password = new PasswordField();
    private final Button login = new Button();

    public LoginForm(CoreAPI coreAPI, JwtService jwtService) {
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

    private Component createLoginForm(CoreAPI coreAPI, JwtService jwtService) {
        var layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.addClassName("login-form-layout");

        configureLoginButton(coreAPI, jwtService);
        layout.add(username, password, login);
        layout.setAlignSelf(FlexComponent.Alignment.END, login);
        return layout;
    }

    private void configureLoginButton(CoreAPI coreAPI, JwtService jwtService) {
        login.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        login.addClickListener(event -> loginAction(coreAPI, jwtService));
    }

    private void loginAction(CoreAPI coreAPI, JwtService jwtService) {
        var authRequest = new AuthRequest(username.getValue(), password.getValue());

        try {
            log.info("Try login");
            String token = coreAPI.login(authRequest).token();
            log.info("Successfully logged in, token: {}", token);

            var username = jwtService.getUsername(token);
            var firstName = jwtService.getFirstName(token);
            var lastName = jwtService.getLastName(token);
            UUID companyId = jwtService.getTenantCompanyId(token);
            UUID employeeId = jwtService.getEmployeeId(token);
            var authorities = jwtService.getAuthorities(token);

            var userPrincipal = new UserPrincipal(username, firstName, lastName, companyId, authorities);
            userPrincipal.setToken(token);
            userPrincipal.setEmployeeId(employeeId);

            Authentication auth = new UsernamePasswordAuthenticationToken(userPrincipal, null, authorities);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);

            coreAPI.setAuthentication(true);
            coreAPI.setToken(token);

            HttpServletRequest request =
                    ((VaadinServletRequest) VaadinService.getCurrentRequest()).getHttpServletRequest();
            HttpServletResponse response =
                    ((VaadinServletResponse) VaadinService.getCurrentResponse()).getHttpServletResponse();

            SecurityContextRepository repo = new HttpSessionSecurityContextRepository();
            repo.saveContext(context, request, response);

            UI.getCurrent().getPage().reload();

        } catch (RestClientException e) {
            log.info("Login failed: {}", e.getMessage());
            new LoginFailNotification();
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        localize();
    }
}
