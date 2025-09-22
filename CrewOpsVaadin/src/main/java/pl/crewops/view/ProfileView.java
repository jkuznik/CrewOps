package pl.crewops.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.NoSuchElementException;
import pl.crewops.component.form.ProfileForm;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.ProfileFormModel;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.security.jwt.JwtServiceVaadin;
import pl.crewops.util.AuthenticationResolver;
import pl.crewops.view.layout.MainLayout;

@Route(value = "profile")
@PageTitle("Profile configuration")
public class ProfileView extends MainLayout implements BeforeEnterObserver {

    public ProfileView(CoreAPI coreAPI, JwtServiceVaadin jwtService, AuthenticationResolver authenticationResolver) {
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
        addClassName("profile-view");

        var principal = authenticationResolver.getPrincipal();
        EmployeeDTO employeeDTO = null;
        try {
            employeeDTO = coreAPI.getEmployeeById(principal.getEmployeeId())
                    // todo custom exception
                    .orElseThrow(NoSuchElementException::new);

        } catch (NotAuthenticatedException e) {
            // todo implement logic
        }

        var profileFormModel = ProfileFormModel.create(principal, employeeDTO);

        final ProfileForm profileForm = new ProfileForm(profileFormModel);

        FlexLayout container = new FlexLayout(profileForm);
        container.setSizeFull();
        container.setJustifyContentMode(FlexLayout.JustifyContentMode.CENTER); // center horizontally
        container.setAlignItems(FlexLayout.Alignment.CENTER);

        mainContent.add(container);
    }
}
