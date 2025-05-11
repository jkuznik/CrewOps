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
import pl.crewops.view.component.VehicleGrid;
import pl.crewops.view.component.mainLayout.MainLayout;

@Slf4j
@Route(value = "vehicles")
@PageTitle("Vehicle view")
public class VehicleView extends MainLayout implements BeforeEnterObserver {
    private final VehicleGrid vehicleGrid;

    public VehicleView(CoreAPI coreAPI, JwtInfoService jwtInfoService) {
        super(coreAPI, jwtInfoService);
        vehicleGrid = new VehicleGrid(coreAPI);
        vehicleGrid.setSizeFull();

        addClassName("vehicle-view");

        mainContent.removeAll();
        mainContent.add(getToolbar(), vehicleGrid, mainFooter);
        mainContent.setFlexGrow(1, vehicleGrid);
    }

    private HorizontalLayout getToolbar() {
        var toolbar = new HorizontalLayout();

        Button list = new Button("Vehicles list");
        Button breakdown = new Button("Breakdown");
        list.addClickListener(event -> listEvent());
        breakdown.addClickListener(event -> breakdownEvent());

        toolbar.add(list, breakdown);

        return toolbar;
    }

    private void listEvent() {
        vehicleGrid.closeEditor();
        vehicleGrid.setVisible(true);
    }

    private void breakdownEvent() {
        vehicleGrid.setVisible(false);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!jwtInfoService.validToken()) {
            event.forwardTo(HomeView.class);
            UI.getCurrent().getPage().setLocation("/");
        }
    }
}
