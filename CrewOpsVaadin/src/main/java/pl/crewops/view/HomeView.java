package pl.crewops.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtServiceVaadin;
import pl.crewops.util.RoleResolver;
import pl.crewops.view.component.content.HomeContent;
import pl.crewops.view.layout.MainLayout;

@Route(value = "")
@PageTitle("Crew Ops")
public class HomeView extends MainLayout {

    public HomeView(CoreAPI coreAPI, JwtServiceVaadin jwtService, RoleResolver roleResolver) {
        super(coreAPI, jwtService, roleResolver);
        addClassName("home-view");

        mainContent.removeAll();
        VerticalLayout currentContent = getCurrentContent();
        currentContent.setSizeFull();

        mainContent.add(currentContent, mainFooter);
        mainContent.setFlexGrow(1, currentContent);
    }

    private VerticalLayout getCurrentContent() {
        VerticalLayout currentContent = new VerticalLayout();
        currentContent.setId("view-content");

        currentContent.setWidthFull();
        currentContent.setPadding(true);
        currentContent.setSpacing(true);
        currentContent.getStyle().set("overflow", "auto");

        HomeContent homeContent = new HomeContent();
        homeContent.setSizeFull();

        currentContent.add(homeContent);

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
