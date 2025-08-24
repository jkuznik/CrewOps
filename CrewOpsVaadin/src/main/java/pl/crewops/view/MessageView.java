package pl.crewops.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import pl.crewops.component.grid.MessageGrid;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtServiceVaadin;
import pl.crewops.util.AuthenticationResolver;
import pl.crewops.view.layout.MainLayout;

@Route(value = "messages")
@PageTitle("CrewOps messages")
public class MessageView extends MainLayout implements BeforeEnterObserver {
    private MessageGrid messageGrid;

    public MessageView(CoreAPI coreAPI, JwtServiceVaadin jwtService, AuthenticationResolver authenticationResolver) {
        super(coreAPI, jwtService, authenticationResolver);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticationResolver.principalIsAuthenticated()) {
            buildContent();
        } else {
            event.forwardTo(HomeView.class);
            UI.getCurrent().getPage().setLocation("/");
        }
    }

    protected void buildContent() {
        mainContent.removeAll();

        messageGrid = new MessageGrid(coreAPI, authenticationResolver);
        messageGrid.setSizeFull();

        mainContent.add(messageGrid);
    }
}
