package pl.crewops.view.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtInfoService;
import pl.crewops.view.HomeView;
import pl.crewops.view.form.LoginForm;

@SpringComponent
public class LoggedUserInfoComponent extends HorizontalLayout {

    public LoggedUserInfoComponent(CoreAPI coreAPI, JwtInfoService jwtInfoService) {
        addClassName("logged-user-info");

        if (jwtInfoService.validToken()) {
            add(loggedUserInfo(jwtInfoService));
        } else {
            LoginForm loginForm = new LoginForm(coreAPI, jwtInfoService);
            add(loginForm);
        }
    }

    private Component loggedUserInfo(JwtInfoService jwtInfoService) {
        var infoLayout = new HorizontalLayout();
        infoLayout.setWidthFull();
        infoLayout.setSpacing(true);

        H1 title = new H1("You are logged as ");

        Button logoutButton = new Button("Logout");
        logoutButton.addClickListener(event -> logout(jwtInfoService));
        logoutButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        infoLayout.add(title, getInfo(jwtInfoService), logoutButton);

        return infoLayout;
    }

    private MessageList getInfo(JwtInfoService jwtInfoService) {
        MessageList info = new MessageList();
        MessageListItem item = new MessageListItem(
                "Session validation time",
                jwtInfoService.getExpires().toInstant(),
                jwtInfoService.getFirstName() + " " + jwtInfoService.getLastName());
        info.setItems(item);
        return info;
    }

    private void logout(JwtInfoService jwtInfoService) {
        jwtInfoService.resetAuthentication();
        UI.getCurrent().navigate(HomeView.class);
    }
}
