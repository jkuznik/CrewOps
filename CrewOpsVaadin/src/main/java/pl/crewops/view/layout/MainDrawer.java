package pl.crewops.view.layout;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import pl.crewops.component.navbarComponents.CustomerRegistryButton;
import pl.crewops.util.AuthenticationResolver;
import pl.crewops.view.EmployeeView;
import pl.crewops.view.HomeView;
import pl.crewops.view.MachineView;

@SpringComponent
@UIScope
@CssImport("./styles/mainStyles/main-drawer.css")
public class MainDrawer extends VerticalLayout {
    private final AuthenticationResolver authenticationResolver;

    private final RouterLink homeLink = new RouterLink(HomeView.class);
    private final RouterLink employeeLink = new RouterLink(EmployeeView.class);
    private final RouterLink machineLink = new RouterLink(MachineView.class);

    private final Span footerText = new Span();

    public MainDrawer(AuthenticationResolver authenticationResolver) {
        addClassName("main-drawer");

        this.authenticationResolver = authenticationResolver;

        localize();

        setSizeFull();
        setSpacing(true);
        setPadding(true);

        VerticalLayout linksLayout = new VerticalLayout();
        linksLayout.setSizeFull();
        linksLayout.setPadding(true);
        linksLayout.setSpacing(true);
        linksLayout.add(homeLink, employeeLink, machineLink);

        if (authenticationResolver.principalHasSystemAdminPermission()) {
            linksLayout.add(new CustomerRegistryButton());
        }

        add(linksLayout, createDrawerFooter());
        setFlexGrow(1, linksLayout);

        if (authenticationResolver.principalIsAuthenticated()) {
            displayLinksDependsOnRoles(employeeLink, machineLink);
        } else {
            hideLinksRequiredAuthentication(employeeLink, machineLink);
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

    private static void hideLinksRequiredAuthentication(RouterLink employeeLink, RouterLink machineLink) {
        employeeLink.setVisible(false);
        machineLink.setVisible(false);
    }

    private void displayLinksDependsOnRoles(RouterLink employeeLink, RouterLink machineLink) {
        if (authenticationResolver.principalHasManagerPermission()) {
            employeeLink.setVisible(true);
            machineLink.setVisible(true);
        } else {
            employeeLink.setVisible(false);
            machineLink.setVisible(true);
        }
    }
}
