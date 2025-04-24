package pl.crewops.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.annotation.SpringComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.view.form.LoginForm;

@SpringComponent
@Slf4j
@Scope("prototype")
@Route(value = "")
@PageTitle("Crew Ops")
public class HomeView extends AppLayout {

    private final CoreAPI coreAPI;

    public HomeView(CoreAPI coreAPI) {
        addClassName("home-view");

        this.coreAPI = coreAPI;
        addToNavbar(createHeader());
        addToDrawer(createDrawer());
    }

    private Component createHeader() {
        var toolbar = new HorizontalLayout();
        var headerLayout = new HorizontalLayout();
        H1 title = new H1("CrewOps");
        headerLayout.setWidthFull();
        headerLayout.add(title);

        var loginLayout = new HorizontalLayout();
        LoginForm loginForm = new LoginForm(coreAPI);

        loginLayout.setWidthFull();
        loginLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        loginLayout.getStyle().set("padding-right", "20px");

        loginLayout.add(loginForm);

        toolbar.setWidthFull();
        toolbar.add(headerLayout, loginLayout);
        toolbar.setFlexGrow(1, headerLayout);
        toolbar.setFlexGrow(1, loginLayout);

        return toolbar;
    }

    private Component createDrawer() {
        RouterLink employeeLink = new RouterLink("Employee", EmployeeView.class);
        RouterLink qualificationLink = new RouterLink("Qualification", QualificationView.class);
        RouterLink vehicleLink = new RouterLink("Vehicle", VehicleView.class);

        employeeLink.setHighlightCondition(HighlightConditions.sameLocation());

        return new VerticalLayout(employeeLink, qualificationLink, vehicleLink);
    }
}
