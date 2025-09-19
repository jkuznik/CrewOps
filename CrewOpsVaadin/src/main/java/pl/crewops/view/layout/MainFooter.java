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

        VerticalLayout leftSide = new VerticalLayout();
        leftSide.addClassName("main-footer-column");

        Span contactSpan = new Span(getTranslation("mainFooter.contact"));
        contactSpan.addClassName("main-footer-text");
        leftSide.add(contactSpan);

        Anchor githubLink = new Anchor("https://github.com/jkuznik", "GitHub");
        githubLink.setTarget("_blank");
        githubLink.addClassName("main-footer-link");

        Anchor emailLink = new Anchor("mailto:janusz.kuznik@devsmith.eu", "janusz.kuznik@devsmith.eu");
        emailLink.addClassName("main-footer-link");

        Anchor linkedinLink = new Anchor("https://www.linkedin.com/in/janusz-kuźnik", "LinkedIn");
        linkedinLink.setTarget("_blank");
        linkedinLink.addClassName("main-footer-link");

        leftSide.add(emailLink, githubLink, linkedinLink);

        VerticalLayout rightSide = new VerticalLayout();
        rightSide.addClassName("main-footer-column");

        Span infoSpan = new Span(getTranslation("mainFooter.info"));
        infoSpan.addClassName("main-footer-text");

        Anchor infoLink = new Anchor("/info", getTranslation("mainFooter.infoLink"));
        infoLink.setTarget("_self");
        infoLink.addClassName("main-footer-link");

        rightSide.add(infoSpan, infoLink);

        mainFooterLayout.add(leftSide, rightSide);
        add(mainFooterLayout);
    }
}
