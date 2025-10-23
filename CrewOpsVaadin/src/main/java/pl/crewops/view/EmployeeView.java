package pl.crewops.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import pl.crewops.component.grid.EmployeeGrid;
import pl.crewops.component.grid.JobPositionGrid;
import pl.crewops.component.grid.QualificationGrid;
import pl.crewops.component.notification.FailNotification;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtServiceVaadin;
import pl.crewops.util.AuthenticationResolver;
import pl.crewops.view.layout.MainLayout;

@Route(value = "employees")
@PageTitle("Employee management")
public class EmployeeView extends MainLayout implements BeforeEnterObserver {
    private EmployeeGrid employeeGrid;
    private QualificationGrid qualificationGrid;
    private JobPositionGrid jobPositionGrid;

    public EmployeeView(CoreAPI coreAPI, JwtServiceVaadin jwtService, AuthenticationResolver authenticationResolver) {
        super(coreAPI, jwtService, authenticationResolver);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticationResolver.principalIsAuthenticated()) {
            try {
                mainContent.removeAll();
                listeners.forEach(Registration::remove);
                buildContent();
            } catch (Exception e) {
                new FailNotification(getTranslation("dailyView.failNotification"));
            }
        } else {
            event.forwardTo(HomeView.class);
            UI.getCurrent().getPage().setLocation("/");
        }
    }

    private void buildContent() {
        addClassName("employee-view");

        employeeGrid = new EmployeeGrid(coreAPI, authenticationResolver);
        qualificationGrid = new QualificationGrid(coreAPI);
        jobPositionGrid = new JobPositionGrid(coreAPI, authenticationResolver);
        employeeGrid.setQualificationGrid(qualificationGrid);
        qualificationGrid.setEmployeeGrid(employeeGrid);

        qualificationGrid.setVisible(false);
        jobPositionGrid.setVisible(false);

        mainContent.add(getToolbar(), employeeGrid, qualificationGrid, jobPositionGrid);
    }

    private HorizontalLayout getToolbar() {
        var toolbar = new HorizontalLayout();

        Button employeeList = new Button(getTranslation("employeeView.employeeList"));
        employeeList.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        employeeList.setWidth("160px");

        Button qualifications = new Button(getTranslation("employeeView.qualifications"));
        qualifications.setWidth("160px");

        Button jobPositions = new Button(getTranslation("employeeView.jobPositions"));
        jobPositions.setWidth("160px");

        Registration registration = employeeList.addClickListener(event -> {
            displayEmployeeGrid();

            employeeList.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            qualifications.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
            jobPositions.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
        });
        listeners.add(registration);

        Registration registration1 = qualifications.addClickListener(event -> {
            displayQualificationGrid();

            qualifications.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            employeeList.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
            jobPositions.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
        });
        listeners.add(registration1);

        jobPositions.addClickListener(event -> {
            displayJobPositionGrid();

            jobPositions.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            employeeList.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
            qualifications.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
        });

        toolbar.add(employeeList, qualifications, jobPositions);

        return toolbar;
    }

    private void displayEmployeeGrid() {
        qualificationGrid.setVisible(false);
        jobPositionGrid.setVisible(false);

        employeeGrid.closeEditor();
        employeeGrid.setVisible(true);
    }

    private void displayQualificationGrid() {
        employeeGrid.setVisible(false);
        jobPositionGrid.setVisible(false);

        qualificationGrid.closeEditor();
        qualificationGrid.setVisible(true);
    }

    private void displayJobPositionGrid() {
        employeeGrid.setVisible(false);
        qualificationGrid.setVisible(false);

        //        jobPositionGrid.closeEditor();
        jobPositionGrid.setVisible(true);
    }
}
