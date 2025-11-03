package pl.crewops.ui.view.layout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtServiceVaadin;
import pl.crewops.ui.component.navbarComponents.LoggedUserInfoComponent;
import pl.crewops.ui.component.notification.FailNotification;
import pl.crewops.util.AuthenticationResolver;

@SpringComponent
@UIScope
@CssImport("./styles/mainStyles/main-navbar.css")
public class MainNavbar extends HorizontalLayout implements AfterNavigationObserver {

    private final CoreAPI coreAPI;
    private final JwtServiceVaadin jwtService;
    private final AuthenticationResolver authenticationResolver;

    private final Button drawerToggleButton = new Button();
    private final Icon menuIcon = VaadinIcon.MENU.create();
    private final Icon replyIcon = VaadinIcon.REPLY.create();

    public MainNavbar(CoreAPI coreAPI, JwtServiceVaadin jwtService, AuthenticationResolver authenticationResolver) {
        this.coreAPI = coreAPI;
        this.jwtService = jwtService;
        this.authenticationResolver = authenticationResolver;

        addClassName("main-navbar");

        try {
            setWidthFull();

            setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

            drawerToggleButton.setIcon(menuIcon);

            Component spacerOne = leftSideNavbar();
            HorizontalLayout internalSpacer = new HorizontalLayout();
            HorizontalLayout authComponentWrapper = createAuthComponentWrapper();

            setFlexGrow(0, spacerOne);
            setFlexGrow(1.0, internalSpacer);
            setFlexGrow(0, authComponentWrapper);

            add(spacerOne, internalSpacer, authComponentWrapper);

        } catch (Exception e) {
            new FailNotification(getTranslation("dailyView.failNotification"));
        }
    }

    /**
     * Nowa metoda: Spacer pierwszy. Zawiera opcjonalnie drawerToggleButton i ma FlexGrow.
     * Dodamy tutaj drawerToggleButton zgodnie z nową prośbą.
     */
    private Component leftSideNavbar() {
        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.addClassName("left-side-mask");

        logoLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        Image crewOpsLogoSvg = new Image("images/crew_ops_logo_metal_fixed.svg", "CrewOps Logo");
        crewOpsLogoSvg.getStyle().set("object-fit", "contain");

        crewOpsLogoSvg.setVisible(!authenticationResolver.principalIsAuthenticated());

        logoLayout.add(drawerToggleButton, crewOpsLogoSvg);

        logoLayout
                .getStyle()
                // Półprzezroczysty czarny kolor tła (60% nieprzezroczystości)
                .set("background-color", "rgba(0, 0, 0, 0.6)")
                // Efekt rozmycia tła (blur)
                .set("backdrop-filter", "blur(4px)")
                .set("-webkit-backdrop-filter", "blur(4px)")
                // Lekki cień
                .set("box-shadow", "0 2px 4px rgba(0, 0, 0, 0.6)")
                .set("border-radius", "8px");

        return logoLayout;
    }

    private HorizontalLayout createAuthComponentWrapper() {
        HorizontalLayout wrapper = new HorizontalLayout();
        wrapper.addClassName("auth-wrapper");

        wrapper.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        wrapper.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        wrapper.add(new LoggedUserInfoComponent(coreAPI, jwtService, authenticationResolver));

        return wrapper;
    }

    // ----------------------------------------------------------------------
    // LOGIKA PRZEŁĄCZANIA IKONY (bez zmian)
    // ----------------------------------------------------------------------

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        getUI().ifPresent(ui -> {
            ui.getChildren().filter(c -> c instanceof AppLayout).findFirst().ifPresent(appLayout -> {
                AppLayout mainAppLayout = (AppLayout) appLayout;

                drawerToggleButton.addClickListener(e -> {
                    boolean isOpened = !mainAppLayout.isDrawerOpened();
                    mainAppLayout.setDrawerOpened(isOpened);
                    updateDrawerIcon(isOpened);
                });

                updateDrawerIcon(mainAppLayout.isDrawerOpened());
            });
        });
    }

    private void updateDrawerIcon(boolean isOpened) {
        drawerToggleButton.setIcon(isOpened ? replyIcon : menuIcon);
    }
}
