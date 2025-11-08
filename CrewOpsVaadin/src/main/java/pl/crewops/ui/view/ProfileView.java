package pl.crewops.ui.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import java.util.Optional;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.ProfileFormModel;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.ui.component.form.ProfileForm;
import pl.crewops.ui.component.notification.FailNotification;
import pl.crewops.ui.component.notification.NotAuthenticatedNotification;
import pl.crewops.ui.view.layout.MainLayout;
import pl.crewops.util.AuthenticationResolver;

@Route(value = "profile")
@PageTitle("Profile configuration")
public class ProfileView extends MainLayout implements BeforeEnterObserver {

    public ProfileView(CoreAPI coreAPI, AuthenticationResolver authenticationResolver) {
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
        addClassName("profile-view");

        var principal = authenticationResolver.getPrincipal();
        Optional<EmployeeDTO> employeeDTO;
        try {
            employeeDTO = coreAPI.getEmployeeById(principal.getEmployeeId());
            employeeDTO.ifPresent(employee -> {
                var profileFormModel = ProfileFormModel.create(principal, employee);

                final ProfileForm profileForm = new ProfileForm(profileFormModel, coreAPI);

                FlexLayout container = new FlexLayout(profileForm);
                container.setSizeFull();

                mainContent.add(container);
            });

            if (employeeDTO.isEmpty()) {
                new FailNotification(getTranslation("profileForm.failedUpdate"));
            }

        } catch (NotAuthenticatedException e) {
            new NotAuthenticatedNotification(e.getMessage());
        }
    }
}
