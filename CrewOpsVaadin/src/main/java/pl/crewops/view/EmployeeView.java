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
import pl.crewops.view.component.mainLayout.MainLayout;

@Route(value = "employees")
@PageTitle("Employee management")
public class EmployeeView extends MainLayout implements BeforeEnterObserver {
    private final EmployeeGrid employeeGrid;

    public EmployeeView(CoreAPI coreAPI, JwtInfoService jwtInfoService) {
        super(coreAPI, jwtInfoService);
        employeeGrid = new EmployeeGrid(coreAPI);
        addClassName("employee-view");

        mainContent.removeAll();
        mainContent.add(getToolbar(), employeeGrid, mainFooter);
        mainContent.setFlexGrow(1, employeeGrid);
    }

    private HorizontalLayout getToolbar() {
        var toolbar = new HorizontalLayout();

        Button list = new Button("Employee list");
        Button breakdown = new Button("Breakdown");
        Button addVehicleButton = new Button("Add vehicle");
        list.addClickListener(event -> listEvent());
        breakdown.addClickListener(event -> breakdownEvent());
        addVehicleButton.addClickListener(event -> addVehicleEvent());

        toolbar.add(list, breakdown, addVehicleButton);

        return toolbar;
    }

    private void listEvent() {
        employeeGrid.closeEditor();
        employeeGrid.setVisible(true);
    }

    private void breakdownEvent() {
        employeeGrid.setVisible(false);
    }

    private void addVehicleEvent() {
        employeeGrid.setVisible(true);
        employeeGrid.addEmployeeEvent();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!jwtInfoService.validToken()) {
            event.forwardTo(HomeView.class);
            UI.getCurrent().getPage().setLocation("/");
        }
    }
}
