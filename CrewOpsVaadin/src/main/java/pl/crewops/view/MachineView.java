package pl.crewops.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import pl.crewops.component.grid.BreakdownGrid;
import pl.crewops.component.grid.MachineGrid;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtServiceVaadin;
import pl.crewops.util.RoleResolver;
import pl.crewops.view.layout.MainLayout;

@Slf4j
@Route(value = "machines")
@PageTitle("Machine view")
public class MachineView extends MainLayout implements BeforeEnterObserver {
    private final MachineGrid machineGrid;
    private final BreakdownGrid breakdownGrid;

    public MachineView(CoreAPI coreAPI, JwtServiceVaadin jwtService, RoleResolver roleResolver) {
        super(coreAPI, jwtService, roleResolver);

        breakdownGrid = new BreakdownGrid(coreAPI, roleResolver);
        breakdownGrid.setSizeFull();
        breakdownGrid.setVisible(false);

        machineGrid = new MachineGrid(coreAPI, breakdownGrid, roleResolver);
        machineGrid.setSizeFull();

        addClassName("machine-view");

        mainContent.removeAll();
        mainContent.add(getToolbar(), machineGrid, breakdownGrid, mainFooter);
        mainContent.setFlexGrow(1, machineGrid);
    }

    private HorizontalLayout getToolbar() {
        var toolbar = new HorizontalLayout();

        Button list = new Button(getTranslation("machineView.machineList"));
        Button breakdown = new Button(getTranslation("machineView.breakdowns"));
        list.addClickListener(event -> displayMachineGrid());
        breakdown.addClickListener(event -> displayBreakdownGrid());

        toolbar.add(list, breakdown);

        return toolbar;
    }

    private void displayMachineGrid() {
        breakdownGrid.setVisible(false);

        machineGrid.closeEditor();
        machineGrid.updateMachineGrid();
        machineGrid.setVisible(true);
    }

    private void displayBreakdownGrid() {
        machineGrid.setVisible(false);

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
