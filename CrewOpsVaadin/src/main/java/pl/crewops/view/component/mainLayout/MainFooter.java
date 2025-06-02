package pl.crewops.view.component.mainLayout;

import com.vaadin.flow.component.dependency.CssImport;
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

        VerticalLayout rightSide = new VerticalLayout();
        rightSide.addClassName("main-footer-column");

        Span infoSpan = new Span(getTranslation("mainFooter.info"));
        infoSpan.addClassName("main-footer-text");
        rightSide.add(infoSpan);

        mainFooterLayout.add(leftSide, rightSide);
        add(mainFooterLayout);
    }
}
