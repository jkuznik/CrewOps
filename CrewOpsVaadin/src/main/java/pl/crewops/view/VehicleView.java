package pl.crewops.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtServiceVaadin;
import pl.crewops.util.RoleResolver;
import pl.crewops.view.component.grid.BreakdownGrid;
import pl.crewops.view.component.grid.VehicleGrid;
import pl.crewops.view.layout.MainLayout;

@Slf4j
@Route(value = "vehicles")
@PageTitle("Vehicle view")
public class VehicleView extends MainLayout implements BeforeEnterObserver {
    private final VehicleGrid vehicleGrid;
    private final BreakdownGrid breakdownGrid;

    public VehicleView(CoreAPI coreAPI, JwtServiceVaadin jwtService, RoleResolver roleResolver) {
        super(coreAPI, jwtService, roleResolver);

        breakdownGrid = new BreakdownGrid(coreAPI, roleResolver);
        breakdownGrid.setSizeFull();
        breakdownGrid.setVisible(false);

        vehicleGrid = new VehicleGrid(coreAPI, breakdownGrid, roleResolver);
        vehicleGrid.setSizeFull();

        addClassName("vehicle-view");

        mainContent.removeAll();
        mainContent.add(getToolbar(), vehicleGrid, breakdownGrid, mainFooter);
        mainContent.setFlexGrow(1, vehicleGrid);
    }

    private HorizontalLayout getToolbar() {
        var toolbar = new HorizontalLayout();

        Button list = new Button(getTranslation("vehicleView.vehicleList"));
        Button breakdown = new Button(getTranslation("vehicleView.breakdowns"));
        list.addClickListener(event -> displayVehicleGrid());
        breakdown.addClickListener(event -> displayBreakdownGrid());

        toolbar.add(list, breakdown);

        return toolbar;
    }

    private void displayVehicleGrid() {
        breakdownGrid.setVisible(false);

        vehicleGrid.closeEditor();
        vehicleGrid.updateVehicleGrid();
        vehicleGrid.setVisible(true);
    }

    private void displayBreakdownGrid() {
        vehicleGrid.setVisible(false);

        breakdownGrid.closeEditor();
        breakdownGrid.setFilter("");
        breakdownGrid.updateBreakdownGrid();
        breakdownGrid.setVisible(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!roleResolver.principalIsAuthenticated()) {
            event.forwardTo(HomeView.class);
            UI.getCurrent().getPage().setLocation("/");
        }
    }
}
