package pl.crewops.ui.view.layout;

import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtServiceVaadin;
import pl.crewops.ui.component.form.LoginForm;
import pl.crewops.ui.component.navbarComponents.LoggedUserInfoComponent;
import pl.crewops.ui.component.notification.FailNotification;
import pl.crewops.util.AuthenticationResolver;

@SpringComponent
@UIScope
@CssImport("./styles/mainStyles/main-navbar.css")
public class MainNavbar extends HorizontalLayout {

    private final CoreAPI coreAPI;
    private final JwtServiceVaadin jwtService;
    private final AuthenticationResolver authenticationResolver;

    public MainNavbar(CoreAPI coreAPI, JwtServiceVaadin jwtService, AuthenticationResolver authenticationResolver) {
        this.coreAPI = coreAPI;
        this.jwtService = jwtService;
        this.authenticationResolver = authenticationResolver;

        addClassName("main-navbar");

        try {

            setSizeFull();
            setSpacing(true);
            setPadding(true);
            HorizontalLayout navbarRightSide = createNavbarRightSide();
            add(createNavbarLeftSide(), navbarRightSide);
            add(navbarRightSide);
        } catch (Exception e) {
            new FailNotification(getTranslation("dailyView.failNotification"));
        }
    }

    private HorizontalLayout createNavbarRightSide() {
        var rightSide = new HorizontalLayout();
        rightSide.setWidthFull();
        rightSide.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        rightSide.getStyle().set("padding-right", "20px");

        if (authenticationResolver.principalIsAuthenticated()) {
            rightSide.add(new LoggedUserInfoComponent(coreAPI, jwtService, authenticationResolver));
        } else {
            rightSide.add(new LoginForm(coreAPI, jwtService));
        }

        return rightSide;
    }

    private static HorizontalLayout createNavbarLeftSide() {
        var leftSide = new HorizontalLayout();
        leftSide.setWidthFull();
        leftSide.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        leftSide.add(new DrawerToggle());
        return leftSide;
    }
}
