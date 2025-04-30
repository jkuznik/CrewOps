package pl.crewops.view.component.mainLayout;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.context.annotation.Scope;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtInfoService;
import pl.crewops.view.HomeView;

@SpringComponent
@Scope("prototype")
public class MainLayout extends AppLayout {

    protected final CoreAPI coreAPI;
    protected final JwtInfoService jwtInfoService;

    protected final VerticalLayout mainContent;
    protected final Footer mainFooter;

    private boolean tokenValid;

    public MainLayout(CoreAPI coreAPI, JwtInfoService jwtInfoService, VerticalLayout mainView, Footer homeViewFooter) {
        addClassName("main-layout");
        tokenValid = jwtInfoService.validToken();
        if (!tokenValid) {
            var currentView = UI.getCurrent().getInternals().getActiveViewLocation();
            if (currentView != null && !currentView.getPath().equals("")) {
                UI.getCurrent().access(() -> UI.getCurrent().navigate(HomeView.class));
            }
        }

        this.coreAPI = coreAPI;
        this.jwtInfoService = jwtInfoService;
        this.mainContent = mainView;
        this.mainFooter = homeViewFooter;

        mainContent.setSizeFull();
        mainContent.setSpacing(true);
        mainContent.setPadding(true);
        mainContent.setVisible(true);
        setContent(mainContent);

        addToNavbar(new MainHeader(coreAPI, jwtInfoService));
        addToDrawer(new MainDrawer(coreAPI, jwtInfoService));
    }
}
