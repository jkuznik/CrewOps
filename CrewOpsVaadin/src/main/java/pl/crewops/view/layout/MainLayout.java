package pl.crewops.view.layout;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtServiceVaadin;
import pl.crewops.util.AuthenticationResolver;

@SpringComponent
@UIScope
@CssImport("./styles/mainStyles/main-layout.css")
public class MainLayout extends AppLayout {

    // TODO: find risky methods and handle exceptions to avoid displaying stackTrace on fe side

    protected final CoreAPI coreAPI;
    protected final JwtServiceVaadin jwtService;
    protected final AuthenticationResolver authenticationResolver;

    protected final VerticalLayout mainContent = new VerticalLayout();
    protected final Footer mainFooter = new MainFooter();
    protected final MainNavbar navbar;
    protected final MainDrawer drawer;

    public MainLayout(CoreAPI coreAPI, JwtServiceVaadin jwtService, AuthenticationResolver authenticationResolver) {
        addClassName("main-layout");

        this.coreAPI = coreAPI;
        this.jwtService = jwtService;
        this.authenticationResolver = authenticationResolver;
        this.navbar = new MainNavbar(coreAPI, jwtService, authenticationResolver);
        this.drawer = new MainDrawer(authenticationResolver);

        mainContent.setSizeFull();
        mainContent.setSpacing(true);
        mainContent.setPadding(true);
        mainContent.setVisible(true);
        // TODO: consider if token validation for view display component rules could be in this one place
        //  and left validation for each operation to BE responsibility
        setContent(mainContent);

        addToNavbar(navbar);
        addToDrawer(drawer);
    }
}
