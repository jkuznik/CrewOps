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
import java.util.List;
import java.util.UUID;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.dto.scheduleTemplate.ScheduleTemplateDTO;
import pl.crewops.util.SpringContextBridge;

@CssImport("./styles/component/schedule-content-component.css")
class ScheduleCalendarComponent extends VerticalLayout {

    private final CoreAPI coreAPI = SpringContextBridge.getBean(CoreAPI.class);
    private final VerticalLayout contentContainer;

    // Zmieniamy na HorizontalLayout, aby kafelki były w rzędzie jak w ShiftPalette
    private final HorizontalLayout templateItemsLayout = new HorizontalLayout();
    private final Button toggleVisibilityButton = new Button(VaadinIcon.ANGLE_DOWN.create());
    private final Calendar calendar = new Calendar();

    private boolean isContentVisible = false;

    public ScheduleCalendarComponent() {
        this.contentContainer = new VerticalLayout();
        addClassName("component-content-border");
        setSizeFull();
        setPadding(true);

        configureTemplatePalette();
        loadTemplatesFromApi();

        calendar.getElement()
                .addEventListener("template-dropped", e -> {
                    JsonObject eventDetail = e.getEventData().getObject("event.detail");
                    if (eventDetail != null) {
                        String dateStr = eventDetail.getString("date");
                        JsonObject templateJson = eventDetail.getObject("template");
                        handleActualTemplateDrop(
                                dateStr,
                                templateJson.getString("title"),
                                (int) templateJson.getNumber("duration"),
                                templateJson.getString("color"));
                    }
                })
                .addEventData("event.detail");

        contentContainer.setVisible(isContentVisible);
        contentContainer.add(templateItemsLayout, calendar);
        add(createToolbar(), contentContainer);
    }

    private void configureTemplatePalette() {
        Span paletteHeader = new Span(getTranslation("scheduleCalendarComponent.paletteTitle"));
        paletteHeader
                .getStyle()
                .set("font-size", "var(--lumo-font-size-s)")
                .set("font-weight", "bold")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "1px");

        templateItemsLayout.setWidthFull();
        templateItemsLayout.setMinHeight("65px");
        templateItemsLayout.setPadding(false);
        templateItemsLayout
                .getStyle()
                .set("gap", "12px")
                .set("overflow-x", "auto")
                .set("padding-bottom", "5px")
                .set("margin-top", "10px");

        contentContainer.add(paletteHeader);
    }

    private void loadTemplatesFromApi() {
        templateItemsLayout.removeAll();
        try {
            List<ScheduleTemplateDTO> templates = coreAPI.getAllTemplates();
            templates.forEach(dto -> {
                // Używamy dedykowanej klasy TemplatePaletteItem
                templateItemsLayout.add(new TemplatePaletteItem(dto));
            });
        } catch (NotAuthenticatedException e) {
            // silent catch
        }
    }

    private void handleActualTemplateDrop(String startDateStr, String title, int daysCount, String color) {
        LocalDate start = LocalDate.parse(startDateStr.split("T")[0]);
        LocalDate end = start.plusDays(daysCount);

        calendar.addEvent(UUID.randomUUID().toString(), title, start.toString(), end.toString(), color);
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

    private Div createDraggableTemplate(ScheduleTemplateDTO dto) {
        Div div = new Div();
        div.setText(dto.name());
        div.addClassName("calendar-template-item");

        String color = "#3498db"; // Domyślny kolor

        div.getStyle().set("background-color", color);
        div.getStyle().set("padding", "10px");
        div.getStyle().set("margin-bottom", "5px");
        div.getStyle().set("border-radius", "4px");
        div.getStyle().set("cursor", "grab");
        div.getStyle().set("color", "white"); // Dodane dla czytelności tekstu
        div.getElement().setAttribute("draggable", "true");

        JsonObject data = Json.createObject();
        data.put("id", dto.id().toString());
        data.put("title", dto.name());
        data.put("duration", Math.max(1, dto.days().size()));
        data.put("color", color);

        div.getElement().setAttribute("data-template", data.toJson());

        return div;
    }
}
