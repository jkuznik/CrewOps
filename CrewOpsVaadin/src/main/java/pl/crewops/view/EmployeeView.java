package pl.crewops.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtInfoService;
import pl.crewops.view.component.EmployeeGrid;
import pl.crewops.view.component.QualificationGrid;
import pl.crewops.view.component.mainLayout.MainLayout;

@Route(value = "employees")
@PageTitle("Employee management")
public class EmployeeView extends MainLayout implements BeforeEnterObserver {
    private final EmployeeGrid employeeGrid;
    private final QualificationGrid qualificationGrid;

    public EmployeeView(CoreAPI coreAPI, JwtInfoService jwtInfoService) {
        super(coreAPI, jwtInfoService);
        employeeGrid = new EmployeeGrid(coreAPI);
        qualificationGrid = new QualificationGrid(coreAPI);
        qualificationGrid.setVisible(false);
        addClassName("employee-view");

        mainContent.removeAll();
        mainContent.add(getToolbar(), employeeGrid, qualificationGrid, mainFooter);
        mainContent.setFlexGrow(1, employeeGrid);
        mainContent.setFlexGrow(1, qualificationGrid);
    }

    private HorizontalLayout getToolbar() {
        var toolbar = new HorizontalLayout();

        Button employeeList = new Button("Employee list");
        Button qualifications = new Button("Qualifications");
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
        if (!jwtInfoService.validToken()) {
            event.forwardTo(HomeView.class);
            UI.getCurrent().getPage().setLocation("/");
        }
    }
}
