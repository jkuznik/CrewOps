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
import pl.crewops.view.component.content.InfoContent;
import pl.crewops.view.layout.MainLayout;

@Route(value = "info")
@PageTitle("Information")
public class InfoView extends MainLayout {

    public InfoView(CoreAPI coreAPI, JwtServiceVaadin jwtService, RoleResolver roleResolver) {
        super(coreAPI, jwtService, roleResolver);
        addClassName("info-view");

        mainContent.removeAll();
        mainContent.add(getCurrentContent(), mainFooter);
        mainContent.setFlexGrow(1, getCurrentContent());
    }

    private VerticalLayout getCurrentContent() {
        VerticalLayout currentContent = new VerticalLayout();
        currentContent.setId("view-content");

        currentContent.setWidthFull();
        currentContent.setPadding(true);
        currentContent.setSpacing(true);
        currentContent.getStyle().set("overflow", "auto");

        InfoContent infoContent = new InfoContent();
        infoContent.setWidthFull();

        currentContent.add(infoContent);

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
