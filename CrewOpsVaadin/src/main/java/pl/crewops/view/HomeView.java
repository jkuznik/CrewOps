package pl.crewops.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtInfoService;
import pl.crewops.view.component.mainLayout.MainLayout;

@SpringComponent
@Slf4j
@Scope("prototype")
@Route(value = "")
@PageTitle("Crew Ops")
public class HomeView extends MainLayout {

    public HomeView(CoreAPI coreAPI, JwtInfoService jwtInfoService) {
        super(coreAPI, jwtInfoService);
        addClassName("home-view");
        VerticalLayout currentContent = new VerticalLayout();
        currentContent.setId("current-content");

        mainContent.removeAll();
        mainContent.add(currentContent, mainFooter);
        mainContent.setFlexGrow(1, currentContent);

        currentContent.setSizeFull();
        currentContent.setPadding(true);
        currentContent.setSpacing(true);
        currentContent.getStyle().set("overflow", "auto");

        H1 title = new H1("Tutaj treść strony startowej");
        currentContent.add(title);

        for (int i = 1; i <= 50; i++) {
            currentContent.add(new Span("Linijka tekstu numer " + i));
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        UI.getCurrent()
                .getPage()
                .executeJs(
                        """
                const content = document.getElementById('current-content');
                const footer = document.getElementById('footer');
                content.addEventListener('scroll', function() {
                    // Obliczamy procent przewinięcia
                    const scrollTop = content.scrollTop;
                    const scrollHeight = content.scrollHeight;
                    const clientHeight = content.clientHeight;
                    const scrolledPercentage = (scrollTop + clientHeight) / scrollHeight;

                    if (scrolledPercentage >= 0.95) {
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
