package pl.crewops.ui.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import lombok.extern.slf4j.Slf4j;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.ui.component.grid.BreakdownGrid;
import pl.crewops.ui.component.grid.MachineGrid;
import pl.crewops.ui.component.notification.FailNotification;
import pl.crewops.ui.view.layout.MainLayout;
import pl.crewops.util.AuthenticationResolver;

@Slf4j
@Route(value = "machines")
@PageTitle("Machine view")
public class MachineView extends MainLayout implements BeforeEnterObserver {

    private MachineGrid machineGrid;
    private BreakdownGrid breakdownGrid;

    public MachineView(CoreAPI coreAPI, AuthenticationResolver authenticationResolver) {
        super(coreAPI, authenticationResolver);
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
        Tab listTab = new Tab(getTranslation("machineView.machineList"));
        Tab breakdownTab = new Tab(getTranslation("machineView.breakdowns"));
        Tab documentationTab = new Tab("Dokumentacja");

        Tabs tabs = new Tabs(listTab, breakdownTab, documentationTab);

        tabs.setSelectedTab(listTab);

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
