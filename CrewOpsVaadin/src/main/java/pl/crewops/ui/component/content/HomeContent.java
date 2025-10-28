package pl.crewops.ui.component.content;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class HomeContent extends VerticalLayout {

    public HomeContent() {
        setWidthFull();
        setPadding(false);
        setSpacing(false);

        FlexLayout mainLayout = new FlexLayout();
        mainLayout.setWidthFull();
        mainLayout
                .getStyle()
                .set("gap", "20px")
                .set("align-items", "start")
                .set("padding", "20px")
                .set("overflow-x", "hidden")
                .set("box-sizing", "border-box")
                .set("flex-wrap", "wrap")
                .set("max-width", "100vw");

        Div imageContainer = new Div();
        imageContainer.setWidth("100%");
        imageContainer
                .getStyle()
                .set("display", "flex")
                .set("justify-content", "center")
                .set("align-items", "center")
                .set("overflow", "hidden")
                .set("flex", "1 1 300px");

        Image headerImage = new Image("images/home-view-pic.png", "home view");
        headerImage
                .getStyle()
                .set("max-width", "100%")
                .set("height", "auto")
                .set("object-fit", "contain")
                .set("display", "block");

        imageContainer.add(headerImage);

        // Text content
        VerticalLayout textLayout = new VerticalLayout();
        textLayout.setPadding(false);
        textLayout.setSpacing(true);
        textLayout.getStyle().set("flex", "1 1 300px").set("max-width", "100%").set("overflow-wrap", "break-word");

        H2 title = new H2(getTranslation("homeContent.title"));

        Paragraph intro = new Paragraph(getTranslation("homeContent.intro"));
        intro.getStyle().set("overflow-wrap", "break-word");

        UnorderedList features = new UnorderedList(
                new ListItem(getTranslation("homeContent.feature1")),
                new ListItem(getTranslation("homeContent.feature2")),
                new ListItem(getTranslation("homeContent.feature3")),
                new ListItem(getTranslation("homeContent.feature4")),
                new ListItem(getTranslation("homeContent.feature5")));

        Paragraph loginInfo = new Paragraph(getTranslation("homeContent.loginInfo"));
        loginInfo.getStyle().set("margin-top", "20px");

        Paragraph credentials = new Paragraph();
        credentials.getElement().setProperty("innerHTML", getTranslation("homeContent.credentials"));

        textLayout.add(title, intro, features);

        mainLayout.add(textLayout);

        add(mainLayout);
    }
}
