package pl.crewops.ui.component.custom.schedule;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import pl.crewops.enums.TimeSlot;

public class DailyScheduleGenerator extends VerticalLayout {

    private final DailyScheduleGrid grid = new DailyScheduleGrid();

    public DailyScheduleGenerator() {
        setSizeFull();

        HorizontalLayout palette = new HorizontalLayout(
                new Div("Drag target test components:"),
                // Zmieniamy wywołania na nową metodę
                createShiftDragSource("Test A", "A", 30), // Np. 60 minut
                createShiftDragSource("Test B", "B", 30) // Np. 30 minut
                );

        // Uzupełnienie logiki przycisku
        add(palette, grid);
    }

    public Component createShiftDragSource(String label, String dataValue, int durationMinutes) {
        // 1. Obliczenie durationInSlots
        // Długość w minutach (np. 30, 60) dzielona przez 15 minut na slot.
        // Używamy stałej intervalDurationMinutes = 15 z DailyScheduleGrid.
        int intervalDurationMinutes = 15; // Zakładamy dostęp lub używamy stałej z DailyScheduleGrid

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
        ShiftResource resource = new ShiftResource(dataValue, startSlotForPalette, durationInSlots);

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

    // Usuń starą metodę createDragSourceComponent(String label, String dataValue)
}
