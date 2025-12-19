package pl.crewops.ui.component.custom.schedule;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.UUID;
import pl.crewops.model.dto.shift.ShiftDTO;

public class DailyScheduleGenerator extends VerticalLayout {

    private final HorizontalLayout shiftsPalette = new HorizontalLayout();

    private final NativeScheduleGrid nativeGrid = new NativeScheduleGrid();

    public DailyScheduleGenerator() {
        setSizeFull();
        setPadding(false);
        setSpacing(true);

        shiftsPalette.setWidthFull();
        shiftsPalette.setMinHeight("60px");
        shiftsPalette.getStyle().set("padding", "10px");
        shiftsPalette.getStyle().set("gap", "10px");
        shiftsPalette.getStyle().set("overflow-x", "auto");

        add(shiftsPalette, nativeGrid);

        ShiftDTO poranna = new ShiftDTO(UUID.randomUUID(), "Zmiana Poranna", null, "#2ecc71"); // Szmaragdowy
        ShiftDTO druga = new ShiftDTO(
                UUID.randomUUID(),
                "Zmiana Druga",
                null,
                "#3498db"); // Jasnoniebieski (zmieniony kolor dla odróżnienia od nocnej)
        ShiftDTO nocna = new ShiftDTO(UUID.randomUUID(), "Zmiana Nocna", null, "#9b59b6"); // Fioletowy

        ShiftDTO czwarta = new ShiftDTO(UUID.randomUUID(), "Międzyzmiana", null, "#f1c40f"); // Słoneczny żółty
        ShiftDTO popoludniowa =
                new ShiftDTO(UUID.randomUUID(), "Zmiana Popołudniowa", null, "#e67e22"); // Pomarańczowy (Carrot)
        ShiftDTO techniczna = new ShiftDTO(UUID.randomUUID(), "Przegląd", null, "#e74c3c"); // Czerwony (Alizarin)
        ShiftDTO biurowa =
                new ShiftDTO(UUID.randomUUID(), "Administracja", null, "#34495e"); // Ciemny granat (Wet Asphalt)
        ShiftDTO weekendowa = new ShiftDTO(UUID.randomUUID(), "Weekend", null, "#1abc9c"); // Turkusowy (Turquoise)
        ShiftDTO shadow = new ShiftDTO(UUID.randomUUID(), "Shadow Shift", null, "#95a5a6"); // Szary (Concrete)

        //        addShiftToPalette(poranna);
        //        addShiftToPalette(druga);
        //        addShiftToPalette(popoludniowa);
        //        addShiftToPalette(nocna);
        //        addShiftToPalette(czwarta);
        //        addShiftToPalette(techniczna);
        //        addShiftToPalette(biurowa);
        //        addShiftToPalette(weekendowa);
        //        addShiftToPalette(shadow);

        add(shiftsPalette, nativeGrid);

        nativeGrid.addDay(new ScheduleDay(1));

        nativeGrid.updateClientSideData();
    }

    public void addShiftToPalette(ShiftDTO dto) {
        removeShiftFromPalette(dto.id());

        ShiftPaletteItem item = new ShiftPaletteItem(dto);
        shiftsPalette.add(item);
        nativeGrid.registerPaletteTemplate(dto);
    }

    public void removeShiftFromPalette(UUID shiftId) {
        shiftsPalette
                .getChildren()
                .filter(ShiftPaletteItem.class::isInstance)
                .map(ShiftPaletteItem.class::cast)
                .filter(item -> item.getShiftDTO().id().equals(shiftId))
                .findFirst()
                .ifPresent(shiftsPalette::remove);

        nativeGrid.removeShiftsByTemplate(shiftId);
    }

    public void updateShiftInPalette(ShiftDTO dto) {
        shiftsPalette
                .getChildren()
                .filter(ShiftPaletteItem.class::isInstance)
                .map(ShiftPaletteItem.class::cast)
                .filter(item -> item.getShiftDTO().id().equals(dto.id()))
                .findFirst()
                .ifPresent(shiftsPalette::remove);

        ShiftPaletteItem newItem = new ShiftPaletteItem(dto);
        shiftsPalette.add(newItem);

        // 2. Aktualizujemy dane w istniejących zasobach na siatce (bez ich usuwania!)
        nativeGrid.updateShiftsFromTemplate(dto);
    }
}
