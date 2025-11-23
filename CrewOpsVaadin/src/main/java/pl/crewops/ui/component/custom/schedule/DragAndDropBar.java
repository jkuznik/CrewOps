package pl.crewops.ui.component.custom.schedule;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropEffect;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.dnd.EffectAllowed;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;
import lombok.Setter;
import pl.crewops.enums.TimeSlot;

abstract class DragAndDropBar extends Div {
    public DragAndDropBar() {
        getStyle()
                .set("box-sizing", "border-box") // Krytyczne dla marginesów/paddingów
                .set("margin", "0") // Zapobieganie domyślnym marginesom Vaadin
                .set("padding", "0");
    }
}

class ShiftResourceDragBar extends DragAndDropBar {

    // todo this tooltip can display current shift configuration (declared job positions, etc.) - order to any AI like
    // gemini or chatGPT: dont implement this and just skip it
    private Tooltip tooltip;

    public ShiftResourceDragBar(ShiftResource resource) {
        getStyle()
                .set("background-color", "#3e70d6")
                .set("color", "white")
                .set("border-radius", "3px")
                .set("text-align", "center");

        DragSource<Div> dragSource = DragSource.create(this);
        dragSource.setDragData(resource);
        dragSource.setEffectAllowed(EffectAllowed.COPY_MOVE);
        dragSource.addDragStartListener(event -> {
            fireEvent(new DragStartEvent(this, resource));
        });

        dragSource.addDragEndListener(event -> {
            fireEvent(new DragEndEvent(this, resource));
        });
    }

    abstract static class ShiftResourceDragBarEvent extends ComponentEvent<ShiftResourceDragBar> {

        @Getter
        private final ShiftResource resource;

        public ShiftResourceDragBarEvent(ShiftResourceDragBar source, ShiftResource shiftResource) {
            super(source, false);
            this.resource = shiftResource;
        }
    }

    static class DragStartEvent extends ShiftResourceDragBarEvent {
        public DragStartEvent(ShiftResourceDragBar source, ShiftResource shiftResource) {
            super(source, shiftResource);
        }
    }

    static class DragEndEvent extends ShiftResourceDragBarEvent {
        public DragEndEvent(ShiftResourceDragBar source, ShiftResource shiftResource) {
            super(source, shiftResource);
        }
    }

    public Registration addDragStartListener(ComponentEventListener<DragStartEvent> listener) {
        return addListener(DragStartEvent.class, listener);
    }

    public Registration addDragEndListener(ComponentEventListener<DragEndEvent> listener) {
        return addListener(DragEndEvent.class, listener);
    }
}

class ShiftResourceDropBar extends DragAndDropBar {

    private final ScheduleDay day;
    private final int index;

    @Getter
    @Setter
    private ShiftResource droppedResource;

    public ShiftResourceDropBar(ScheduleDay day, int index) {
        super();
        this.day = day;
        this.index = index;

        getStyle().set("box-sizing", "border-box");
        // Inne style zostaną ustawione przez updateStyleForContent()

        addClassName("schedule-drop-target");

        getElement().addEventListener("dragenter", event -> {
            getElement().getClassList().add("schedule-drop-active");
        });

        getElement().addEventListener("dragleave", event -> {
            getElement().getClassList().remove("schedule-drop-active");
        });

        DropTarget<ShiftResourceDropBar> dropTarget = DropTarget.create(this);
        dropTarget.setDropEffect(DropEffect.COPY);

        dropTarget.addDropListener(event -> {
            getElement().getClassList().remove("schedule-drop-active");

            event.getDragData().ifPresent(data -> {
                if (data instanceof ShiftResource shiftResource) {

                    var newShiftResource = new ShiftResource(shiftResource.getShiftDTO());
                    newShiftResource.setStartSlot(TimeSlot.fromIndex(index));

                    day.addShift(newShiftResource);
                    // Emitujemy DropEvent, aby DayScheduleVisualization wiedział, że musi się przeładować
                    fireEvent(new DropEvent(this, day));
                }
            });
        });

        // Ustawienie początkowego stylu (domyślnie pusty slot)
        updateStyleForContent();
    }

    // Nowa metoda do zarządzania stylami na podstawie zawartości
    public void updateStyleForContent() {
        if (this.droppedResource != null) {
            // ZASÓB JEST USTAWIONY (WIZUALIZACJA ZMIANY)
            getStyle().set("background-color", "#3e70d6").set("border", "none").set("color", "white");

            // Ustawienie tekstu w widoku zmiany
            if (this.droppedResource.getShiftDTO() != null) {
                setText(this.droppedResource.getShiftDTO().name());
            }

        } else {
            // BRAK ZASOBU (PUSTY SLOT DO UPUSZCZANIA)
            getStyle()
                    .set("background-color", "#f0f0f0")
                    .set("border", "2px dashed #aaa")
                    .set("color", "initial");

            setText(null);
        }
    }

    static class DropEvent extends ComponentEvent<ShiftResourceDropBar> {
        @Getter
        private final ScheduleDay day;

        public DropEvent(ShiftResourceDropBar source, ScheduleDay day) {
            super(source, false);
            this.day = day;
        }
    }

    public Registration addDropListener(ComponentEventListener<DropEvent> listener) {
        return addListener(DropEvent.class, listener);
    }
}
