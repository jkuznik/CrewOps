package pl.crewops.ui.component.custom.schedule;

import static pl.crewops.ui.component.custom.schedule.DailyScheduleGrid.INTERVALS_PER_DAY;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropEffect;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.dnd.EffectAllowed;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;
import lombok.Setter;
import pl.crewops.enums.TimeSlot;

abstract class DragAndDropBar {}

class ShiftResourceDragBar extends Div implements DragSource<ShiftResourceDragBar> {
    @Getter
    private final ShiftResource resource;

    private final Span label = new Span();

    public ShiftResourceDragBar(ShiftResource resource) {
        this.resource = resource;

        getStyle().set("box-sizing", "border-box").set("margin", "0").set("padding", "0");

        addClassName("shift-base-style");

        // Dodajemy label jako kontrolowany element tekstowy
        label.getStyle().set("display", "inline-block");
        add(label);

        // Drag & Drop
        DragSource<Div> dragSource = DragSource.create(this);
        dragSource.setDragData(resource);
        dragSource.setEffectAllowed(EffectAllowed.COPY_MOVE);

        dragSource.addDragStartListener(e -> getElement().getClassList().add("dragged-item"));
        dragSource.addDragEndListener(e -> getElement().getClassList().remove("dragged-item"));
    }

    public void setText(String text) {
        label.setText(text);
    }

    // Ustawia kolor tekstu bezpośrednio na label z !important, aby przebić zewnętrzne reguły
    public void setTextColor(String color) {
        // zabezpieczenie: gdy color null lub pusty -> usuń właściwość
        if (color == null || color.isBlank()) {
            // usuń inline style important
            label.getElement().executeJs("this.style.removeProperty('color');");
        } else {
            // ustaw z priorytetem 'important'
            label.getElement().executeJs("this.style.setProperty('color', $0, 'important');", color);
        }
    }
}

@CssImport("./styles/component/dailyView/drag-and-drop.css")
class ShiftResourceDropBar extends Div {

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

        getStyle().set("box-sizing", "border-box").set("margin", "0").set("padding", "0");

        addClassName("schedule-drop-target");

        DropTarget<ShiftResourceDropBar> dropTarget = DropTarget.create(this);
        dropTarget.setDropEffect(DropEffect.MOVE);

        dropTarget.addDropListener(event -> {
            getElement().getClassList().remove("schedule-drop-active");

            event.getDragData().ifPresent(data -> {
                if (data instanceof ShiftResourceResizeBar resizeData) {
                    handleResizeDropInternal(resizeData);
                    return;
                }

                if (data instanceof ShiftResource shiftResource) {
                    shiftResource.setStartSlot(TimeSlot.fromIndex(index));

                    if (shiftResource.hasCrossMidnightSegment()) {
                        if (shiftResource.getEndSlotIndex() < INTERVALS_PER_DAY) {
                            fireEvent(new ShiftNoLongerCrossesMidnightEvent(this, shiftResource));
                        } else {
                            int beforeMoveStartSlot = shiftResource.getBeforeMoveStartSlot();
                            int newStartSlot = shiftResource.getStartSlot().getIndex();

                            int value;
                            if (newStartSlot >= beforeMoveStartSlot) {
                                value = newStartSlot - beforeMoveStartSlot;
                            } else {
                                value = -(beforeMoveStartSlot - newStartSlot);
                            }
                            fireEvent(new CrossMidnightResizeEvent(this, shiftResource, value));
                        }
                    } else {
                        if (shiftResource.getEndSlotIndex() > INTERVALS_PER_DAY) {
                            fireEvent(new ShiftCrossMidnightEvent(this, shiftResource));
                        }
                    }

                    shiftResource.setBeforeMoveStartSlot(index);
                    if (!day.getShifts().contains(shiftResource)) {
                        day.getShifts().add(shiftResource);
                    }
                }
            });
        });

        updateStyleForContent();
    }

    private void handleResizeDropInternal(ShiftResourceResizeBar resizeData) {
        ShiftResource shiftToModify = resizeData.getShift();
        ShiftResourceResizeBar.ResizeEdge edge = resizeData.getEdge();

        int dropSlotIndex = this.index;
        int originalStartIndex = shiftToModify.getStartSlotIndex();
        int originalEndIndex = originalStartIndex + shiftToModify.getDurationInSlots();

        int newStartIndex = originalStartIndex;
        int newDuration;

        if (edge == ShiftResourceResizeBar.ResizeEdge.END) {
            int newEndIndex = dropSlotIndex + 1;
            newDuration = newEndIndex - originalStartIndex;
            if (shiftToModify.isCrossMidnightSegment()) {
                fireEvent(new CrossMidnightResizeEvent(
                        this, shiftToModify, newDuration - shiftToModify.getDurationInSlots()));
            }

        } else {
            newStartIndex = dropSlotIndex; // Nowy start to slot, na który upuszczono
            newDuration = originalEndIndex - newStartIndex;
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
    }

    public void updateStyleForContent() {
        if (this.droppedResource != null) {
            setStyleForFilled();
        } else {
            setStyleForEmpty();
        }
    }

    // 🔥 MODYFIKUJEMY: setStyleForFilled
    private void setStyleForFilled() {
        // 🔥 USUŃ removeClassName("slot-style-empty");
        addClassName("slot-style-filled");
    }

    // 🔥 MODYFIKUJEMY: setStyleForEmpty (teraz ustawia brak stylu wizualnego)
    private void setStyleForEmpty() {
        removeClassName("slot-style-filled");
        // 🔥 NIE DODAJEMY TUTAJ ŻADNEJ KLASY (Domyślnie jest niewidoczny/przezroczysty)
        // Należy jednak usunąć to, co zostało po starej klasie.
    }

    // 🔥 MODYFIKUJEMY: applyStyles
    public void applyStyles(int duration) {
        getStyle().set("flex-grow", String.valueOf(duration));
        getStyle().set("flex-shrink", "0");
        getStyle().set("width", "auto");

        if (this.droppedResource != null) {
            removeClassName("schedule-slot-drop-target"); // Nie jest już celem upuszczenia
            setStyleForFilled();
        } else {
            // Dodajemy tylko klasę targetu
            addClassName("schedule-slot-drop-target");
            setStyleForEmpty(); // Po prostu usuwa klasę filled
        }
    }

    abstract static class ShiftResourceDropBarEvent extends ComponentEvent<ShiftResourceDropBar> {
        @Getter
        private final ShiftResource shiftResource;

        public ShiftResourceDropBarEvent(ShiftResourceDropBar source, ShiftResource shiftResource) {
            super(source, false);
            this.shiftResource = shiftResource;
        }
    }

    static class ShiftCrossMidnightEvent extends ShiftResourceDropBarEvent {
        public ShiftCrossMidnightEvent(ShiftResourceDropBar source, ShiftResource shiftResource) {
            super(source, shiftResource);
        }
    }

    public Registration addCrossMidnightEvent(ComponentEventListener<ShiftCrossMidnightEvent> listener) {
        return addListener(ShiftCrossMidnightEvent.class, listener);
    }

    static class ShiftNoLongerCrossesMidnightEvent extends ShiftResourceDropBarEvent {
        public ShiftNoLongerCrossesMidnightEvent(ShiftResourceDropBar source, ShiftResource shift) {
            super(source, shift);
        }
    }

    public Registration addShiftNoLongerCrossesMidnightEvent(
            ComponentEventListener<ShiftNoLongerCrossesMidnightEvent> listener) {
        return addListener(ShiftNoLongerCrossesMidnightEvent.class, listener);
    }

    @Getter
    static class CrossMidnightResizeEvent extends ShiftResourceDropBarEvent {

        private final int value;

        public CrossMidnightResizeEvent(ShiftResourceDropBar source, ShiftResource shiftResource, int value) {
            super(source, shiftResource);
            this.value = value;
        }
    }

    public Registration addCrossMidnightResizeEvent(ComponentEventListener<CrossMidnightResizeEvent> listener) {
        return addListener(CrossMidnightResizeEvent.class, listener);
    }
}

@Getter
class ShiftResourceResizeBar extends Div implements DragSource<ShiftResourceResizeBar> {

    private final ShiftResource shift;
    private final ResizeEdge edge;

    public ShiftResourceResizeBar(ShiftResource shift, ResizeEdge edge) {
        this.shift = shift;
        this.edge = edge;

        addClassName("resize-drag-source");

        setWidth("5px");
        setHeightFull();

        getStyle()
                .set("background-color", "#ffffff")
                .set("opacity", "0.6")
                .set("cursor", "ew-resize")
                .set("position", "absolute")
                .set("top", "0")
                .set("z-index", "10");

        if (edge == ResizeEdge.START) {
            getStyle().set("left", "0");
        } else {
            getStyle().set("right", "0");
        }

        DragSource<ShiftResourceResizeBar> dragSource = DragSource.create(this);
        setDragData(this);
        setEffectAllowed(EffectAllowed.MOVE);

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

        dragSource.addDragStartListener(e -> getElement().getClassList().add("dragged-handle"));
        dragSource.addDragEndListener(e -> getElement().getClassList().remove("dragged-handle"));
    }

    public enum ResizeEdge {
        START,
        END
    }
}
