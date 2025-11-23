package pl.crewops.ui.component.custom.schedule;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import pl.crewops.model.dto.shift.ShiftDTO;

public class DailyScheduleGenerator extends VerticalLayout {

    private final HorizontalLayout shiftsPalette = new HorizontalLayout();

    private final DailyScheduleGrid grid = new DailyScheduleGrid();

    public DailyScheduleGenerator() {
        setSizeFull();

        add(shiftsPalette, grid);
    }

    public void addShiftResourceDragBar(ShiftDTO shiftDTO) {
        var dragItem = new ShiftResourceDragBar(new ShiftResource(shiftDTO));

        dragItem.setText(shiftDTO.name());

        dragItem.getStyle()
                .set("padding", "6px")
                .set("background", "#c33")
                .set("color", "white")
                .set("border-radius", "6px")
                .set("cursor", "grab")
                .set("min-width", "50px")
                .set("justify-content", "center")
                .set("display", "flex");

        dragItem.addDragStartListener(event -> {});
        dragItem.addDragEndListener(event -> {
            grid.getGrid().getDataProvider().refreshAll();
        });

        shiftsPalette.add(dragItem);
    }
}
