package pl.crewops.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import pl.crewops.component.content.HomeContent;
import pl.crewops.component.form.LoginForm;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtServiceVaadin;
import pl.crewops.util.AuthenticationResolver;
import pl.crewops.view.layout.MainLayout;

@Route(value = "")
@PageTitle("Crew Ops")
public class HomeView extends MainLayout {

    public HomeView(CoreAPI coreAPI, JwtServiceVaadin jwtService, AuthenticationResolver authenticationResolver) {
        super(coreAPI, jwtService, authenticationResolver);
        addClassName("home-view");

        mainContent.removeAll();

        if (!authenticationResolver.principalIsAuthenticated()) {
            remove(navbar);
            remove(drawer);
        }

        var currentContent = getCurrentContent();

        mainContent.add(currentContent, mainFooter);
        mainContent.setFlexGrow(1, currentContent);
    }

    private Component getCurrentContent() {
        Component currentContent;
        if (authenticationResolver.principalIsAuthenticated()) {
            VerticalLayout layout = new VerticalLayout();
            layout.setId("view-content");

            layout.setWidthFull();
            layout.setPadding(true);
            layout.setSpacing(true);
            layout.getStyle().set("overflow", "auto");

            HomeContent homeContent = new HomeContent();
            homeContent.setSizeFull();

            layout.add(homeContent);
            currentContent = layout;
        } else {
            Div container = new Div();
            container.setId("view-content");
            container.setSizeFull();
            container
                    .getStyle()
                    .set("display", "flex")
                    .set("align-items", "center")
                    .set("justify-content", "center");

            var loginForm = new LoginForm(coreAPI, jwtService);
            loginForm.setWidth("400px");
            loginForm.getStyle().set("max-width", "90%");

            container.add(loginForm);
            currentContent = container;
        }
        return currentContent;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        UI.getCurrent()
                .getPage()
                .executeJs(
                        """
                        const content = document.getElementById('view-content');
                        const footer = document.getElementById('footer');
                        content.addEventListener('scroll', function() {
                            // Percentage of content scroll
                            const scrollTop = content.scrollTop;
                            const scrollHeight = content.scrollHeight;
                            const clientHeight = content.clientHeight;
                            const scrolledPercentage = (scrollTop + clientHeight) / scrollHeight;

                            if (scrolledPercentage >= 0.80) {
                                $0.$server.showFooter(true);
                            } else {
                                $0.$server.showFooter(false);
                            }
                        });
                        """,
                        getElement());
    }

    @ClientCallable
    public void showFooter(boolean show) {
        if (show) {
            mainFooter.getStyle().set("opacity", "1");
        } else {
            mainFooter.getStyle().set("opacity", "0");
        }
        mainFooter.setVisible(show);
    }
}
