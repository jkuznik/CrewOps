package pl.crewops.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtService;
import pl.crewops.view.component.mainLayout.MainLayout;

@Route(value = "")
@PageTitle("Crew Ops")
public class HomeView extends MainLayout {

    public HomeView(CoreAPI coreAPI, JwtService jwtService) {
        super(coreAPI, jwtService);
        addClassName("home-view");

        mainContent.removeAll();
        mainContent.add(getCurrentContent(), mainFooter);
        mainContent.setFlexGrow(1, getCurrentContent());
    }

    private VerticalLayout getCurrentContent() {
        VerticalLayout currentContent = new VerticalLayout();
        currentContent.setId("view-content");

        currentContent.setSizeFull();
        currentContent.setPadding(true);
        currentContent.setSpacing(true);
        currentContent.getStyle().set("overflow", "auto");

        Span label = new Span(getTranslation("homeView.title"));
        label.getStyle().set("white-space", "pre-wrap");

        H1 title = new H1(label);
        currentContent.add(title);
        // TODO: delete this code snippet any time, this code only check if footer is displaying in proper time depends
        // on scroller
        //        for (int i = 1; i <= 50; i++) {
        //            String lineText = getTranslation("homeView.line", i);
        //            currentContent.add(new Span(lineText));
        //        }

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
