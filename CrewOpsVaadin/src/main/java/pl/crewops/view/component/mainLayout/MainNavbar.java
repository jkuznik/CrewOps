package pl.crewops.view.component.mainLayout;

import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtInfoService;
import pl.crewops.view.component.LoggedUserInfoComponent;
import pl.crewops.view.component.form.LoginForm;

@SpringComponent
@CssImport("./styles/mainStyles/main-navbar.css")
public class MainNavbar extends HorizontalLayout {

    private final CoreAPI coreAPI;
    private final JwtInfoService jwtInfoService;

    private boolean tokenValid;

    public MainNavbar(CoreAPI coreAPI, JwtInfoService jwtInfoService) {
        addClassName("main-navbar");

        this.coreAPI = coreAPI;
        this.jwtInfoService = jwtInfoService;
        tokenValid = jwtInfoService.validToken();

        setSizeFull();
        setSpacing(true);
        setPadding(true);
        add(createNavbarLeftSide(), createNavbarRightSide());
        setFlexGrow(1, createNavbarLeftSide());
        setFlexGrow(1, createNavbarRightSide());
    }

    private HorizontalLayout createNavbarRightSide() {
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

    private static HorizontalLayout createNavbarLeftSide() {
        var leftSide = new HorizontalLayout();
        H1 title = new H1("CrewOps");
        title.addClassName("main-navbar-title");
        leftSide.setWidthFull();
        leftSide.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        leftSide.add(new DrawerToggle(), title);
        return leftSide;
    }
}
