package pl.crewops.ui.component.custom.schedule;

import com.vaadin.flow.component.html.Div;
import elemental.json.Json;
import elemental.json.JsonObject;
import lombok.Getter;
import pl.crewops.model.dto.scheduleTemplate.ScheduleTemplateDTO;

@Getter
public class TemplatePaletteItem extends Div {

    private final ScheduleTemplateDTO templateDTO;

    public TemplatePaletteItem(ScheduleTemplateDTO dto) {
        this.templateDTO = dto;
        this.addClassName("calendar-template-item"); // Kluczowe dla selektora w TS
        setText(dto.name());

        // Konfiguracja wizualna - identyczna jak w ShiftPaletteItem
        String color = "#3498db"; // Tymczasowy niebieski
        getStyle().set("background-color", color);
        getStyle().set("padding", "8px 12px");
        getStyle().set("border-radius", "6px");
        getStyle().set("cursor", "grab");
        getStyle().set("color", "white");
        getStyle().set("user-select", "none");
        getStyle().set("font-weight", "600");
        getStyle().set("font-size", "var(--lumo-font-size-s)");

        getStyle().set("width", "130px");
        getStyle().set("min-width", "130px");
        getStyle().set("height", "50px");
        getStyle().set("display", "flex");
        getStyle().set("align-items", "center");
        getStyle().set("justify-content", "center");
        getStyle().set("text-align", "center");
        getStyle().set("box-shadow", "var(--lumo-box-shadow-xs)");

        // Obsługa ucinania długiego tekstu (identyczna jak w ShiftPaletteItem)
        getStyle().set("display", "-webkit-box");
        getStyle().set("-webkit-line-clamp", "2");
        getStyle().set("-webkit-box-orient", "vertical");
        getStyle().set("overflow", "hidden");
        getStyle().set("white-space", "normal");

        getElement().setAttribute("draggable", "true");

        // Przygotowanie danych dla FullCalendar (TS)
        JsonObject data = Json.createObject();
        data.put("id", dto.id().toString());
        data.put("title", dto.name());
        data.put("duration", Math.max(1, dto.days().size()));
        data.put("color", color);
        getElement().setAttribute("data-template", data.toJson());
    }
}
