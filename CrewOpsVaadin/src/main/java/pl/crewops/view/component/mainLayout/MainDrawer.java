package pl.crewops.view.component.mainLayout;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.custom.UserPrincipal;
import pl.crewops.security.jwt.JwtService;
import pl.crewops.view.EmployeeView;
import pl.crewops.view.HomeView;
import pl.crewops.view.VehicleView;

@SpringComponent
@UIScope
@CssImport("./styles/mainStyles/main-drawer.css")
public class MainDrawer extends VerticalLayout {

    private final CoreAPI coreAPI;
    private final JwtService jwtService;
    private UserPrincipal principal;

    public MainDrawer(CoreAPI coreAPI, JwtService jwtService) {
        addClassName("main-drawer");

        this.coreAPI = coreAPI;
        this.jwtService = jwtService;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserPrincipal userPrincipal
                && jwtService.validToken(userPrincipal.getToken())) {

            this.principal = userPrincipal;
        }

        setSizeFull();
        setSpacing(true);
        setPadding(true);

        RouterLink homeLink = new RouterLink("Home", HomeView.class);
        RouterLink employeeLink = new RouterLink("Employee", EmployeeView.class);
        // TODO: delete this if new solution works well
        //        RouterLink qualificationLink = new RouterLink("Qualification", QualificationView.class);
        RouterLink vehicleLink = new RouterLink("Vehicle", VehicleView.class);

        VerticalLayout linksLayout = new VerticalLayout();
        linksLayout.setSizeFull();
        linksLayout.setPadding(true);
        linksLayout.setSpacing(true);
        //        linksLayout.add(homeLink, employeeLink, qualificationLink, vehicleLink);
        linksLayout.add(homeLink, employeeLink, vehicleLink);

        add(linksLayout, createDrawerFooter());
        setFlexGrow(1, linksLayout);

        //        checkDrawer(employeeLink, qualificationLink, vehicleLink);
        checkDrawer(employeeLink, vehicleLink);
    }

    private Footer createDrawerFooter() {
        Footer footer = new Footer();
        footer.addClassName("drawer-footer");

        Span footerText = new Span("© 2025 CrewOps");
        footerText.addClassName("drawer-footer-text");

        footer.add(footerText);

        return footer;
    }

    //    private void checkDrawer(RouterLink employeeLink, RouterLink qualificationLink, RouterLink vehicleLink) {
    private void checkDrawer(RouterLink employeeLink, RouterLink vehicleLink) {
        if (principal == null || !jwtService.validToken(principal.getToken())) {
            employeeLink.setVisible(false);
            //            qualificationLink.setVisible(true);
            vehicleLink.setVisible(false);
        } else {
            employeeLink.setVisible(true);
            //            qualificationLink.setVisible(false);
            vehicleLink.setVisible(true);
        }
    }
}
