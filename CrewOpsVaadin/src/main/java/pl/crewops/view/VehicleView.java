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
import pl.crewops.security.jwt.JwtInfoService;
import pl.crewops.view.component.grid.BreakdownGrid;
import pl.crewops.view.component.grid.VehicleGrid;
import pl.crewops.view.component.mainLayout.MainLayout;

@Slf4j
@Route(value = "vehicles")
@PageTitle("Vehicle view")
public class VehicleView extends MainLayout implements BeforeEnterObserver {
    private final VehicleGrid vehicleGrid;
    private final BreakdownGrid breakdownGrid;

    public VehicleView(CoreAPI coreAPI, JwtInfoService jwtInfoService) {
        super(coreAPI, jwtInfoService);
        vehicleGrid = new VehicleGrid(coreAPI);
        vehicleGrid.setSizeFull();

        breakdownGrid = new BreakdownGrid(coreAPI);
        breakdownGrid.setSizeFull();
        breakdownGrid.setVisible(false);

        addClassName("vehicle-view");

        mainContent.removeAll();
        mainContent.add(getToolbar(), vehicleGrid, breakdownGrid, mainFooter);
        mainContent.setFlexGrow(1, vehicleGrid);
    }

    private HorizontalLayout getToolbar() {
        var toolbar = new HorizontalLayout();

        Button list = new Button("Vehicles list");
        Button breakdown = new Button("Breakdowns");
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
        breakdownGrid.updateBreakdownGrid();
        breakdownGrid.setVisible(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!jwtInfoService.validToken()) {
            event.forwardTo(HomeView.class);
            UI.getCurrent().getPage().setLocation("/");
        }
    }
}
