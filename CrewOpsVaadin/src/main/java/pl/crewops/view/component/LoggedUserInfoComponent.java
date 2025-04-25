package pl.crewops.view.component;

import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.time.Instant;
import pl.crewops.infrastructure.core.CoreAPI;

@SpringComponent
public class LoggedUserInfoComponent extends HorizontalLayout {

    private final CoreAPI coreAPI;

    public LoggedUserInfoComponent(CoreAPI coreAPI) {
        addClassName("logged-user-info");

        this.coreAPI = coreAPI;

        add(getLoggedUserInfo());
    }

    private MessageList getLoggedUserInfo() {
        //        coreAPI.getTokenInfo();
        MessageList info = new MessageList();
        MessageListItem item = new MessageListItem("Logged user info", Instant.now(), "Jan Kuz");
        info.setItems(item);
        return info;
    }
}
