package pl.crewops.view.component;

import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtInfoService;

@SpringComponent
public class LoggedUserInfoComponent extends HorizontalLayout {

    private final CoreAPI coreAPI;
    private final JwtInfoService jwtInfoService;

    public LoggedUserInfoComponent(CoreAPI coreAPI, JwtInfoService jwtInfoService) {
        addClassName("logged-user-info");

        this.coreAPI = coreAPI;
        this.jwtInfoService = jwtInfoService;

        if (jwtInfoService.validToken(coreAPI)) {
            add(getLoggedUserInfo());
        }
    }

    private MessageList getLoggedUserInfo() {
        MessageList info = new MessageList();
        MessageListItem item = new MessageListItem(
                "Logged user info",
                jwtInfoService.getExpires().toInstant(),
                jwtInfoService.getFirstName() + " " + jwtInfoService.getLastName());
        info.setItems(item);
        return info;
    }
}
