package pl.crewops.ui.component.custom.schedule;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.UUID;
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

        String textColor = getTextColorForBackground(shiftDTO.color());

        dragItem.getStyle()
                .set("padding", "6px")
                .set("background-color", shiftDTO.color())
                .set("border-radius", "6px")
                .set("cursor", "grab")
                .set("min-width", "50px")
                .set("justify-content", "center")
                .set("display", "flex");

        // Ustawiamy kolor bezpośrednio na labelie (z !important)
        dragItem.setTextColor(textColor);

        dragItem.addDragStartListener(event -> {});
        dragItem.addDragEndListener(event -> {
            grid.getGrid().getDataProvider().refreshAll();
        });

        shiftsPalette.add(dragItem);
    }

    private String getTextColorForBackground(String hex) {
        try {
            int r = Integer.parseInt(hex.substring(1, 3), 16);
            int g = Integer.parseInt(hex.substring(3, 5), 16);
            int b = Integer.parseInt(hex.substring(5, 7), 16);

            // luminancja (perceived brightness)
            double luminance = (0.299 * r + 0.587 * g + 0.114 * b);

            // gdy kolor jasny → czarny tekst
            return luminance > 130 ? "black" : "white";

        } catch (Exception e) {
            return "white"; // domyślnie
        }
    }

    public void updateShiftResourceDragBar(ShiftDTO shiftDTO) {
        shiftsPalette
                .getChildren()
                .filter(component -> component instanceof ShiftResourceDragBar)
                .map(component -> (ShiftResourceDragBar) component)
                .filter(bar -> bar.getResource().getShiftDTO().id().equals(shiftDTO.id()))
                .findFirst()
                .ifPresent(shiftResourceDragBar -> {
                    shiftResourceDragBar.setText(shiftDTO.name());
                    shiftResourceDragBar.getStyle().set("background-color", shiftDTO.color());
                    shiftResourceDragBar.setTextColor(getTextColorForBackground(shiftDTO.color()));
                });
    }

    public void removeShiftResourceDragBar(UUID shiftId) {
        shiftsPalette
                .getChildren()
                .filter(component -> component instanceof ShiftResourceDragBar)
                .map(component -> (ShiftResourceDragBar) component)
                .filter(bar -> bar.getResource().getShiftDTO().id().equals(shiftId))
                .findFirst()
                .ifPresent(shiftsPalette::remove);
    }
}
