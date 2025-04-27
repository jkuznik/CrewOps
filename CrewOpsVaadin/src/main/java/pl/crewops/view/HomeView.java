package pl.crewops.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Footer;
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
import pl.crewops.view.component.HomeViewFooter;
import pl.crewops.view.component.MainLayout;

@SpringComponent
@Slf4j
@Scope("prototype")
@Route(value = "", layout = MainLayout.class)
@PageTitle("Crew Ops")
public class HomeView extends VerticalLayout {

    private Footer footer;

    public HomeView(CoreAPI coreAPI, JwtInfoService jwtInfoService) {
        addClassName("home-view");
        setSizeFull();

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setPadding(false);
        mainLayout.setSpacing(false);

        VerticalLayout content = new VerticalLayout();
        content.setId("content");
        content.setPadding(true);
        content.setSpacing(true);
        content.setWidthFull();
        content.getStyle().set("overflow", "auto");
        content.setHeightFull();

        H1 title = new H1("Tutaj treść strony startowej");
        content.add(title);

        for (int i = 1; i <= 50; i++) {
            content.add(new Span("Linijka tekstu numer " + i));
        }

        this.footer = new HomeViewFooter();
        this.footer.setVisible(false);

        mainLayout.add(content, footer);
        mainLayout.setFlexGrow(1, content);

        add(mainLayout);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        UI.getCurrent()
                .getPage()
                .executeJs(
                        """
                const content = document.getElementById('content');
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
            footer.getStyle().set("opacity", "1");
        } else {
            footer.getStyle().set("opacity", "0");
        }
        footer.setVisible(show);
    }
}
