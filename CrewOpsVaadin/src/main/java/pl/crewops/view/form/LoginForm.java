package pl.crewops.view.form;

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
import com.vaadin.flow.spring.annotation.SpringComponent;
import lombok.extern.slf4j.Slf4j;
import pl.crewops.auth.AuthRequest;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtInfoService;

@SpringComponent
@CssImport("./styles/component/login-form.css")
@Slf4j
public class LoginForm extends FormLayout {
    TextField username = new TextField("Username");
    PasswordField password = new PasswordField("Password");
    Button login = new Button("Login");

    public LoginForm(CoreAPI coreAPI, JwtInfoService jwtInfoService) {
        addClassName("login-form");

        username.addClassName("login-input");
        password.addClassName("login-input");
        login.addClassName("login-button");
        login.addClickShortcut(Key.ENTER);

        add(createLoginForm(coreAPI, jwtInfoService));
    }

    private Component createLoginForm(CoreAPI coreAPI, JwtInfoService jwtInfoService) {
        var horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        horizontalLayout.addClassName("login-form-layout");

        configureLoginButton(coreAPI, jwtInfoService);
        horizontalLayout.add(username, password, login);
        horizontalLayout.setAlignSelf(FlexComponent.Alignment.END, login);
        return horizontalLayout;
    }

    private void configureLoginButton(CoreAPI coreAPI, JwtInfoService jwtInfoService) {
        login.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        login.addClickListener(event -> loginAction(coreAPI, jwtInfoService));
    }

    private void loginAction(CoreAPI coreAPI, JwtInfoService jwtInfoService) {
        var authRequest = new AuthRequest(username.getValue(), password.getValue());
        jwtInfoService.setAuthentication(coreAPI.login(authRequest));
        log.debug("Login action success: ");
        UI.getCurrent().getPage().reload();
    }
}
