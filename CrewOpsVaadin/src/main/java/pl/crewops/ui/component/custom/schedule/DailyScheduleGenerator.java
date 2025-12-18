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

        ShiftDTO poranna = new ShiftDTO(UUID.randomUUID(), "Zmiana Poranna", null, "#2ecc71");
        ShiftDTO nocna = new ShiftDTO(UUID.randomUUID(), "Zmiana Nocna", null, "#9b59b6");

        addShiftToPalette(poranna);
        addShiftToPalette(nocna);

        add(shiftsPalette, nativeGrid);

        nativeGrid.addDay(new ScheduleDay(1));
        nativeGrid.addDay(new ScheduleDay(2));

        nativeGrid.updateClientSideData();
    }

    private void addShiftToPalette(ShiftDTO dto) {
        ShiftPaletteItem item = new ShiftPaletteItem(dto);
        shiftsPalette.add(item);
        nativeGrid.registerPaletteTemplate(dto);
    }
}
