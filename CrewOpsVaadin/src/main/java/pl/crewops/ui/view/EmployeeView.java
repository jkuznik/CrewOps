package pl.crewops.ui.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.ui.component.grid.EmployeeGrid;
import pl.crewops.ui.component.grid.JobPositionGrid;
import pl.crewops.ui.component.grid.QualificationGrid;
import pl.crewops.ui.component.notification.FailNotification;
import pl.crewops.ui.view.layout.MainLayout;
import pl.crewops.util.AuthenticationResolver;

@Route(value = "employees")
@PageTitle("Employee management")
public class EmployeeView extends MainLayout implements BeforeEnterObserver {
    private EmployeeGrid employeeGrid;
    private QualificationGrid qualificationGrid;
    private JobPositionGrid jobPositionGrid;

    public EmployeeView(CoreAPI coreAPI, AuthenticationResolver authenticationResolver) {
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

    private Tabs getToolbar() {
        // Definicja zakładek
        Tab employeeListTab = new Tab(getTranslation("employeeView.employeeList"));
        Tab qualificationsTab = new Tab(getTranslation("employeeView.qualifications"));
        Tab documentationTab = new Tab("Medycyna pracy");
        Tab contractTab = new Tab("Dokumentacja");
        Tab jobPositionsTab = new Tab(getTranslation("employeeView.jobPositions"));

        Tabs tabs = new Tabs(employeeListTab, qualificationsTab, documentationTab, contractTab, jobPositionsTab);
        tabs.setFlexGrowForEnclosedTabs(2);

        // Domyślnie wybieramy pierwszą zakładkę i pokazujemy listę pracowników
        tabs.setSelectedTab(employeeListTab);

        // KLUCZOWA LOGIKA: Przełączanie widoków
        tabs.addSelectedChangeListener(event -> {
            Tab selectedTab = event.getSelectedTab();

            if (selectedTab.equals(employeeListTab)) {
                displayEmployeeGrid();
            } else if (selectedTab.equals(qualificationsTab)) {
                displayQualificationGrid();
            } else if (selectedTab.equals(jobPositionsTab)) {
                displayJobPositionGrid();
            }
        });

        return tabs;
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

        jobPositionGrid.setVisible(true);
    }
}
