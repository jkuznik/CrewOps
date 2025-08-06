package pl.crewops.view.layout;

import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import pl.crewops.component.form.LoginForm;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtServiceVaadin;
import pl.crewops.util.RoleResolver;
import pl.crewops.view.layout.navbarComponents.LanguageSelectorComponent;
import pl.crewops.view.layout.navbarComponents.LoggedUserInfoComponent;

@SpringComponent
@UIScope
@CssImport("./styles/mainStyles/main-navbar.css")
public class MainNavbar extends HorizontalLayout {

    private final CoreAPI coreAPI;
    private final JwtServiceVaadin jwtService;

    public MainNavbar(CoreAPI coreAPI, JwtServiceVaadin jwtService, RoleResolver roleResolver) {
        addClassName("main-navbar");

        this.coreAPI = coreAPI;
        this.jwtService = jwtService;

        setSizeFull();
        setSpacing(true);
        setPadding(true);
        HorizontalLayout navbarRightSide = createNavbarRightSide(roleResolver);
        add(createNavbarLeftSide(), navbarRightSide);
        setFlexGrow(1, createNavbarLeftSide());
        setFlexGrow(1, navbarRightSide);
    }

    private HorizontalLayout createNavbarRightSide(RoleResolver roleResolver) {
        var rightSide = new HorizontalLayout();
        rightSide.setWidthFull();
        rightSide.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        rightSide.getStyle().set("padding-right", "20px");

        if (roleResolver.getPrincipal() != null
                && jwtService.validToken(roleResolver.getPrincipal().getToken())) {
            rightSide.add(new LoggedUserInfoComponent(coreAPI, jwtService, roleResolver));
        } else {
            rightSide.add(new LoginForm(coreAPI, jwtService));
        }
        rightSide.add(new LanguageSelectorComponent());

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
