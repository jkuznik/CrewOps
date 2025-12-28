package pl.crewops.ui.component.custom.schedule;

import com.vaadin.flow.component.html.Div;
import lombok.Getter;
import pl.crewops.model.dto.shift.ShiftDTO;

@Getter
class ShiftPaletteItem extends Div {

    private final ShiftDTO shiftDTO;

    public ShiftPaletteItem(ShiftDTO dto) {
        this.shiftDTO = dto;
        setText(dto.name());

        getStyle().set("background-color", dto.color());
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

        getStyle().set("display", "-webkit-box");
        getStyle().set("-webkit-line-clamp", "2");
        getStyle().set("-webkit-box-orient", "vertical");
        getStyle().set("overflow", "hidden");
        getStyle().set("white-space", "normal");

        getElement().setAttribute("draggable", "true");
        getElement().setAttribute("data-shift-id", dto.id().toString());

        getElement()
                .executeJs(
                        "this.addEventListener('dragstart', e => {"
                                + "  e.dataTransfer.setData('application/json', JSON.stringify({id: $0, isNew: true}));"
                                + "  e.dataTransfer.dropEffect = 'copy';"
                                + "});",
                        dto.id().toString());
    }
}
