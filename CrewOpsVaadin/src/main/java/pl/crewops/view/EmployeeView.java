package pl.crewops.view;

import static pl.crewops.model.auth.RoleType.*;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.Set;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.auth.RoleGrantedAuthority;
import pl.crewops.security.custom.UserPrincipal;
import pl.crewops.security.jwt.JwtServiceVaadin;
import pl.crewops.view.component.grid.EmployeeGrid;
import pl.crewops.view.component.grid.QualificationGrid;
import pl.crewops.view.layout.MainLayout;

@Route(value = "employees")
@PageTitle("Employee management")
public class EmployeeView extends MainLayout implements BeforeEnterObserver {
    private final EmployeeGrid employeeGrid;
    private final QualificationGrid qualificationGrid;

    public EmployeeView(CoreAPI coreAPI, JwtServiceVaadin jwtService) {
        super(coreAPI, jwtService);

        employeeGrid = new EmployeeGrid(coreAPI);
        qualificationGrid = new QualificationGrid(coreAPI);
        employeeGrid.setQualificationGrid(qualificationGrid);
        qualificationGrid.setEmployeeGrid(employeeGrid);

        qualificationGrid.setVisible(false);
        addClassName("employee-view");

        mainContent.removeAll();
        mainContent.add(getToolbar(), employeeGrid, qualificationGrid, mainFooter);
        mainContent.setFlexGrow(1, employeeGrid);
        mainContent.setFlexGrow(1, qualificationGrid);
    }

    private HorizontalLayout getToolbar() {
        var toolbar = new HorizontalLayout();

        Button employeeList = new Button(getTranslation("employeeView.employeeList"));
        Button qualifications = new Button(getTranslation("employeeView.qualifications"));
        employeeList.addClickListener(event -> displayEmployeeGrid());
        qualifications.addClickListener(event -> displayQualificationGrid());

        toolbar.add(employeeList, qualifications);

        return toolbar;
    }

    private void displayEmployeeGrid() {
        qualificationGrid.setVisible(false);

        employeeGrid.closeEditor();
        employeeGrid.setVisible(true);
    }

    private void displayQualificationGrid() {
        employeeGrid.setVisible(false);

        qualificationGrid.closeEditor();
        qualificationGrid.setVisible(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isAuthenticated(authentication)
                && authentication.getPrincipal() instanceof UserPrincipal principal
                && tokenIsValid(principal)) {

            Set<RoleGrantedAuthority> roleGrantedAuthorities = jwtService.extractAuthorities(principal.getToken());
            if (requestHasRequiredPermission(roleGrantedAuthorities)) {
                return;
            }
        }

        event.forwardTo(HomeView.class);
        UI.getCurrent().getPage().setLocation("/");
    }

    private boolean requestHasRequiredPermission(Set<RoleGrantedAuthority> roleGrantedAuthorities) {
        return roleGrantedAuthorities.contains(new RoleGrantedAuthority(MANAGER))
                || roleGrantedAuthorities.contains(new RoleGrantedAuthority(COMPANY_ADMIN))
                || roleGrantedAuthorities.contains(new RoleGrantedAuthority(SYSTEM_ADMIN));
    }

    private boolean tokenIsValid(UserPrincipal principal) {
        return jwtService.validToken(principal.getToken());
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated();
    }
}
