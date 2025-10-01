package pl.crewops.view.layout;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
@CssImport("./styles/mainStyles/main-footer.css")
public class MainFooter extends Footer {

    public MainFooter() {
        addClassName("main-footer");

        HorizontalLayout mainFooterLayout = new HorizontalLayout();
        mainFooterLayout.addClassName("main-footer-layout");

        // Left column
        VerticalLayout leftSide = new VerticalLayout();
        leftSide.addClassName("main-footer-column");

        Span contactSpan = new Span(getTranslation("mainFooter.contact"));
        contactSpan.addClassName("main-footer-text");

        Anchor contactLink = new Anchor("/contact", getTranslation("mainFooter.contactLink"));
        contactLink.setTarget("_self");
        contactLink.addClassName("main-footer-link");

        leftSide.add(contactSpan, contactLink);

        // Right column
        VerticalLayout rightSide = new VerticalLayout();
        rightSide.addClassName("main-footer-column");

        Span policySpan = new Span(getTranslation("mainFooter.info"));
        policySpan.addClassName("main-footer-text");

        Anchor policyLink =
                new Anchor("https://devsmith.eu/private-policy.html", getTranslation("mainFooter.privacyPolicyLink"));
        policyLink.setTarget("_self");
        policyLink.addClassName("main-footer-link");

        rightSide.add(policySpan, policyLink);

        mainFooterLayout.add(leftSide, rightSide);
        add(mainFooterLayout);
    }
}
