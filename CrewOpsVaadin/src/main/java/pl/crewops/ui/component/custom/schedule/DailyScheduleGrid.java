package pl.crewops.ui.component.custom.schedule;

import static pl.crewops.ui.component.custom.schedule.DailyScheduleGrid.intervalsPerDay;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import pl.crewops.enums.TimeSlot;

@CssImport("./styles/component/dailyView/schedule-grid.css")
class DailyScheduleGrid extends VerticalLayout {

    static final int intervalsPerDay = 96;

    private final List<ScheduleDay> dayList = new ArrayList<>();
    private int dayCounter = 1;

    private final Grid<ScheduleDay> grid = new Grid<>();
    private final Button addButton;

    public DailyScheduleGrid() {
        setWidthFull();

        addButton = new Button("Next day", e -> {
            nextDay(new ScheduleDay(++dayCounter));
        });

        grid.setAllRowsVisible(true);
        grid.setSelectionMode(SelectionMode.NONE);
        grid.setColumnReorderingAllowed(false);
        grid.setRowsDraggable(false);

        grid.addColumn(ScheduleDay::getDayNumber)
                .setHeader("Dzień")
                .setFlexGrow(0)
                .setAutoWidth(true)
                .setFrozen(true);

        // Zastępujemy 24 kolumny jedną złożoną kolumną
        grid.addComponentColumn(day -> new DayScheduleVisualization(day, grid)) // Używamy nowego komponentu
                .setHeader(createScheduleHeader())
                //                .setAutoWidth(true)
                .setFlexGrow(10);

        grid.addComponentColumn(this::createRemoveButton)
                .setFlexGrow(0)
                .setAutoWidth(true)
                .setFrozenToEnd(true);

        add(grid, addButton);

        nextDay(new ScheduleDay(1));
    }

    public void nextDay(ScheduleDay scheduleDay) {
        dayList.add(scheduleDay);
        grid.setItems(dayList);
    }

    public void removeDay() {
        dayList.removeLast();
        grid.setItems(dayList);
        dayCounter--;
    }

    private Component createRemoveButton(ScheduleDay day) {
        var removeRowButton = new Button();
        removeRowButton.addClickListener(event -> {
            removeDay();
        });
        removeRowButton.setIcon(VaadinIcon.TRASH.create());

        return removeRowButton;
    }

    private HorizontalLayout createScheduleHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setHeight("20px");
        header.setSpacing(false);
        header.setPadding(false);
        header.addClassName("schedule-header-row"); // Klasa CSS do opcjonalnego stylizowania nagłówka

        // Iterujemy przez 96 slotów
        for (int index = 0; index < intervalsPerDay; index++) {
            TimeSlot slot = TimeSlot.fromIndex(index);

            // Pełna godzina jest co 4 sloty (00:00, 01:00, 02:00 itd.)
            boolean isHourStart = index % 4 == 0;

            Div headerCell = new Div();
            // KLUCZOWY PUNKT: Każda komórka nagłówka musi mieć flex-grow: 1,
            // aby zajmowała dokładnie taką samą szerokość jak slot poniżej.
            headerCell.getStyle().set("flex-grow", "1");
            headerCell.getStyle().set("flex-shrink", "0");
            headerCell.getStyle().set("width", "auto");

            // Wyrównanie: tekst na początku komórki
            headerCell.getStyle().set("text-align", "left");
            headerCell.addClassName("schedule-header-cell");

            if (isHourStart) {
                // Generowanie etykiety godziny (np. "12")
                int hours = slot.getValue().hours(); // Zakładam, że TimeSlot.getValue() dostarcza hours()
                String formattedHour = String.valueOf(hours); // Format liczbowy (np. 12 zamiast 12:00)

                Span label = new Span(formattedHour);
                label.getStyle().set("font-size", "10px");
                label.getStyle().set("font-weight", "bold");

                headerCell.add(label);

                // Opcjonalnie: wizualne oddzielenie godzin
                headerCell.getStyle().set("border-left", "1px solid #ccc");
            }

            header.add(headerCell);
        }
        return header;
    }
}

@Getter
final class ScheduleDay {

    @Setter
    int dayNumber;

    // Dodana lista zasobów dla danego dnia
    private final List<ShiftResource> shifts = new ArrayList<>();

    public ScheduleDay(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public void addShift(ShiftResource shift) {
        // Możesz dodać logikę sprawdzania kolizji przed dodaniem
        this.shifts.add(shift);
    }
}

class DayScheduleVisualization extends VerticalLayout {

    private final ScheduleDay day;
    private final Grid<ScheduleDay> grid; // Potrzebny do odświeżania

    // Lista wierszy (pasów/ścieżek) w tym dniu
    private final List<HorizontalLayout> scheduleRows = new ArrayList<>();

    public DayScheduleVisualization(ScheduleDay day, Grid<ScheduleDay> grid) {
        this.day = day;
        this.grid = grid;

        setWidthFull();
        setSpacing(false);
        setPadding(false);
        addClassName("schedule-day-visualization"); // Klasa CSS dla kontenera dnia

        // Utwórz pierwszy (domyślny) wiersz pasma
        addNewShiftRow();

        // Na razie pomijamy logikę dodawania pustego pasma 'czyszczącego',
        // skupimy się na jednym pasie. Logikę dynamicznego dodawania pasów dodamy później.

        // Renderuj istniejące zmiany (na razie wszystkie w pierwszym pasie)
        renderSchedule();
    }

    public void renderSchedule() {
        // Logika renderowania staje się bardziej złożona w przypadku wielu pasów,
        // ale na początku wszystkie zmiany renderujemy w pierwszym pasie.

        // Wyczyść i przebuduj komponenty. W przyszłości to będzie zarządzane przez
        // dedykowaną logikę alokacji pasów.
        removeAll();
        scheduleRows.clear();

        // Zapewnij istnienie co najmniej jednego wiersza
        HorizontalLayout firstRow = createNewShiftRow(day);
        scheduleRows.add(firstRow);
        add(firstRow);

        // Upewnij się, że element DropTimeBar jest zawsze obecny na końcu,
        // aby można było upuścić na puste sloty.
    }

    // Metoda dodająca nowy, pusty wiersz (używany dynamicznie)
    private void addNewShiftRow() {
        HorizontalLayout newRow = createNewShiftRow(day);
        scheduleRows.add(newRow);
        add(newRow);
    }

    // --- KLUCZOWA METODA TWORZĄCA WERSZ 96 KOMPONENTÓW ---
    // W DayScheduleVisualization::createNewShiftRow

    private HorizontalLayout createNewShiftRow(ScheduleDay day) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setHeight("18px");
        row.setSpacing(false);
        row.setPadding(false);
        row.addClassName("schedule-shift-row");
        row.getStyle().set("display", "flex");
        row.getStyle().set("margin", "0");

        // Używamy tradycyjnej pętli FOR, aby móc manipulować licznikiem 'index'
        for (int index = 0; index < intervalsPerDay; index++) {

            TimeSlot currentSlot = TimeSlot.fromIndex(index);
            int currentSlotIndexGlobal = index;

            // 1. Sprawdzenie, czy slot jest zajęty
            ShiftResource existingShift = day.getShifts().stream()
                    .filter(shift -> {
                        int shiftStartIndex = shift.getStartSlot().getIndex();
                        int shiftEndIndex = shift.getEndSlotIndex();
                        return shiftStartIndex <= currentSlotIndexGlobal && shiftEndIndex > currentSlotIndexGlobal;
                    })
                    .findFirst()
                    .orElse(null);

            Component cellComponent;
            if (existingShift != null) {
                // JEŚLI JEST ZAJĘTY

                if (existingShift.getStartSlot().getIndex() == index) {
                    // JEST TO SLOT STARTOWY: Rysujemy DragTimeBar i rozciągamy go.
                    DragTimeBar dragBar = new DragTimeBar(existingShift);

                    int duration = existingShift.getDurationInSlots();

                    // Ustaw Flexbox, aby zajął odpowiednią liczbę slotów
                    dragBar.getStyle().set("flex-grow", String.valueOf(duration));
                    dragBar.getStyle().set("flex-shrink", "0");
                    dragBar.getStyle().set("width", "auto");

                    cellComponent = dragBar;

                    // KLUCZOWA LOGIKA: Przesuń licznik pętli, aby pominąć sloty środkowe.
                    // Odejmujemy 1, ponieważ na końcu pętli 'for' index i tak zostanie zwiększony.
                    index += duration - 1;

                } else {
                    // Slot zajęty, ale nie jest slotem startowym (jest to środek zmiany),
                    // więc kontynuujemy do następnego (slot zostanie zajęty przez flex-grow).
                    continue;
                }

            } else {
                // JEŚLI JEST WOLNY: Rysujemy DropTimeBar (cel upuszczania)
                DropTimeBar dropBar = new DropTimeBar(currentSlot, day, grid);

                dropBar.getStyle().set("flex-grow", "1"); // Zajmuje dokładnie 1 slot
                dropBar.getStyle().set("flex-shrink", "0");

                dropBar.addClassName("schedule-slot-drop-target");
                cellComponent = dropBar;
            }

            row.add(cellComponent);
        }

        return row;
    }
}
