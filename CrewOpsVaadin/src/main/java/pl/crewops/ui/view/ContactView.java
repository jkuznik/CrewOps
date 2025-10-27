package pl.crewops.ui.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtServiceVaadin;
import pl.crewops.ui.component.notification.FailNotification;
import pl.crewops.ui.view.layout.MainLayout;
import pl.crewops.util.AuthenticationResolver;

@Route(value = "contact")
@PageTitle("Contact - Crew Ops")
public class ContactView extends MainLayout implements BeforeEnterObserver {

    public ContactView(CoreAPI coreAPI, JwtServiceVaadin jwtService, AuthenticationResolver authenticationResolver) {
        super(coreAPI, jwtService, authenticationResolver);
        addClassName("contact-view");
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
        VerticalLayout layout = new VerticalLayout();
        layout.setId("view-content");
        layout.setWidthFull();
        layout.setDefaultHorizontalComponentAlignment(VerticalLayout.Alignment.CENTER);
        layout.setPadding(true);
        layout.setSpacing(true);
        layout.getStyle().set("overflow", "auto");

        H3 title = new H3("Contact Us");
        layout.add(title);

        // Form container
        VerticalLayout formLayout = new VerticalLayout();
        formLayout.setWidth("400px"); // fixed width to avoid stretching
        formLayout.setPadding(false);
        formLayout.setSpacing(true);

        // Email field
        EmailField emailField = new EmailField("Your Email");
        emailField.setRequiredIndicatorVisible(true);
        emailField.setPlaceholder("you@example.com");
        emailField.setWidthFull();

        // Message field
        TextArea messageField = new TextArea("Message");
        messageField.setPlaceholder("Write your message here...");
        messageField.setWidthFull();
        messageField.setHeight("150px"); // compact height

        // Submit button
        Button submitButton = new Button("Send");
        submitButton.setWidthFull();
        submitButton.addClickListener(e -> {
            if (emailField.isEmpty()) {
                emailField.setErrorMessage("Email is required");
            } else {
                emailField.setErrorMessage(null);
                // Handle backend logic
            }
        });

        formLayout.add(emailField, messageField, submitButton);

        // LinkedIn link below the form
        HorizontalLayout linkLayout = new HorizontalLayout();
        linkLayout.setSpacing(true);
        linkLayout.setDefaultVerticalComponentAlignment(VerticalLayout.Alignment.CENTER);

        Anchor linkedinLink = new Anchor("https://www.linkedin.com/in/janusz-kuźnik", "LinkedIn");
        linkedinLink.setTarget("_blank");

        linkLayout.add(new Span("Find me on:"), linkedinLink);

        // Add to main layout
        layout.add(formLayout, linkLayout);

        mainContent.add(layout, mainFooter);
        mainContent.setFlexGrow(1, layout);
    }
}
