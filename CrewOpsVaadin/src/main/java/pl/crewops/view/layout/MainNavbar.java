package pl.crewops.view.layout;

import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.custom.UserPrincipal;
import pl.crewops.security.jwt.JwtService;
import pl.crewops.view.component.form.LoginForm;
import pl.crewops.view.component.navbarComponents.LoggedUserInfoComponent;

@SpringComponent
@UIScope
@CssImport("./styles/mainStyles/main-navbar.css")
public class MainNavbar extends HorizontalLayout {

    private final CoreAPI coreAPI;
    private final JwtService jwtService;
    private UserPrincipal principal;

    public MainNavbar(CoreAPI coreAPI, JwtService jwtService) {
        addClassName("main-navbar");

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
        add(createNavbarLeftSide(), createNavbarRightSide());
        setFlexGrow(1, createNavbarLeftSide());
        setFlexGrow(1, createNavbarRightSide());
    }

    private HorizontalLayout createNavbarRightSide() {
        var rightSide = new HorizontalLayout();
        rightSide.setWidthFull();
        rightSide.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        rightSide.getStyle().set("padding-right", "20px");

        if (principal == null || jwtService.validToken(principal.getToken())) {
            var loggedUserInfoComponent = new LoggedUserInfoComponent(coreAPI, jwtService);
            rightSide.add(loggedUserInfoComponent);
        } else {
            LoginForm loginForm = new LoginForm(coreAPI, jwtService);
            rightSide.add(loginForm);
        }

        return rightSide;
    }

    private static HorizontalLayout createNavbarLeftSide() {
        var leftSide = new HorizontalLayout();
        H1 title = new H1();
        title.setText(title.getTranslation("mainNavbar.title"));
        title.addClassName("main-navbar-title");
        leftSide.setWidthFull();
        leftSide.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        leftSide.add(new DrawerToggle(), title);
        return leftSide;
    }
}
