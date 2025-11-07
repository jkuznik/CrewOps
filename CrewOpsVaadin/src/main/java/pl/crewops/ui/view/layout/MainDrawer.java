package pl.crewops.ui.view.layout;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import pl.crewops.ui.component.notification.FailNotification;
import pl.crewops.ui.view.DailyView;
import pl.crewops.ui.view.EmployeeView;
import pl.crewops.ui.view.HomeView;
import pl.crewops.ui.view.MachineView;
import pl.crewops.util.AuthenticationResolver;

@SpringComponent
@UIScope
@CssImport("./styles/mainStyles/main-drawer.css")
public class MainDrawer extends VerticalLayout {
    private final AuthenticationResolver authenticationResolver;

    private final RouterLink homeLink = new RouterLink(HomeView.class);
    private final RouterLink dailyLink = new RouterLink(DailyView.class);
    private final RouterLink employeeLink = new RouterLink(EmployeeView.class);
    private final RouterLink machineLink = new RouterLink(MachineView.class);

    private final Span footerText = new Span();

    private final Span homeTextSpan = new Span();
    private final Span dailyTextSpan = new Span();
    private final Span employeeTextSpan = new Span();
    private final Span machineTextSpan = new Span();

    private static final String DRAWER_COLOR = "white";

    public MainDrawer(AuthenticationResolver authenticationResolver) {
        this.authenticationResolver = authenticationResolver;

        addClassName("main-drawer");

        try {

            localize();
            customizeLinks();

            setSizeFull();
            setSpacing(true);
            setPadding(true);

            VerticalLayout linksLayout = new VerticalLayout();
            linksLayout.setSizeFull();
            linksLayout.setPadding(true);
            linksLayout.setSpacing(true);
            linksLayout.add(homeLink, dailyLink, employeeLink, machineLink);

            add(linksLayout, createDrawerFooter());
            setFlexGrow(1, linksLayout);

            if (authenticationResolver.principalIsAuthenticated()) {
                displayLinksDependsOnRoles();
            } else {
                hideLinksRequiredAuthentication();
            }
        } catch (Exception e) {
            new FailNotification(getTranslation("dailyView.failNotification"));
        }
    }

    private void customizeLinks() {
        addIconAndPlaceholder(homeLink, VaadinIcon.HOME, homeTextSpan);
        addIconAndPlaceholder(dailyLink, VaadinIcon.CALENDAR_CLOCK, dailyTextSpan);
        addIconAndPlaceholder(employeeLink, VaadinIcon.USERS, employeeTextSpan);
        addIconAndPlaceholder(machineLink, VaadinIcon.TOOLS, machineTextSpan);
    }

    private void addIconAndPlaceholder(RouterLink link, VaadinIcon iconType, Span textSpan) {
        HorizontalLayout content = new HorizontalLayout();
        content.setSpacing(true);
        content.setAlignItems(FlexComponent.Alignment.CENTER);

        Icon icon = new Icon(iconType);

        // Ustawienie koloru dla Ikony Vaadin
        icon.getStyle().set("color", DRAWER_COLOR);

        // Ustawienie koloru dla tekstu Span
        textSpan.getStyle().set("color", DRAWER_COLOR);

        content.add(icon, textSpan);

        link.getElement().appendChild(content.getElement());
        link.addClassName("drawer-link-with-icon");
    }

    private void localize() {
        homeTextSpan.setText(getTranslation("mainDrawer.link.home"));
        dailyTextSpan.setText(getTranslation("mainDrawer.link.daily"));
        employeeTextSpan.setText(getTranslation("mainDrawer.link.employee"));
        machineTextSpan.setText(getTranslation("mainDrawer.link.machine"));

        footerText.setText(getTranslation("mainDrawer.footer.text"));
    }

    private Footer createDrawerFooter() {
        Footer footer = new Footer();
        footer.addClassName("drawer-footer");

        footerText.addClassName("drawer-footer-text");
        // Ustawienie koloru dla tekstu stopki
        footerText.getStyle().set("color", DRAWER_COLOR);

        footer.add(footerText);
        return footer;
    }

    private void hideLinksRequiredAuthentication() {
        dailyLink.setVisible(false);
        employeeLink.setVisible(false);
        machineLink.setVisible(false);
    }

    private void displayLinksDependsOnRoles() {
        if (authenticationResolver.principalHasManagerPermission()) {
            employeeLink.setVisible(true);
        } else {
            employeeLink.setVisible(false);
        }
    }
}
