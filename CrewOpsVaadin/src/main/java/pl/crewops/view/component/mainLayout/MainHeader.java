package pl.crewops.view.component.mainLayout;

import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtInfoService;
import pl.crewops.view.component.LoggedUserInfoComponent;
import pl.crewops.view.form.LoginForm;

@SpringComponent
public class MainHeader extends HorizontalLayout {

    private final CoreAPI coreAPI;
    private final JwtInfoService jwtInfoService;

    private boolean tokenValid;

    public MainHeader(CoreAPI coreAPI, JwtInfoService jwtInfoService) {
        addClassName("main-header");

        this.coreAPI = coreAPI;
        this.jwtInfoService = jwtInfoService;
        tokenValid = jwtInfoService.validToken();

        setSizeFull();
        setSpacing(true);
        setPadding(true);
        add(createHeaderLeftSide(), createHeaderRightSide());
        setFlexGrow(1, createHeaderLeftSide());
        setFlexGrow(1, createHeaderRightSide());
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
}
