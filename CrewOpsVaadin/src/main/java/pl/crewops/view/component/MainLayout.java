package pl.crewops.view.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
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

    private final CoreAPI coreAPI;
    private final JwtInfoService jwtInfoService;

    public MainLayout(CoreAPI coreAPI, JwtInfoService jwtInfoService) {
        addClassName("home-view");

        this.coreAPI = coreAPI;
        this.jwtInfoService = jwtInfoService;
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

        if (jwtInfoService.validToken(coreAPI)) {
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
        RouterLink homeLink = new RouterLink("Home", HomeView.class);
        RouterLink employeeLink = new RouterLink("Employee", EmployeeView.class);
        RouterLink qualificationLink = new RouterLink("Qualification", QualificationView.class);
        RouterLink vehicleLink = new RouterLink("Vehicle", VehicleView.class);

        employeeLink.setHighlightCondition(HighlightConditions.sameLocation());

        return new VerticalLayout(homeLink, employeeLink, qualificationLink, vehicleLink);
    }
}
