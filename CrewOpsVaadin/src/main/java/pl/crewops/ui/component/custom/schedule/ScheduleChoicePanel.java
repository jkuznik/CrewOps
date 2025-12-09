package pl.crewops.ui.component.custom.schedule;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import io.overcoded.vaadin.panel.Panel;
import io.overcoded.vaadin.panel.PanelType;

@CssImport("./styles/component/panel.css")
public class ScheduleChoicePanel extends Panel implements ClickNotifier<ScheduleChoicePanel> {

    private final VerticalLayout summary = new VerticalLayout();
    private final Span descriptionSpan = new Span();

    private final Component emptyContainer = new Div();

    private boolean onceClicked = false;

    public ScheduleChoicePanel(String description) {
        super(
                PanelType.PRIMARY.getConfig().toBuilder()
                        .collapsable(false)
                        .closeable(false)
                        .titleFontSize("var(--lumo-font-size-xl)")
                        .build(),
                "",
                new Div());

        descriptionSpan.setText(description);
        summary.add(descriptionSpan);
        setContent(summary);

        emptyContainer.setVisible(false);
        geminiGenerated();
    }

    private void geminiGenerated() {
        setClassName("custom-timeline-panel");

        // Ustawienie kursora i cienia, aby panel wyglądał jak element interaktywny
        getStyle().set("cursor", "pointer");
        getStyle().set("transition", "0.2s transform, 0.2s box-shadow"); // Płynne przejście
        getStyle().set("box-shadow", "var(--lumo-box-shadow-s)"); // Domyślny cień

        // Dodanie interakcji na najechanie/kliknięcie dla lepszej informacji zwrotnej
        getElement().addEventListener("mouseover", e -> getStyle()
                .set("box-shadow", "var(--lumo-box-shadow-xl)")
                .set("transform", "translateY(-2px)"));
        getElement().addEventListener("mouseout", e -> getStyle()
                .set("box-shadow", "var(--lumo-box-shadow-s)")
                .set("transform", "translateY(0)"));
        getElement().addEventListener("mousedown", e -> getStyle().set("transform", "scale(0.98)"));
        getElement().addEventListener("mouseup", e -> getStyle().set("transform", "translateY(-2px)"));
    }

    public void onClickModification(String width, String height) {
        onceClicked = !onceClicked;

        if (onceClicked) {
            setContent(emptyContainer);
            setWidth(width);
            setHeight(height);
        } else {
            setContent(summary);
            setSizeFull();
        }
    }

    public void setDescription(String description) {
        descriptionSpan.setText(description);
    }
}
