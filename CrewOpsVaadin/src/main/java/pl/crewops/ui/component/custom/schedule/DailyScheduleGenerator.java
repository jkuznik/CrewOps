package pl.crewops.ui.component.custom.schedule;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.dnd.DropEffect;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import java.util.UUID;
import pl.crewops.enums.TimeSlot;

public class DailyScheduleGenerator extends VerticalLayout {

    private final DailyScheduleGrid grid = new DailyScheduleGrid();

    public DailyScheduleGenerator() {
        setSizeFull();

        HorizontalLayout palette = new HorizontalLayout(
                new Div("Drag target test components:"),
                // Zmieniamy wywołania na nową metodę
                createShiftDragSource("Test A", "A", 60), // Np. 60 minut
                createShiftDragSource("Test B", "B", 60) // Np. 30 minut
                );

        add(palette, grid);
    }

    public Component createShiftDragSource(String label, String dataValue, int durationMinutes) {
        int intervalDurationMinutes = 15;

        if (durationMinutes % intervalDurationMinutes != 0) {
            throw new IllegalArgumentException("Duration must be a multiple of 15 minutes.");
        }

        int durationInSlots = durationMinutes / intervalDurationMinutes;

        TimeSlot startSlotForPalette = TimeSlot.H00_00;

        // Zaktualizowany ShiftResource, ustawiamy trackIndex na 0
        ShiftResource resource = new ShiftResource(UUID.randomUUID(), startSlotForPalette, durationInSlots, 0);

        DragTimeBar dragItem = new DragTimeBar(resource);

        dragItem.setText(label);

        dragItem.getStyle()
                .set("padding", "6px")
                .set("background", "#c33")
                .set("color", "white")
                .set("border-radius", "6px")
                .set("cursor", "grab")
                .set("min-width", "50px")
                .set("justify-content", "center")
                .set("display", "flex");

        return dragItem;
    }
}

class DailyDropTimeBar extends TimeBar {

    private final ScheduleDay day;
    private final Grid<ScheduleDay> grid;
    private final TimeSlot targetSlot;
    private final int trackIndex;

    public DailyDropTimeBar(TimeSlot slot, ScheduleDay day, Grid<ScheduleDay> grid, int trackIndex) {
        super();
        this.targetSlot = slot;
        this.day = day;
        this.grid = grid;
        this.trackIndex = trackIndex;

        getStyle()
                .set("background-color", "#f0f0f0")
                .set("border", "2px dashed #aaa")
                .set("box-sizing", "border-box");

        addClassName("schedule-drop-target");

        getElement().addEventListener("dragenter", event -> {
            getElement().getClassList().add("schedule-drop-active");
        });

        getElement().addEventListener("dragleave", event -> {
            getElement().getClassList().remove("schedule-drop-active");
        });

        DropTarget<DailyDropTimeBar> dropTarget = DropTarget.create(this);
        dropTarget.setDropEffect(DropEffect.COPY);

        dropTarget.addDropListener(event -> {
            getElement().getClassList().remove("schedule-drop-active");

            event.getDragData().ifPresent(data -> {
                if (data instanceof ShiftResource droppedResource) {

                    TimeSlot newShiftStartSlot = this.targetSlot;
                    int newDurationInSlots = droppedResource.getDurationInSlots();

                    ShiftResource newShift = new ShiftResource(
                            UUID.randomUUID(), newShiftStartSlot, newDurationInSlots, this.trackIndex);

                    day.addShift(newShift);

                    fireEvent(new DropEventOnLastTrack(this, false, day));

                    grid.getDataProvider().refreshAll();
                }
            });
        });
    }

    public Registration addDropOnLastTrackListener(ComponentEventListener<DropEventOnLastTrack> listener) {
        return addListener(DropEventOnLastTrack.class, listener);
    }
}

class DropEventOnLastTrack extends ComponentEvent<DailyDropTimeBar> {

    private final ScheduleDay day;

    public DropEventOnLastTrack(DailyDropTimeBar source, boolean fromClient, ScheduleDay day) {
        super(source, fromClient);
        this.day = day;
    }

    public ScheduleDay getDay() {
        return day;
    }
}
