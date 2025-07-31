package pl.crewops.view.layout;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtServiceVaadin;

@SpringComponent
@UIScope
@CssImport("./styles/mainStyles/main-layout.css")
public class MainLayout extends AppLayout {

    // TODO: find solution to avoid displaying exceptions stackTrace on fe side

    protected final CoreAPI coreAPI;
    protected final JwtServiceVaadin jwtService;

    protected final VerticalLayout mainContent = new VerticalLayout();
    protected final Footer mainFooter = new MainFooter();

    public MainLayout(CoreAPI coreAPI, JwtServiceVaadin jwtService) {
        addClassName("main-layout");

        this.coreAPI = coreAPI;
        this.jwtService = jwtService;

        mainContent.setSizeFull();
        mainContent.setSpacing(true);
        mainContent.setPadding(true);
        mainContent.setVisible(true);
        setContent(mainContent);

        addToNavbar(new MainNavbar(coreAPI, jwtService));
        addToDrawer(new MainDrawer(jwtService));
    }
}
