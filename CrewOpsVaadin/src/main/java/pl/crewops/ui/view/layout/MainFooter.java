package pl.crewops.ui.view.layout;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import pl.crewops.ui.component.notification.FailNotification;

@SpringComponent
@UIScope
@CssImport("./styles/mainStyles/main-footer.css")
public class MainFooter extends Footer {

    private static final String DOC_DOMAIN = "https://devsmith.eu";
    private static final String POLICY = "/private-policy/";
    private static final String TERMS = "/terms-of-service/";

    private static final String FALLBACK_LANGUAGE = "en";

    public MainFooter() {
        addClassName("main-footer");

        try {

            HorizontalLayout mainFooterLayout = new HorizontalLayout();
            mainFooterLayout.addClassName("main-footer-layout");

            String currentLanguage = getLocale().getLanguage();

            String documentLanguage = currentLanguage.equals("pl") ? "pl" : FALLBACK_LANGUAGE;

            // --- Left column (Contact) ---
            VerticalLayout leftSide = new VerticalLayout();
            leftSide.addClassName("main-footer-column");

            Span contactSpan = new Span(getTranslation("mainFooter.contact"));
            contactSpan.addClassName("main-footer-text");

            Anchor contactLink = new Anchor("/contact", getTranslation("mainFooter.contactLink"));
            contactLink.setTarget("_self");
            contactLink.addClassName("main-footer-link");

            leftSide.add(contactSpan, contactLink);

            // --- Right column (Policy & Terms) ---
            VerticalLayout rightSide = new VerticalLayout();
            rightSide.addClassName("main-footer-column");

            Span policySpan = new Span(getTranslation("mainFooter.info"));
            policySpan.addClassName("main-footer-text");

            // Użycie documentLanguage do budowy linków
            String privacyPolicyHref = DOC_DOMAIN + "/" + documentLanguage + POLICY;
            Anchor policyLink = new Anchor(privacyPolicyHref, getTranslation("mainFooter.privacyPolicyLink"));
            policyLink.setTarget("_blank");
            policyLink.addClassName("main-footer-link");

            String termsOfServiceHref = DOC_DOMAIN + "/" + documentLanguage + TERMS;
            Anchor termsLink = new Anchor(termsOfServiceHref, getTranslation("mainFooter.termsOfServiceLink"));
            termsLink.setTarget("_blank");
            termsLink.addClassName("main-footer-link");

            rightSide.add(policySpan, policyLink, termsLink);

            mainFooterLayout.add(leftSide, rightSide);
            add(mainFooterLayout);
        } catch (Exception e) {
            new FailNotification(getTranslation("dailyView.failNotification"));
        }
    }
}
