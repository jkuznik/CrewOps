package pl.crewops.ui.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtServiceVaadin;
import pl.crewops.ui.component.content.InfoContent;
import pl.crewops.ui.component.notification.FailNotification;
import pl.crewops.ui.view.layout.MainLayout;
import pl.crewops.util.AuthenticationResolver;

@Route(value = "info")
@PageTitle("Information")
public class InfoView extends MainLayout {

    public InfoView(CoreAPI coreAPI, JwtServiceVaadin jwtService, AuthenticationResolver authenticationResolver) {
        super(coreAPI, jwtService, authenticationResolver);
        addClassName("info-view");

        try {
            mainContent.removeAll();
            listeners.forEach(Registration::remove);

            mainContent.add(getCurrentContent(), mainFooter);
            mainContent.setFlexGrow(1, getCurrentContent());
        } catch (Exception e) {
            new FailNotification(getTranslation("dailyView.failNotification"));
        }
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
