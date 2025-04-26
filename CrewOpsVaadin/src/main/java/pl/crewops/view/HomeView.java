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
import pl.crewops.view.component.MainLayout;

@SpringComponent
@Slf4j
@Scope("prototype")
@Route(value = "", layout = MainLayout.class)
@PageTitle("Crew Ops")
public class HomeView extends VerticalLayout {

    public HomeView(CoreAPI coreAPI, JwtInfoService jwtInfoService) {
        addClassName("home-view");

        H1 title = new H1("CrewOps");
        add(title);
    }
}
