package pl.crewops.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import pl.crewops.component.content.HomeContent;
import pl.crewops.component.content.RegistryContent;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtServiceVaadin;
import pl.crewops.util.AuthenticationResolver;
import pl.crewops.view.layout.MainLayout;

@Route(value = "")
@PageTitle("Crew Ops")
public class HomeView extends MainLayout {

    // TODO:
    //  rebuild register customer feature in a way that option will be available on homepage - during new customer
    //  registration only required data should be company information - that create new tenant, company and initial
    //  company admin user but with that difference this initial user has empty values what should be updated after
    // first
    //  login action. Username of this initial user should be related to company name -
    //  only system admin can modified username <- add admin console for that.

    public HomeView(CoreAPI coreAPI, JwtServiceVaadin jwtService, AuthenticationResolver authenticationResolver) {
        super(coreAPI, jwtService, authenticationResolver);
        addClassName("home-view");

        mainContent.removeAll();

        if (!authenticationResolver.principalIsAuthenticated()) {
            setDrawerOpened(false);
        }

        var currentContent = getCurrentContent();

        mainContent.add(currentContent, mainFooter);
        mainContent.setFlexGrow(1, currentContent);
    }

    private Component getCurrentContent() {
        var layout = new VerticalLayout();

        layout.setId("view-content");

        layout.setWidthFull();
        layout.setPadding(true);
        layout.setSpacing(true);
        layout.getStyle().set("overflow", "auto");

        if (!authenticationResolver.principalIsAuthenticated()) {
            layout.add(new RegistryContent());
        } else {
            HomeContent homeContent = new HomeContent();
            homeContent.setWidthFull();

            layout.add(homeContent);
        }
        return layout;
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
