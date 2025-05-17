package pl.crewops.view.component.mainLayout;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.context.annotation.Scope;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtService;

@SpringComponent
@Scope("prototype")
@CssImport("./styles/mainStyles/main-layout.css")
public class MainLayout extends AppLayout {

    protected final CoreAPI coreAPI;
    protected final JwtService jwtService;

    protected final VerticalLayout mainContent = new VerticalLayout();
    protected final Footer mainFooter = new MainFooter();

    public MainLayout(CoreAPI coreAPI, JwtService jwtService) {
        addClassName("main-layout");

        this.coreAPI = coreAPI;
        this.jwtService = jwtService;

        mainContent.setSizeFull();
        mainContent.setSpacing(true);
        mainContent.setPadding(true);
        mainContent.setVisible(true);
        setContent(mainContent);

        addToNavbar(new MainNavbar(coreAPI, jwtService));
        addToDrawer(new MainDrawer(coreAPI, jwtService));
    }
}
