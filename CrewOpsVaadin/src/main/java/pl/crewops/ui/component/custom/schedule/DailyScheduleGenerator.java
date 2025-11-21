package pl.crewops.ui.component.custom.schedule;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dnd.DropEffect;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

        // Upewnij się, że durationMinutes jest wielokrotnością 15, jeśli to możliwe
        if (durationMinutes % intervalDurationMinutes != 0) {
            throw new IllegalArgumentException("Duration must be a multiple of 15 minutes.");
        }

        int durationInSlots = durationMinutes / intervalDurationMinutes; // np. 30/15 = 2 sloty, 60/15 = 4 sloty

        // 2. Definicja fikcyjnego TimeSlot startowego.
        // Czas startu jest tu nieistotny, bo to jest źródło na palecie, ale musi być poprawnym TimeSlot.
        // Używamy H00_00 jako uniwersalnego startu dla elementu palety.
        TimeSlot startSlotForPalette = TimeSlot.H00_00;

        // 3. Utwórz nowy ShiftResource używając nowego konstruktora
        ShiftResource resource = new ShiftResource(UUID.randomUUID(), startSlotForPalette, durationInSlots);

        // 4. Tworzymy DragTimeBar na podstawie tego ShiftResource
        // NOTE: Pamiętaj, że DragTimeBar nadal musi wiedzieć, jak obliczyć szerokość paska wizualizacji.
        DragTimeBar dragItem = new DragTimeBar(resource);

        // Ustawienie etykiety
        dragItem.setText(label);

        // 3. Ustawienie stylu i konfiguracja DnD
        dragItem.getStyle()
                .set("padding", "6px")
                .set("background", "#c33")
                .set("color", "white")
                .set("border-radius", "6px")
                .set("cursor", "grab")
                .set("min-width", "50px") // Wymuś minimalną szerokość
                .set("justify-content", "center")
                .set("display", "flex"); // Wyśrodkuj tekst

        // Konfiguracja DragSource jest już w klasie DragTimeBar, ale upewnijmy się,
        // że jest poprawnie ustawiona (w Twoim kodzie DragTimeBar już to robi).

        // Ponieważ DragTimeBar już implementuje DragSource,
        // musimy upewnić się, że efekt jest COPY.
        // Jeśli nie zmienimy DragTimeBar, to musimy stworzyć DragSource tutaj:

        // **OPCJA A: Twój obecny DragTimeBar** (wymaga, by DragTimeBar akceptował EffectAllowed)
        // Albo...

        // **OPCJA B: Prosty Div, ale z DragData jako obiekt ShiftResource (Wymagana zmiana w DragTimeBar)**

        // Wybieram **OPCJĘ B** i modyfikuję Twój **DragTimeBar**, aby upewnić się, że działa na obiekcie ShiftResource,
        // a nie tylko na Stringu.

        return dragItem;
    }
}

class DailyDropTimeBar extends TimeBar {

    private final ScheduleDay day;
    private final Grid<ScheduleDay> grid;
    private final TimeSlot targetSlot;

    // NOWY KONSTRUKTOR
    public DailyDropTimeBar(TimeSlot slot, ScheduleDay day, Grid<ScheduleDay> grid) {
        super();
        this.targetSlot = slot;
        this.day = day;
        this.grid = grid;

        getStyle()
                .set("background-color", "#f0f0f0")
                .set("border", "2px dashed #aaa")
                .set("box-sizing", "border-box");

        addClassName("schedule-drop-target");

        // Listener dla zdarzenia 'dragenter'
        getElement().addEventListener("dragenter", event -> {
            // Dodaj klasę, gdy przeciągany element wejdzie na DropTarget
            getElement().getClassList().add("schedule-drop-active");
        });

        // Listener dla zdarzenia 'dragleave'
        getElement().addEventListener("dragleave", event -> {
            // Usuń klasę, gdy przeciągany element opuści DropTarget
            getElement().getClassList().remove("schedule-drop-active");
        });

        // Ważne: Zdarzenie 'drop' samo w sobie nie powoduje automatycznego usunięcia klasy,
        // więc musimy to zrobić ręcznie po zakończeniu upuszczania.

        // *****************************************************************
        // Logika DropTarget (pozostaje zaimplementowana w Vaadin Java API)
        DropTarget<DailyDropTimeBar> dropTarget = DropTarget.create(this);
        dropTarget.setDropEffect(DropEffect.COPY);

        dropTarget.addDropListener(event -> {
            getElement().getClassList().remove("schedule-drop-active");

            event.getDragData().ifPresent(data -> {
                if (data instanceof ShiftResource droppedResource) {

                    // 1. Zidentyfikuj slot startowy z tego DropTarget
                    TimeSlot newShiftStartSlot = this.targetSlot;

                    // 2. Pobierz długość w slotach z przeciąganego obiektu
                    int newDurationInSlots = droppedResource.getDurationInSlots();

                    ShiftResource newShift =
                            new ShiftResource(droppedResource.getId(), newShiftStartSlot, newDurationInSlots);

                    day.addShift(newShift);

                    grid.getDataProvider().refreshAll();
                }
            });
        });
    }
}
