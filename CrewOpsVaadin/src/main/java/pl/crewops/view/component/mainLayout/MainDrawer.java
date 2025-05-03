package pl.crewops.view.component.mainLayout;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtInfoService;
import pl.crewops.view.EmployeeView;
import pl.crewops.view.HomeView;
import pl.crewops.view.QualificationView;
import pl.crewops.view.VehicleView;

@SpringComponent
@UIScope
@CssImport("./styles/mainStyles/main-drawer.css")
public class MainDrawer extends VerticalLayout {

    private final CoreAPI coreAPI;
    private final JwtInfoService jwtInfoService;

    private boolean tokenValid;

    public MainDrawer(CoreAPI coreAPI, JwtInfoService jwtInfoService) {
        addClassName("main-drawer");

        this.coreAPI = coreAPI;
        this.jwtInfoService = jwtInfoService;

        tokenValid = jwtInfoService.validToken();

        setSizeFull();
        setSpacing(true);
        setPadding(true);

        RouterLink homeLink = new RouterLink("Home", HomeView.class);
        RouterLink employeeLink = new RouterLink("Employee", EmployeeView.class);
        RouterLink qualificationLink = new RouterLink("Qualification", QualificationView.class);
        RouterLink vehicleLink = new RouterLink("Vehicle", VehicleView.class);

        VerticalLayout linksLayout = new VerticalLayout();
        linksLayout.setSizeFull();
        linksLayout.setPadding(true);
        linksLayout.setSpacing(true);
        linksLayout.add(homeLink, employeeLink, qualificationLink, vehicleLink);

        add(linksLayout, createDrawerFooter());
        setFlexGrow(1, linksLayout);

        checkDrawer(employeeLink, qualificationLink, vehicleLink);
    }

    private Footer createDrawerFooter() {
        Footer footer = new Footer();
        footer.addClassName("drawer-footer");

        Span footerText = new Span("© 2025 CrewOps");
        footerText.addClassName("drawer-footer-text");

        footer.add(footerText);

        return footer;
    }

    private void checkDrawer(RouterLink employeeLink, RouterLink qualificationLink, RouterLink vehicleLink) {
        if (tokenValid) {
            employeeLink.setVisible(true);
            qualificationLink.setVisible(true);
            vehicleLink.setVisible(true);
        } else {
            employeeLink.setVisible(false);
            qualificationLink.setVisible(false);
            vehicleLink.setVisible(false);
        }
    }
}
