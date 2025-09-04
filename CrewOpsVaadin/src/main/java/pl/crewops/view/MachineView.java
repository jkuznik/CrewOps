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
import pl.crewops.util.AuthenticationResolver;
import pl.crewops.view.layout.MainLayout;

@Slf4j
@Route(value = "machines")
@PageTitle("Machine view")
public class MachineView extends MainLayout implements BeforeEnterObserver {
    private MachineGrid machineGrid;
    private BreakdownGrid breakdownGrid;

    public MachineView(CoreAPI coreAPI, JwtServiceVaadin jwtService, AuthenticationResolver authenticationResolver) {
        super(coreAPI, jwtService, authenticationResolver);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticationResolver.principalIsAuthenticated()) {
            buildContent();
        } else {
            event.forwardTo(HomeView.class);
            UI.getCurrent().getPage().setLocation("/");
        }
    }

    private void buildContent() {
        addClassName("machine-view");

        breakdownGrid = new BreakdownGrid(coreAPI, authenticationResolver);
        breakdownGrid.setSizeFull();
        breakdownGrid.setVisible(false);

        machineGrid = new MachineGrid(coreAPI, breakdownGrid, authenticationResolver);
        machineGrid.setSizeFull();

        mainContent.removeAll();

        // TODO: temporary remove footer from this view, there is te way to add it again
        //        mainContent.add(getToolbar(), machineGrid, breakdownGrid, mainFooter);
        mainContent.add(getToolbar(), machineGrid, breakdownGrid);
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
        breakdownGrid.setTypeFilter("");
        breakdownGrid.updateBreakdownGrid();
        breakdownGrid.setVisible(true);
    }
}
