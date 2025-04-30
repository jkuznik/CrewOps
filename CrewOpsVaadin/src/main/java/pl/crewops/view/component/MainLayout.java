package pl.crewops.view.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.context.annotation.Scope;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtInfoService;
import pl.crewops.view.EmployeeView;
import pl.crewops.view.HomeView;
import pl.crewops.view.QualificationView;
import pl.crewops.view.VehicleView;
import pl.crewops.view.form.LoginForm;

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

        addToNavbar(createHeader());
        addToDrawer(createDrawer());
    }

    private Component createHeader() {
        var header = new HorizontalLayout();
        var leftSide = createHeaderLeftSide();

        var rightSide = createHeaderRightSide();

        header.setWidthFull();
        header.add(leftSide, rightSide);
        header.setFlexGrow(1, leftSide);
        header.setFlexGrow(1, rightSide);

        return header;
    }

    private HorizontalLayout createHeaderRightSide() {
        var rightSide = new HorizontalLayout();
        rightSide.setWidthFull();
        rightSide.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        rightSide.getStyle().set("padding-right", "20px");

        if (tokenValid) {
            LoggedUserInfoComponent loggedUserInfoComponent = new LoggedUserInfoComponent(coreAPI, jwtInfoService);
            rightSide.add(loggedUserInfoComponent);
        } else {
            LoginForm loginForm = new LoginForm(coreAPI, jwtInfoService);
            rightSide.add(loginForm);
        }

        return rightSide;
    }

    private static HorizontalLayout createHeaderLeftSide() {
        var leftSide = new HorizontalLayout();
        H1 title = new H1("CrewOps");
        leftSide.setWidthFull();
        leftSide.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        leftSide.add(new DrawerToggle(), title);
        return leftSide;
    }

    private Component createDrawer() {
        VerticalLayout drawerLayout = new VerticalLayout();

        RouterLink homeLink = new RouterLink("Home", HomeView.class);
        RouterLink employeeLink = new RouterLink("Employee", EmployeeView.class);
        RouterLink qualificationLink = new RouterLink("Qualification", QualificationView.class);
        RouterLink vehicleLink = new RouterLink("Vehicle", VehicleView.class);

        drawerLayout.setHeightFull();

        VerticalLayout linksLayout = new VerticalLayout();
        linksLayout.setPadding(false);
        linksLayout.setSpacing(false);
        linksLayout.add(homeLink, employeeLink, qualificationLink, vehicleLink);

        drawerLayout.add(linksLayout, createDrawerFooter());
        drawerLayout.setFlexGrow(1, linksLayout);

        checkDrawer(employeeLink, qualificationLink, vehicleLink);

        return drawerLayout;
    }

    private Footer createDrawerFooter() {
        Footer footer = new Footer();
        footer.getStyle()
                .set("width", "100%")
                .set("text-align", "center")
                .set("padding", "10px")
                .set("background-color", "#f1f1f1");

        Span footerText = new Span("© 2025 CrewOps - by Janusz Kuźnik.");
        footerText
                .getStyle()
                .set("font-size", "12px")
                .set("color", "#888")
                .set("margin-top", "auto")
                .set("text-align", "center");

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
