package pl.crewops.ui.component.custom.schedule;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import elemental.json.Json;
import elemental.json.JsonObject;
import java.time.LocalDate;
import java.util.UUID;

@CssImport("./styles/component/schedule-content-component.css")
class ScheduleCalendarComponent extends VerticalLayout {

    private final VerticalLayout contentContainer;

    private final Button toggleVisibilityButton = new Button(VaadinIcon.ANGLE_DOWN.create());

    private final Calendar calendar = new Calendar();
    private final TempTemplate demoTemplate = new TempTemplate("Standardowy Szablon", 4, "#2ecc71");

    private boolean isContentVisible = false;

    public ScheduleCalendarComponent() {
        this.contentContainer = new VerticalLayout();
        addClassName("component-content-border");
        setSizeFull();
        setPadding(true);

        // 1. Tworzymy element "przeciągalny" (Twoja paleta szablonów)
        Div templateItem = createDraggableTemplate(demoTemplate);

        // 2. Nasłuchujemy na moment upuszczenia na kalendarz
        calendar.getElement()
                .addEventListener("template-dropped", e -> {
                    JsonObject eventDetail = e.getEventData().getObject("event.detail");

                    if (eventDetail != null) {
                        String dateStr = eventDetail.getString("date");
                        JsonObject templateJson = eventDetail.getObject("template");

                        TempTemplate droppedTemplate = new TempTemplate(
                                templateJson.getString("title"),
                                (int) templateJson.getNumber("duration"),
                                templateJson.getString("color"));

                        handleTemplateDrop(dateStr, droppedTemplate);
                    }
                })
                .addEventData("event.detail");

        contentContainer.setVisible(isContentVisible);
        contentContainer.add(templateItem, calendar);
        add(createToolbar(), contentContainer);
    }

    private HorizontalLayout createToolbar() {
        HorizontalLayout bar = new HorizontalLayout();
        bar.setWidthFull();
        bar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        bar.setAlignItems(FlexComponent.Alignment.CENTER);

        bar.getStyle().set("background-color", "transparent");

        Span title = new Span();
        title.setText(getTranslation("scheduleCalendarComponent.title"));
        title.getStyle().set("font-weight", "bold");

        title.setWidth("50%");
        toggleVisibilityButton.setWidth("50%");
        toggleVisibilityButton.addClickListener(e -> toggleContentVisibility());

        bar.add(title, toggleVisibilityButton);
        bar.setFlexGrow(1, toggleVisibilityButton);

        return bar;
    }

    private void toggleContentVisibility() {
        isContentVisible = !isContentVisible;
        contentContainer.setVisible(isContentVisible);

        toggleVisibilityButton.setIcon(
                isContentVisible ? VaadinIcon.ANGLE_UP.create() : VaadinIcon.ANGLE_DOWN.create());
    }

    private Div createDraggableTemplate(TempTemplate template) {
        Div div = new Div();
        div.setText(template.name());
        div.addClassName("calendar-template-item");
        div.getStyle().set("background-color", template.color());
        div.getStyle().set("padding", "10px");
        div.getStyle().set("margin-bottom", "5px");
        div.getStyle().set("border-radius", "4px");
        div.getStyle().set("cursor", "grab");
        div.getElement().setAttribute("draggable", "true");

        JsonObject data = Json.createObject();
        data.put("title", template.name());
        data.put("duration", template.daysCount());
        data.put("color", template.color());
        div.getElement().setAttribute("data-template", data.toJson());

        return div;
    }

    private void handleTemplateDrop(String startDateStr, TempTemplate template) {
        LocalDate start = LocalDate.parse(startDateStr.split("T")[0]);
        LocalDate end = start.plusDays(template.daysCount());

        calendar.addEvent(
                UUID.randomUUID().toString(), template.name(), start.toString(), end.toString(), template.color());
    }
}
