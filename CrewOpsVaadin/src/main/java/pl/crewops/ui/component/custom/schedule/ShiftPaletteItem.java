package pl.crewops.ui.component.custom.schedule;

import com.vaadin.flow.component.html.Div;
import lombok.Getter;
import pl.crewops.model.dto.shift.ShiftDTO;

@Getter
public class ShiftPaletteItem extends Div {

    private final ShiftDTO shiftDTO;

    public ShiftPaletteItem(ShiftDTO dto) {
        this.shiftDTO = dto;
        setText(dto.name());
        getStyle().set("background-color", dto.color());
        getStyle().set("padding", "8px 12px");
        getStyle().set("border-radius", "4px");
        getStyle().set("cursor", "grab");
        getStyle().set("color", "white");
        getStyle().set("user-select", "none");
        getStyle().set("font-weight", "bold");
        getStyle().set("white-space", "nowrap");

        // KLUCZ: Ustawiamy natywne atrybuty HTML5 Drag & Drop
        getElement().setAttribute("draggable", "true");

        // Przekazujemy ID w atrybucie, który łatwo odczytamy w JS
        getElement().setAttribute("data-shift-id", dto.id().toString());

        // Dodajemy prosty skrypt, który odpali się przy starcie przeciągania
        getElement()
                .executeJs(
                        "this.addEventListener('dragstart', e => {"
                                + "  e.dataTransfer.setData('application/json', JSON.stringify({id: $0, isNew: true}));"
                                + "  e.dataTransfer.dropEffect = 'copy';"
                                + "});",
                        dto.id().toString());
    }
}
