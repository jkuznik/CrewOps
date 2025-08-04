package pl.crewops.view.layout;

import static pl.crewops.model.auth.RoleType.*;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import java.util.Set;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.crewops.model.auth.RoleGrantedAuthority;
import pl.crewops.security.custom.UserPrincipal;
import pl.crewops.security.jwt.JwtServiceVaadin;
import pl.crewops.view.EmployeeView;
import pl.crewops.view.HomeView;
import pl.crewops.view.MachineView;

@SpringComponent
@UIScope
@CssImport("./styles/mainStyles/main-drawer.css")
public class MainDrawer extends VerticalLayout {

    private final JwtServiceVaadin jwtService;

    private final RouterLink homeLink = new RouterLink(HomeView.class);
    private final RouterLink employeeLink = new RouterLink(EmployeeView.class);
    private final RouterLink machineLink = new RouterLink(MachineView.class);

    private final Span footerText = new Span();

    public MainDrawer(JwtServiceVaadin jwtService) {
        addClassName("main-drawer");

        this.jwtService = jwtService;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        localize();

        setSizeFull();
        setSpacing(true);
        setPadding(true);

        VerticalLayout linksLayout = new VerticalLayout();
        linksLayout.setSizeFull();
        linksLayout.setPadding(true);
        linksLayout.setSpacing(true);
        linksLayout.add(homeLink, employeeLink, machineLink);

        add(linksLayout, createDrawerFooter());
        setFlexGrow(1, linksLayout);

        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {

            customizeDrawerDependsOnRoles(userPrincipal, employeeLink, machineLink);
        } else {
            customizeDrawerDependsOnRoles(null, employeeLink, machineLink);
        }
    }

    private void localize() {
        homeLink.setText(getTranslation("mainDrawer.link.home"));
        employeeLink.setText(getTranslation("mainDrawer.link.employee"));
        machineLink.setText(getTranslation("mainDrawer.link.machine"));

        footerText.setText(getTranslation("mainDrawer.footer.text"));
    }

    private Footer createDrawerFooter() {
        Footer footer = new Footer();
        footer.addClassName("drawer-footer");

        footerText.addClassName("drawer-footer-text");

        footer.add(footerText);

        return footer;
    }

    private void customizeDrawerDependsOnRoles(
            UserPrincipal principal, RouterLink employeeLink, RouterLink machineLink) {
        if (isRequestAuthenticated(principal)) {
            displayLinksDependsOnRoles(principal, employeeLink, machineLink);
        } else {
            hideLinksRequiredAuthentication(employeeLink, machineLink);
        }
    }

    private static void hideLinksRequiredAuthentication(RouterLink employeeLink, RouterLink machineLink) {
        employeeLink.setVisible(false);
        machineLink.setVisible(false);
    }

    private void displayLinksDependsOnRoles(UserPrincipal principal, RouterLink employeeLink, RouterLink machineLink) {
        Set<RoleGrantedAuthority> roleGrantedAuthorities = jwtService.extractAuthorities(principal.getToken());
        if (roleGrantedAuthorities.contains(new RoleGrantedAuthority(MANAGER))
                || roleGrantedAuthorities.contains(new RoleGrantedAuthority(COMPANY_ADMIN))
                || roleGrantedAuthorities.contains(new RoleGrantedAuthority(SYSTEM_ADMIN))) {
            employeeLink.setVisible(true);
            machineLink.setVisible(true);
        } else {
            employeeLink.setVisible(false);
            machineLink.setVisible(true);
        }
    }

    private boolean isRequestAuthenticated(UserPrincipal principal) {
        return principal != null && jwtService.validToken(principal.getToken());
    }
}
