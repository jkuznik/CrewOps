package pl.crewops.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import pl.crewops.component.grid.EmployeeGrid;
import pl.crewops.component.grid.QualificationGrid;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtServiceVaadin;
import pl.crewops.util.RoleResolver;
import pl.crewops.view.layout.MainLayout;

@Route(value = "employees")
@PageTitle("Employee management")
public class EmployeeView extends MainLayout implements BeforeEnterObserver {
    private final EmployeeGrid employeeGrid;
    private final QualificationGrid qualificationGrid;

    public EmployeeView(CoreAPI coreAPI, JwtServiceVaadin jwtService, RoleResolver roleResolver) {
        super(coreAPI, jwtService, roleResolver);

        employeeGrid = new EmployeeGrid(coreAPI, roleResolver);
        qualificationGrid = new QualificationGrid(coreAPI);
        employeeGrid.setQualificationGrid(qualificationGrid);
        qualificationGrid.setEmployeeGrid(employeeGrid);

        qualificationGrid.setVisible(false);
        addClassName("employee-view");

        mainContent.removeAll();
        // TODO: temporary remove footer from this view, there is te way to add it again
        //        mainContent.add(getToolbar(), employeeGrid, qualificationGrid, mainFooter);
        mainContent.add(getToolbar(), employeeGrid, qualificationGrid);
        mainContent.setFlexGrow(1, employeeGrid);
        mainContent.setFlexGrow(1, qualificationGrid);
    }

    private HorizontalLayout getToolbar() {
        var toolbar = new HorizontalLayout();

        Button employeeList = new Button(getTranslation("employeeView.employeeList"));
        Button qualifications = new Button(getTranslation("employeeView.qualifications"));
        employeeList.addClickListener(event -> displayEmployeeGrid());
        qualifications.addClickListener(event -> displayQualificationGrid());

        toolbar.add(employeeList, qualifications);

        return toolbar;
    }

    private void displayEmployeeGrid() {
        qualificationGrid.setVisible(false);

        employeeGrid.closeEditor();
        employeeGrid.setVisible(true);
    }

    private void displayQualificationGrid() {
        employeeGrid.setVisible(false);

        qualificationGrid.closeEditor();
        qualificationGrid.setVisible(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!roleResolver.principalHasManagerRole()) {
            event.forwardTo(HomeView.class);
            UI.getCurrent().getPage().setLocation("/");
        }
    }
}
