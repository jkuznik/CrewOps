package pl.crewops.ui.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.ui.component.notification.FailNotification;
import pl.crewops.ui.view.layout.MainLayout;
import pl.crewops.util.AuthenticationResolver;

@Route("safety")
@PageTitle("HSE")
public class HSEView extends MainLayout implements BeforeEnterObserver {

    public HSEView(CoreAPI coreAPI, AuthenticationResolver authenticationResolver) {
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
        final Button safetyRaport = new Button();
        safetyRaport.setText(getTranslation("dailyActivityForm.safetyRaport"));
        safetyRaport.setIcon(new Icon(VaadinIcon.SHIELD));
        applyButtonStyles(safetyRaport);

        final Button readSafetyRaport = new Button();
        readSafetyRaport.setText(getTranslation("dailyActivityForm.readSafetyRaport"));
        readSafetyRaport.setIcon(new Icon(VaadinIcon.WARNING));
        readSafetyRaport.addClassName("pulse-red-animation");
        applyButtonStyles(readSafetyRaport);

        mainContent.add(safetyRaport, readSafetyRaport);
    }

    private void applyButtonStyles(Button button) {
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        button.setWidthFull();
    }
}
