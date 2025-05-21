package pl.crewops.view.component.mainLayout;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import java.util.Locale;
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

    public MainDrawer(CoreAPI coreAPI, JwtService jwtService, CustomI18NProvider i18nProvider) {
        addClassName("main-drawer");

        this.coreAPI = coreAPI;
        this.jwtService = jwtService;
        this.i18nProvider = i18nProvider;

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

        var locale = UI.getCurrent().getLocale();

        RouterLink homeLink =
                new RouterLink(i18nProvider.getTranslation("mainDrawer.link.home", locale), HomeView.class);
        RouterLink employeeLink =
                new RouterLink(i18nProvider.getTranslation("mainDrawer.link.employee", locale), EmployeeView.class);
        RouterLink vehicleLink =
                new RouterLink(i18nProvider.getTranslation("mainDrawer.link.vehicle", locale), VehicleView.class);

        VerticalLayout linksLayout = new VerticalLayout();
        linksLayout.setSizeFull();
        linksLayout.setPadding(true);
        linksLayout.setSpacing(true);
        linksLayout.add(homeLink, employeeLink, vehicleLink);

        add(linksLayout, createDrawerFooter(locale));
        setFlexGrow(1, linksLayout);

        checkDrawer(employeeLink, vehicleLink);
    }

    private Footer createDrawerFooter(Locale locale) {
        Footer footer = new Footer();
        footer.addClassName("drawer-footer");

        String footerTextStr = i18nProvider.getTranslation("mainDrawer.footer.text", locale);
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
