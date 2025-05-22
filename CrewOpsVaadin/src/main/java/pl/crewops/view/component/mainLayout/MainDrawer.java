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
import pl.crewops.infrastructure.localization.CustomI18NProvider;
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
    private final CustomI18NProvider i18nProvider;
    private UserPrincipal principal;

    public MainDrawer(CoreAPI coreAPI, JwtService jwtService, CustomI18NProvider customI18NProvider) {
        addClassName("main-drawer");

        this.coreAPI = coreAPI;
        this.jwtService = jwtService;
        this.i18nProvider = customI18NProvider;

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

        RouterLink homeLink = new RouterLink(
                customI18NProvider.getTranslation("mainDrawer.link.home", customI18NProvider.getCurrentLocale()),
                HomeView.class);
        RouterLink employeeLink = new RouterLink(
                customI18NProvider.getTranslation("mainDrawer.link.employee", customI18NProvider.getCurrentLocale()),
                EmployeeView.class);
        RouterLink vehicleLink = new RouterLink(
                customI18NProvider.getTranslation("mainDrawer.link.vehicle", customI18NProvider.getCurrentLocale()),
                VehicleView.class);

        VerticalLayout linksLayout = new VerticalLayout();
        linksLayout.setSizeFull();
        linksLayout.setPadding(true);
        linksLayout.setSpacing(true);
        linksLayout.add(homeLink, employeeLink, vehicleLink);

        add(linksLayout, createDrawerFooter());
        setFlexGrow(1, linksLayout);

        checkDrawer(employeeLink, vehicleLink);
    }

    private Footer createDrawerFooter() {
        Footer footer = new Footer();
        footer.addClassName("drawer-footer");

        String footerTextStr = i18nProvider.getTranslation("mainDrawer.footer.text", i18nProvider.getCurrentLocale());
        Span footerText = new Span(footerTextStr);
        footerText.addClassName("drawer-footer-text");

        footer.add(footerText);

        return footer;
    }

    private void checkDrawer(RouterLink employeeLink, RouterLink vehicleLink) {
        if (principal == null || !jwtService.validToken(principal.getToken())) {
            employeeLink.setVisible(false);
            vehicleLink.setVisible(false);
        } else {
            employeeLink.setVisible(true);
            vehicleLink.setVisible(true);
        }
    }
}
