package pl.crewops.view.component.mainLayout;

import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;

@SpringComponent
public class MainFooter extends Footer {

    public MainFooter() {
        setId("footer");
        getStyle()
                .set("width", "100%")
                .set("text-align", "center")
                .set("padding", "10px")
                .set("background-color", "#f1f1f1");

        var horizontalLayout = new HorizontalLayout();

        horizontalLayout.setSpacing(true);
        horizontalLayout.setWidthFull();
        horizontalLayout.setHeight("100%");

        var leftSide = new VerticalLayout();

        leftSide.setSpacing(true);
        leftSide.setWidth("100%");

        Span contactSpan = new Span("Contact");
        contactSpan
                .getStyle()
                .set("font-size", "12px")
                .set("color", "#888")
                .set("margin-top", "auto")
                .set("text-align", "center");

        leftSide.add(contactSpan);

        var rightSide = new VerticalLayout();

        rightSide.setSpacing(true);
        rightSide.setWidth("100%");

        Span infoSpan = new Span("Info");
        infoSpan.getStyle()
                .set("font-size", "12px")
                .set("color", "#888")
                .set("margin-top", "auto")
                .set("text-align", "center");

        rightSide.add(infoSpan);

        horizontalLayout.add(leftSide, rightSide);

        add(horizontalLayout);
    }
}
