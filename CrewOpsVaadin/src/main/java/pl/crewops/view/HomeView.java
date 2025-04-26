package pl.crewops.view;

import com.vaadin.flow.component.html.H1;
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

        H1 title = new H1("Tutaj treść strony startowej");
        add(title);
    }
}
