package pl.crewops.view.component.content;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class HomeContent extends VerticalLayout {

    public HomeContent() {
        Div contentWrapper = new Div();
        contentWrapper
                .getStyle()
                .set("max-width", "1800px")
                .set("margin", "0 auto")
                .set("width", "100%");

        var topGallery = topGallery();
        var middleGallery = middleGallery();
        middleGallery.getStyle().set("margin-bottom", "40px"); // <-- odstęp 40px

        var bottomGallery = bottomGallery();

        contentWrapper.add(topGallery, middleGallery, bottomGallery);
        add(contentWrapper);
    }

    private HorizontalLayout bottomGallery() {
        var bottomGallery = new HorizontalLayout();
        bottomGallery.setSpacing(false);
        bottomGallery.setPadding(false);
        bottomGallery.setWidthFull();

        Div scrollableImageWrapper = new Div();
        scrollableImageWrapper
                .getStyle()
                .set("width", "100%")
                .set("overflow", "auto")
                .set("display", "block")
                .set("padding", "10px 0");

        Image screen4 = createZoomableImage("images/dependencies-pic.png", "Screen 4");
        screen4.getStyle()
                .set("transform", "scale(1.5)")
                .set("transform-origin", "top left")
                .set("display", "block");

        scrollableImageWrapper.add(screen4);
        bottomGallery.add(scrollableImageWrapper);

        return bottomGallery;
    }

    private HorizontalLayout middleGallery() {
        HorizontalLayout middleGallery = new HorizontalLayout();
        middleGallery.setSpacing(false);
        middleGallery.setPadding(false);
        middleGallery.setWidthFull();

        Div scrollableImageWrapper = new Div();
        scrollableImageWrapper
                .getStyle()
                .set("max-width", "100%")
                .set("overflow", "auto")
                .set("display", "block");

        Image screen3 = createZoomableImage("images/test-pic.png", "Screen 3");
        screen3.getStyle()
                .set("transform", "scale(1.3)")
                .set("transform-origin", "top left")
                .set("display", "block");

        scrollableImageWrapper.add(screen3);
        middleGallery.add(scrollableImageWrapper);

        return middleGallery;
    }

    private HorizontalLayout topGallery() {
        HorizontalLayout topGallery = new HorizontalLayout();
        topGallery.setWidthFull();
        topGallery.setPadding(false);
        topGallery.setSpacing(false);
        topGallery
                .getStyle()
                .set("overflow-x", "auto")
                .set("white-space", "nowrap")
                .set("display", "flex")
                .set("align-items", "flex-start");

        Image screen1 = createZoomableImage("images/structure-pic.png", "structure");
        Div container1 = new Div(screen1);
        container1
                .getStyle()
                .set("display", "inline-block")
                .set("margin-right", "10px")
                .set("vertical-align", "top");
        container1.getElement().getStyle().set("flex-grow", "1");

        Div textContainer = new Div();
        textContainer
                .getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("justify-content", "flex-start")
                .set("white-space", "normal")
                .set("max-width", "100%")
                .set("text-align", "left")
                .set("vertical-align", "top")
                .set("margin-right", "10px")
                .set("margin-left", "10px");
        textContainer.getElement().getStyle().set("flex-grow", "2");

        Span systemDescription = new Span(getTranslation("homeContent.systemDescription"));
        systemDescription
                .getStyle()
                .set("display", "block")
                .set("margin-bottom", "35px")
                .set("font-size", "21px");

        Span vaadinDescription = new Span(getTranslation("homeContent.vaadinDescription"));
        vaadinDescription.getStyle().set("margin-bottom", "35px").set("font-size", "21px");

        Span additionalDescription = new Span(getTranslation("homeContent.additionalDescription"));
        additionalDescription
                .getStyle()
                .set("display", "block")
                .set("margin-bottom", "35px")
                .set("font-size", "21px");

        Image screen3 = createZoomableImage("images/cache-pic.png", "Cache");
        screen3.getStyle()
                .set("transform-origin", "top left")
                .set("display", "block")
                .set("margin-top", "20px");

        textContainer.add(systemDescription, vaadinDescription, additionalDescription, screen3);

        Image screen2 = createZoomableImage("images/vaadin-components-pic.png", "vaadin");
        Div container2 = new Div(screen2);
        container2
                .getStyle()
                .set("display", "inline-block")
                .set("vertical-align", "top")
                .set("margin-left", "10px");
        container2.getElement().getStyle().set("flex-grow", "1");

        topGallery.add(container1, textContainer, container2);
        return topGallery;
    }

    private Image createZoomableImage(String src, String alt) {
        Image img = new Image(src, alt);

        img.addClickListener((ClickEvent<Image> event) -> {
            Dialog dialog = new Dialog();
            dialog.setModal(true);
            dialog.setDraggable(false);
            dialog.setResizable(false);
            dialog.setCloseOnEsc(true);
            dialog.setCloseOnOutsideClick(true);

            Image zoomedImg = new Image(src, alt);
            zoomedImg.setMaxWidth("90vw");
            zoomedImg.setMaxHeight("90vh");

            dialog.add(zoomedImg);
            dialog.open();
        });

        img.getStyle().set("cursor", "pointer");

        return img;
    }
}
