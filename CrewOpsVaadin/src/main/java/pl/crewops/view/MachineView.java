package pl.crewops.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import lombok.extern.slf4j.Slf4j;
import pl.crewops.component.grid.BreakdownGrid;
import pl.crewops.component.grid.MachineGrid;
import pl.crewops.component.notification.FailNotification;
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
            try {
                mainContent.removeAll();
                listeners.forEach(Registration::remove);
                buildContent();
            } catch (Exception e) {
                new FailNotification(getTranslation("failNotification"));
            }
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

        mainContent.add(getToolbar(), machineGrid, breakdownGrid);
        mainContent.setFlexGrow(1, machineGrid);
    }

    private Tabs getToolbar() {
        // Tworzenie zakładek
        Tab listTab = new Tab(getTranslation("machineView.machineList"));
        Tab breakdownTab = new Tab(getTranslation("machineView.breakdowns"));

        Tabs tabs = new Tabs(listTab, breakdownTab);

        // Domyślnie wybieramy pierwszą zakładkę i wyświetlamy machineGrid
        tabs.setSelectedTab(listTab);

        // Logika przełączania widoków na podstawie wybranej zakładki
        tabs.addSelectedChangeListener(event -> {
            if (event.getSelectedTab().equals(listTab)) {
                displayMachineGrid();
            } else if (event.getSelectedTab().equals(breakdownTab)) {
                displayBreakdownGrid();
            }
        });

        return tabs;
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
