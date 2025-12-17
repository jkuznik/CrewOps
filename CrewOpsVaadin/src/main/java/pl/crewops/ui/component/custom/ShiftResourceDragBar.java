package pl.crewops.ui.component.custom;

import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.html.Div;
import lombok.Getter;
import pl.crewops.ui.component.custom.schedule.ShiftResource;

@Getter
public class ShiftResourceDragBar extends Div implements DragSource<ShiftResourceDragBar> {
    private final ShiftResource resource;

    public ShiftResourceDragBar(ShiftResource resource) {
        this.resource = resource;
        setText(resource.getShiftDTO().name());

        // Stylowanie identyczne jak w oryginale
        getStyle().set("background-color", resource.getShiftDTO().color());
        getStyle().set("padding", "10px");
        getStyle().set("border-radius", "4px");
        getStyle().set("cursor", "grab");
        getStyle().set("color", "white");
        getStyle().set("font-weight", "bold");

        // Kluczowe dla integracji z TS: Przekazujemy dane w formacie JSON
        setDragData(resource.getShiftDTO().id().toString());

        // Dodajemy typ danych, który nasz TS umie odczytać
        getElement().getStyle().set("user-select", "none");
    }
}
