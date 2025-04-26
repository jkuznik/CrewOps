package pl.crewops.view;

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

@SpringComponent
@Slf4j
@Scope("prototype")
@Route(value = "")
@PageTitle("Crew Ops")
public class HomeView extends VerticalLayout {

    public HomeView(CoreAPI coreAPI, JwtInfoService jwtInfoService) {
        addClassName("home-view");
        setSizeFull(); // Zajmij całą wysokość

        // Layout główny: treść + stopka
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setPadding(false);
        mainLayout.setSpacing(false);

        // Treść
        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);
        content.setSpacing(true);
        content.setWidthFull();
        content.getStyle().set("overflow", "auto"); // <--- WAŻNE
        content.setHeightFull();

        H1 title = new H1("Tutaj treść strony startowej");
        content.add(title);

        // Dodajemy DUŻO linijek
        for (int i = 1; i <= 50; i++) {
            content.add(new Span("Linijka tekstu numer " + i));
        }

        Footer footer = createFooter();

        mainLayout.add(content, footer);
        mainLayout.setFlexGrow(1, content); // <-- content rośnie, footer stoi

        add(mainLayout);
    }

    private Footer createFooter() {
        Footer footer = new Footer();
        footer.getStyle()
                .set("width", "100%")
                .set("text-align", "center")
                .set("padding", "10px")
                .set("background-color", "#f1f1f1");

        Span footerText = new Span("© 2025 CrewOps - by Janusz Kuźnik.");
        footerText
                .getStyle()
                .set("font-size", "12px")
                .set("color", "#888")
                .set("margin-top", "auto")
                .set("text-align", "center");

        footer.add(footerText);

        return footer;
    }
}
