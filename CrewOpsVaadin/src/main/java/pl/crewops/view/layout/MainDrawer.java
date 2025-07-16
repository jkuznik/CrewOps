package pl.crewops.view.layout;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.crewops.security.custom.UserPrincipal;
import pl.crewops.security.jwt.JwtServiceVaadin;
import pl.crewops.view.EmployeeView;
import pl.crewops.view.HomeView;
import pl.crewops.view.VehicleView;

@SpringComponent
@UIScope
@CssImport("./styles/mainStyles/main-drawer.css")
public class MainDrawer extends VerticalLayout {

    private final JwtServiceVaadin jwtService;
    private UserPrincipal principal;

    private final RouterLink homeLink = new RouterLink(HomeView.class);
    private final RouterLink employeeLink = new RouterLink(EmployeeView.class);
    private final RouterLink vehicleLink = new RouterLink(VehicleView.class);

    private final Span footerText = new Span();

    public MainDrawer(JwtServiceVaadin jwtService) {
        addClassName("main-drawer");

        this.jwtService = jwtService;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        localize();

        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {

            this.principal = userPrincipal;
        }

        setSizeFull();
        setSpacing(true);
        setPadding(true);

        VerticalLayout linksLayout = new VerticalLayout();
        linksLayout.setSizeFull();
        linksLayout.setPadding(true);
        linksLayout.setSpacing(true);
        linksLayout.add(homeLink, employeeLink, vehicleLink);

        add(linksLayout, createDrawerFooter());
        setFlexGrow(1, linksLayout);

        checkDrawer(employeeLink, vehicleLink);
    }

    private void localize() {
        homeLink.setText(getTranslation("mainDrawer.link.home"));
        employeeLink.setText(getTranslation("mainDrawer.link.employee"));
        vehicleLink.setText(getTranslation("mainDrawer.link.vehicle"));

        footerText.setText(getTranslation("mainDrawer.footer.text"));
    }

    private Footer createDrawerFooter() {
        Footer footer = new Footer();
        footer.addClassName("drawer-footer");

        footerText.addClassName("drawer-footer-text");

        footer.add(footerText);

        return footer;
    }

    private void checkDrawer(RouterLink employeeLink, RouterLink vehicleLink) {
        if (principal == null || !jwtService.validToken(principal.getToken())) {
            employeeLink.setVisible(false);
            vehicleLink.setVisible(false);
        } else {
            employeeLink.setVisible(true);
            vehicleLink.setVisible(true);
        }
    }
}
