package pl.crewops.view.component.content;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class InfoContent extends VerticalLayout {

    public InfoContent() {
        Div contentWrapper = new Div();
        contentWrapper
                .getStyle()
                .set("max-width", "1600px")
                .set("margin", "0 auto")
                .set("width", "100%");

        var topGallery = topGallery();
        topGallery.getStyle().set("margin-bottom", "60px");

        var middleGallery = middleGallery();
        middleGallery.getStyle().set("margin-bottom", "60px");

        var bottomGallery = bottomGallery();
        bottomGallery.getStyle().set("margin-bottom", "60px");

        var finalGallery = finalGallery();
        finalGallery.getStyle().set("margin-top", "60px");

        contentWrapper.add(noticeGallery(), topGallery, middleGallery, bottomGallery, finalGallery);
        add(contentWrapper);
    }

    private VerticalLayout noticeGallery() {
        VerticalLayout noticeLayout = new VerticalLayout();
        noticeLayout.setWidthFull();
        noticeLayout.setSpacing(false);
        noticeLayout.setPadding(true);

        Span heading = new Span(getTranslation("infoContent.limitedFunctionalityNotice"));

        noticeLayout.add(heading);
        return noticeLayout;
    }

    private VerticalLayout finalGallery() {
        VerticalLayout finalGallery = new VerticalLayout();
        finalGallery.setPadding(false);
        finalGallery.setSpacing(true);
        finalGallery.setWidthFull();

        Span gitDescription = new Span(getTranslation("infoContent.gitDescription"));
        gitDescription.getStyle().set("font-size", "21px").set("margin-bottom", "20px");

        VerticalLayout imagesColumn = new VerticalLayout();
        imagesColumn.setSpacing(true);
        imagesColumn.setPadding(false);
        imagesColumn.setWidthFull();

        Image img1 = createZoomableImage("images/todo-list-pic.png", "GitHub todo list");
        Image img2 = createZoomableImage("images/open-issues-pic.png", "GitHub open issues");
        Image img3 = createZoomableImage("images/done-issues-pic.png", "GitHub done issues");

        // Ustawiamy, żeby zdjęcia były dostosowane do szerokości rodzica
        img1.setWidth("100%");
        img2.setWidth("100%");
        img3.setWidth("100%");

        imagesColumn.add(img1, img2, img3);
        finalGallery.add(gitDescription, imagesColumn);

        return finalGallery;
    }

    private HorizontalLayout bottomGallery() {
        HorizontalLayout bottomGallery = new HorizontalLayout();
        bottomGallery.setSpacing(false);
        bottomGallery.setPadding(false);
        bottomGallery.setWidthFull();

        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setPadding(false);
        wrapper.setSpacing(false);
        wrapper.setWidthFull();

        Span moduleDescription = new Span(getTranslation("infoContent.moduleDescription"));
        moduleDescription.getStyle().set("margin-bottom", "20px").set("font-size", "20px");

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
        wrapper.add(moduleDescription, scrollableImageWrapper);
        bottomGallery.add(wrapper);

        return bottomGallery;
    }

    private VerticalLayout middleGallery() {
        VerticalLayout middleGallery = new VerticalLayout();
        middleGallery.setSpacing(false);
        middleGallery.setPadding(false);
        middleGallery.setWidthFull();

        Span testsDescription = new Span(getTranslation("infoContent.testsDescription"));
        testsDescription
                .getStyle()
                .set("font-size", "20px")
                .set("margin-bottom", "15px")
                .set("display", "block");

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
        middleGallery.add(testsDescription, scrollableImageWrapper);

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

        Span systemDescription = new Span(getTranslation("infoContent.systemDescription"));
        systemDescription
                .getStyle()
                .set("display", "block")
                .set("margin-bottom", "35px")
                .set("font-size", "21px");

        Span vaadinDescription = new Span(getTranslation("infoContent.vaadinDescription"));
        vaadinDescription.getStyle().set("margin-bottom", "35px").set("font-size", "21px");

        Span additionalDescription = new Span(getTranslation("infoContent.additionalDescription"));
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
