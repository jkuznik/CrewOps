package pl.crewops.view.component.form;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import pl.crewops.auth.AuthRequest;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.custom.UserPrincipal;
import pl.crewops.security.jwt.JwtService;

@SpringComponent
@CssImport("./styles/component/login-form.css")
@Slf4j
public class LoginForm extends FormLayout {
    TextField username = new TextField("Username");
    PasswordField password = new PasswordField("Password");
    Button login = new Button("Login");

    public LoginForm(CoreAPI coreAPI, JwtService jwtService) {
        addClassName("login-form");

        username.addClassName("login-input");
        password.addClassName("login-input");
        login.addClassName("login-button");
        login.addClickShortcut(Key.ENTER);

        add(createLoginForm(coreAPI, jwtService));
    }

    private Component createLoginForm(CoreAPI coreAPI, JwtService jwtService) {
        var horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        horizontalLayout.addClassName("login-form-layout");

        configureLoginButton(coreAPI, jwtService);
        horizontalLayout.add(username, password, login);
        horizontalLayout.setAlignSelf(FlexComponent.Alignment.END, login);
        return horizontalLayout;
    }

    private void configureLoginButton(CoreAPI coreAPI, JwtService jwtService) {
        login.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        login.addClickListener(event -> loginAction(coreAPI, jwtService));
    }

    private void loginAction(CoreAPI coreAPI, JwtService jwtService) {
        var authRequest = new AuthRequest(username.getValue(), password.getValue());

        String token = null;
        try {
            log.info("Try login");
            token = coreAPI.login(authRequest).token();
            log.info("Successfully logged in, token: {}", token);
        } catch (Exception e) {
            log.info("Login failed, {}", e.getMessage());
            // TODO: implement logic in case of auth fail
        }

        if (token != null) {
            var username = jwtService.getUsername(token);
            var firstName = jwtService.getFirstName(token);
            var lastName = jwtService.getLastName(token);
            var grantedAuthorities = jwtService.getAuthorities(token);

            UserPrincipal userPrincipal = new UserPrincipal(username, firstName, lastName, grantedAuthorities);
            userPrincipal.setToken(token);

            Authentication auth =
                    new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());

            // Utwórz SecurityContext
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);

            coreAPI.setAuthentication(true);
            coreAPI.setToken(token);

            // Trwale zapisz SecurityContext do sesji HTTP
            HttpServletRequest request =
                    ((VaadinServletRequest) VaadinService.getCurrentRequest()).getHttpServletRequest();
            HttpServletResponse response =
                    ((VaadinServletResponse) VaadinService.getCurrentResponse()).getHttpServletResponse();

            SecurityContextRepository repo = new HttpSessionSecurityContextRepository();
            repo.saveContext(context, request, response);
        }

        log.info("Login action success");
        UI.getCurrent().getPage().reload();
    }
}
