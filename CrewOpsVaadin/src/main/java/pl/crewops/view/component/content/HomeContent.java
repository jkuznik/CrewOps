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

        Span systemDescription = new Span(
                "System został zaprojektowany w oparciu o wielomodułową architekturę, umożliwiającą logiczne rozdzielenie odpowiedzialności oraz łatwiejsze zarządzanie kodem. Każdy moduł odpowiada za wyraźnie zdefiniowany obszar funkcjonalny, dzięki czemu możliwa jest niezależna praca nad różnymi częściami systemu oraz ich łatwe testowanie i utrzymywanie. Podejście to wspiera skalowalność i rozwój projektu w dłuższej perspektywie.");
        systemDescription.getStyle().set("display", "block").set("margin-bottom", "300px");

        Span vaadinDescription = new Span(
                "W warstwie prezentacji zastosowano podejście komponentowe oparte na frameworku Vaadin. Kluczowe widoki i funkcje użytkownika są budowane z użyciem reużywalnych komponentów, które zawierają zarówno logikę interfejsu, jak i spójny wygląd. Takie podejście ułatwia utrzymanie jednolitego stylu graficznego w całej aplikacji oraz przyspiesza implementację kolejnych funkcjonalności.");
        vaadinDescription.getStyle().set("margin-top", "auto");

        textContainer.add(systemDescription, vaadinDescription);

        Image screen2 = createZoomableImage("images/vaadin-components-pic.png", "vaadin");
        Div container2 = new Div(screen2);
        container2
                .getStyle()
                .set("display", "inline-block")
                .set("vertical-align", "top")
                .set("margin-left", "10px");
        container2.getElement().getStyle().set("flex-grow", "1");

        topGallery.add(container1, textContainer, container2);

        var middleGallery = new HorizontalLayout();
        middleGallery.setSpacing(true);
        middleGallery.setPadding(true);
        middleGallery.getStyle().set("overflow-x", "auto").set("white-space", "nowrap");

        Image screen3 = createZoomableImage("images/test-pic.png", "Screen 3");

        middleGallery.add(screen3);

        var middleGallery2 = new HorizontalLayout();

        middleGallery2.setSpacing(true);
        middleGallery2.setPadding(true);
        middleGallery2.getStyle().set("overflow-x", "auto").set("white-space", "nowrap");

        Image screen4 = createZoomableImage("images/dependencies-pic.png", "Screen 4");

        middleGallery2.add(screen4);

        var bottomGallery = new HorizontalLayout();
        bottomGallery.setSpacing(true);
        bottomGallery.setPadding(true);
        bottomGallery.getStyle().set("overflow-x", "auto").set("white-space", "nowrap");

        Image screen5 = createZoomableImage("images/cache-pic.png", "Screen 5");

        bottomGallery.add(screen5);

        add(topGallery, middleGallery, middleGallery2, bottomGallery);
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
