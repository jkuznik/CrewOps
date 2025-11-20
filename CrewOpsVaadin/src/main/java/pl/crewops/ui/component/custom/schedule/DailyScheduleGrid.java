package pl.crewops.ui.component.custom.schedule;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import lombok.Getter;
import lombok.Setter;

@CssImport("./styles/component/dailyView/schedule-grid.css")
class DailyScheduleGrid extends VerticalLayout {

    static final int intervalsPerDay = 24;
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

        grid.addColumn(ScheduleDay::getDayNumber).setHeader("Dzień").setFrozen(true);

        IntStream.range(0, intervalsPerDay).forEach(index -> grid.addComponentColumn(day -> createCell(day, index))
                .setHeader(index + ":00"));

        grid.addComponentColumn(this::createRemoveButton).setHeader("").setFrozenToEnd(true);

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

    private Component createShiftDisplay(ScheduleDay day) {
        HorizontalLayout container = new HorizontalLayout();
        container.setWidthFull();
        container.setSpacing(false); // Ważne, aby nie było odstępów

        // Dodanie wszystkich przesunięć jako DragTimeBar
        day.getShifts().forEach(shift -> container.add(new DragTimeBar(shift)));

        return container;
    }

    // Wewnątrz DailyScheduleGrid

    private Component createCell(ScheduleDay day, int hourIndex) {
        // 1. Definicja okna czasowego (potrzebna obu komponentom)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), hourIndex, 0);
        LocalDateTime to = from.plusMinutes(60);

        // 2. Wyszukanie istniejącej zmiany dla TEJ konkretnej godziny
        ShiftResource existingShift = day.getShifts().stream()
                .filter(shift -> shift.getFrom().getHour() == hourIndex)
                .findFirst()
                .orElse(null);

        if (existingShift != null) {
            // ZMIANA ZNAJDUJE SIĘ W TEJ GODZINIE:
            // Zwracamy komponent, który reprezentuje ZMIANĘ (nie jest celem upuszczania)
            DragTimeBar dragBar = new DragTimeBar(existingShift);
            dragBar.setText(existingShift.getType());

            // Możesz dodać style, aby lepiej wyglądał w siatce
            dragBar.getStyle()
                    .set("background-color", "#3e70d6")
                    .set("border", "1px solid #3e70d6")
                    .set("box-sizing", "border-box")
                    .set("margin", "-1");

            return dragBar;

        } else {
            // BRAK ZMIANY:
            // Zwracamy komponent, który jest celem upuszczania
            DropTimeBar dropBar = new DropTimeBar(from, to, day, grid);
            dropBar.addClassName("drop-cell-time-bar");

            return dropBar;
        }
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
