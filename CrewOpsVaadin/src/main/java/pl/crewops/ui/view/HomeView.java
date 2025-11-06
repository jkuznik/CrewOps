package pl.crewops.ui.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtServiceVaadin;
import pl.crewops.ui.component.content.HomeContent;
import pl.crewops.ui.component.content.RegistryContent;
import pl.crewops.ui.view.layout.MainLayout;
import pl.crewops.util.AuthenticationResolver;

@Route(value = "")
@PageTitle("Crew Ops")
public class HomeView extends MainLayout {

    public HomeView(CoreAPI coreAPI, JwtServiceVaadin jwtService, AuthenticationResolver authenticationResolver) {
        super(coreAPI, jwtService, authenticationResolver);
        addClassName("home-view");

        //        try {
        mainContent.removeAll();
        listeners.forEach(Registration::remove);

        if (!authenticationResolver.principalIsAuthenticated()) {
            setDrawerOpened(false);
        }

        var currentContent = getCurrentContent();

        mainContent.add(currentContent, mainFooter);
        mainContent.setFlexGrow(1, currentContent);
        //        } catch (Exception e) {
        //            new FailNotification(getTranslation("dailyView.failNotification"));
        //        }
    }

    private Component getCurrentContent() {
        var layout = new VerticalLayout();
        //        final String IMAGE_PATH = "images/crew_ops_future_static_logo.png";

        layout.setId("view-content");
        layout.setWidthFull();
        layout.setPadding(true);
        layout.setSpacing(true);
        layout.getStyle().set("overflow", "auto");

        if (!authenticationResolver.principalIsAuthenticated()) {
            layout.getStyle().set("background-size", "cover");
            layout.getStyle().set("background-position", "center");

            //            String overlayAndImage = "linear-gradient(rgba(0, 0, 0, 0.95), rgba(0, 0, 0, 0.95)), url('" +
            // IMAGE_PATH + "')";

            //            layout.getStyle().set("background-image", overlayAndImage);
            //            layout.getStyle().set("background-repeat", "no-repeat");

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
        // 🚨 KOREKTA: Zamiast sterowania widocznością, sterujemy klasą CSS

        // Używamy tej samej logiki opacitiy, ale dodajemy też klasę
        if (show) {
            // Dodaj klasę, aby stopka się POKAZAŁA (rozwinięcie i opacitiy 1)
            mainFooter.addClassName("footer-visible");
            mainFooter.removeClassName("footer-hidden"); // Opcjonalnie, ale czystsze
            mainFooter.getStyle().set("opacity", "1");
        } else {
            // Dodaj klasę, aby stopka się UKRYŁA (zwinięcie i opacitiy 0)
            mainFooter.removeClassName("footer-visible");
            mainFooter.addClassName("footer-hidden");
            mainFooter.getStyle().set("opacity", "0");
        }

        // UWAGA: mainFooter.setVisible(show) MUSI zostać USUNIĘTE,
        // jeśli było wcześniej (nie ma go w tym fragmencie, ale warto upewnić się, że nie jest w mainLayout)
    }
}
