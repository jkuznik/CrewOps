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

    @Getter
    private final int index;

    @Getter
    @Setter
    private ShiftResource droppedResource;

    public ShiftResourceDropBar(ScheduleDay day, int index) {
        super();
        this.day = day;
        this.index = index;

        getStyle().set("box-sizing", "border-box");

        addClassName("schedule-drop-target");

        DropTarget<ShiftResourceDropBar> dropTarget = DropTarget.create(this);
        dropTarget.setDropEffect(DropEffect.MOVE);

        dropTarget.addDropListener(event -> {
            getElement().getClassList().remove("schedule-drop-active");

            event.getDragData().ifPresent(data -> {

                // --- 1. FILTROWANIE OPERACJI RESIZE (nowa logika) ---
                if (data instanceof ResizeDragData resizeData) {
                    // TO JEST OPERACJA RESIZE (z uchwytu)
                    handleResizeDropInternal(resizeData);
                    return; // Zakończ, jeśli obsłużono RESIZE
                }

                // --- 2. OPERACJA MOVE / COPY (oryginalna logika przenoszenia/kopiowania) ---
                if (data instanceof ShiftResource shiftResource) {

                    // Sprawdzamy, czy Vaadin potwierdził operację MOVE
                    boolean isMoveOperation = event.getDropEffect() != null
                            && event.getDropEffect().equals(DropEffect.MOVE);

                    // 1. Tworzymy nowy zasób z nowym slotem startowym.
                    var newShiftResource = new ShiftResource(shiftResource.getShiftDTO());
                    newShiftResource.setStartSlot(TimeSlot.fromIndex(index)); // Używamy 'this.index'
                    newShiftResource.setDurationInSlots(shiftResource.getDurationInSlots());

                    // 2. Usuwamy stary zasób, jeśli to była operacja MOVE
                    if (isMoveOperation && day.getShifts().contains(shiftResource)) {
                        day.getShifts().remove(shiftResource);
                    }

                    // 3. Dodajemy nowy zasób do dnia
                    day.addShift(newShiftResource);

                    // Emitujemy DropEvent, aby DayScheduleVisualization wiedział, że musi się przeładować
                    fireEvent(new DropEvent(this, day));
                }
            });
        });

        updateStyleForContent();
    }

    private void handleResizeDropInternal(ResizeDragData resizeData) {
        ShiftResource shiftToModify = resizeData.getShift();
        ShiftResizeHandle.ResizeEdge edge = resizeData.getEdge();

        int dropSlotIndex = this.index;
        int originalStartIndex = shiftToModify.getStartSlotIndex();
        int originalEndIndex = originalStartIndex + shiftToModify.getDurationInSlots();

        int newStartIndex = originalStartIndex;
        int newDuration;

        if (edge == ShiftResizeHandle.ResizeEdge.END) {
            // ZMIANA KOŃCA (ROZCIĄGANIE W PRAWO)
            // Kończymy na końcu slotu, na który upuszczono.
            int newEndIndex = dropSlotIndex + 1;
            newDuration = newEndIndex - originalStartIndex;

        } else { // edge == ShiftResizeHandle.ResizeEdge.START
            // ZMIANA STARTU (ROZCIĄGANIE W LEWO)
            newStartIndex = dropSlotIndex; // Nowy start to slot, na który upuszczono
            newDuration = originalEndIndex - newStartIndex;
        }

        // --- WALIDACJA ---
        if (newDuration < 2) {
            // Minimalna długość 2 slotów (dla uchwytów)
            System.err.println("RESIZE ERROR: Nowa długość (" + newDuration + ") jest za krótka.");
            return;
        }

        // Nie możemy zacząć później niż koniec, ani skończyć wcześniej niż początek
        if (newStartIndex >= originalEndIndex || newStartIndex >= (originalStartIndex + newDuration)) {
            System.err.println("RESIZE ERROR: Nieprawidłowa zmiana. Start przekroczył koniec.");
            return;
        }

        if (newStartIndex != originalStartIndex) {
            shiftToModify.setStartSlot(TimeSlot.fromIndex(newStartIndex));
        }
        shiftToModify.setDurationInSlots(newDuration);

        fireEvent(new DropEvent(this, day));
    }

    public void updateStyleForContent() {
        if (this.droppedResource != null) {
            setStyleForFilled();
        } else {
            setStyleForEmpty();
        }
    }

    private void setStyleForFilled() {
        getStyle().set("background-color", "#3e70d6").set("border", "none");
    }

    private void setStyleForEmpty() {
        getStyle().set("background-color", "#f0f0f0").set("border", "2px dashed #aaa");
    }

    public void applyStyles(int duration) {

        getStyle().set("flex-grow", String.valueOf(duration));
        getStyle().set("flex-shrink", "0");
        getStyle().set("width", "auto");

        if (this.droppedResource != null) {
            removeClassName("schedule-slot-drop-target");
            setStyleForFilled();
        } else {
            addClassName("schedule-slot-drop-target");
            setStyleForEmpty();
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

class ShiftResizeHandle extends Div implements DragSource<ShiftResizeHandle> {

    public enum ResizeEdge {
        START,
        END
    }

    public ShiftResizeHandle(ShiftResource shift, ResizeEdge edge) {

        setWidth("5px");
        setHeightFull();
        getStyle()
                .set("background-color", "#ffffff")
                .set("opacity", "0.6")
                .set("cursor", "ew-resize")
                .set("position", "absolute")
                .set("top", "0")
                .set("z-index", "10");

        // Pozycjonowanie krawędzi
        if (edge == ResizeEdge.START) {
            getStyle().set("left", "0");
        } else {
            getStyle().set("right", "0");
        }

        // Konfiguracja DragSource
        DragSource.create(this);
        // Przekazujemy klucz identyfikujący: obiekt zmiany + informacja o krawędzi
        setDragData(new ResizeDragData(shift, edge));
        setEffectAllowed(EffectAllowed.MOVE);

        // KLUCZOWY IDENTYFIKATOR DLA FILTROWANIA W handleResizeDrop
        addClassName("resize-drag-source");

        // ----------------------------------------------------------------------
        // KLUCZOWA POPRAWKA: Zatrzymanie propagacji za pomocą bezpośredniego JS.
        // Gwarantuje to, że kliknięcie na uchwycie nie uruchomi DragSource na rodzicu.
        getElement()
                .executeJs("this.addEventListener('mousedown', function(e) { " + "    e.stopPropagation(); "
                        + "}, true);");
        // ----------------------------------------------------------------------

        // Opcjonalne efekty wizualne na hover
        getElement().addEventListener("mouseenter", e -> getStyle().set("opacity", "1.0"));
        getElement().addEventListener("mouseleave", e -> getStyle().set("opacity", "0.6"));
    }
}

class ResizeDragData {
    private final ShiftResource shift;
    private final ShiftResizeHandle.ResizeEdge edge;

    public ResizeDragData(ShiftResource shift, ShiftResizeHandle.ResizeEdge edge) {
        this.shift = shift;
        this.edge = edge;
    }

    public ShiftResource getShift() {
        return shift;
    }

    public ShiftResizeHandle.ResizeEdge getEdge() {
        return edge;
    }
}
