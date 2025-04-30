package pl.crewops.view.form;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import lombok.extern.slf4j.Slf4j;
import pl.crewops.auth.AuthRequest;
import pl.crewops.auth.AuthResponse;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtInfoService;

@SpringComponent
@Slf4j
public class LoginForm extends FormLayout {

    private final CoreAPI coreAPI;
    private final JwtInfoService jwtInfoService;

    TextField username = new TextField("Username");
    PasswordField password = new PasswordField("Password");
    Button login = new Button("Login");

    public LoginForm(CoreAPI coreAPI, JwtInfoService jwtInfoService) {
        addClassName("login-form");

        this.coreAPI = coreAPI;
        this.jwtInfoService = jwtInfoService;
        add(createLoginForm());
    }

    private Component createLoginForm() {
        var horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        configureLoginButton();
        horizontalLayout.add(username, password, login);
        horizontalLayout.setAlignSelf(FlexComponent.Alignment.END, login);
        return horizontalLayout;
    }

    private void configureLoginButton() {
        login.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        login.addClickListener(event -> {
            loginAction();
        });
    }

    private void loginAction() {
        var authRequest = new AuthRequest(username.getValue(), password.getValue());
        AuthResponse authResponse = coreAPI.login(authRequest);
        jwtInfoService.setToken(authResponse.token());
        // todo: zmienic ta logike na taka aby coreAPI każdorazowo sprawdzała ważność tokena
        coreAPI.setToken(authResponse);

        log.info("Auth response: " + authResponse);
        UI.getCurrent().getPage().reload();
    }
}
