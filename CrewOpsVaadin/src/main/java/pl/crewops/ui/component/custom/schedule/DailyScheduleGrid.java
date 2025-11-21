package pl.crewops.ui.component.custom.schedule;

import static pl.crewops.ui.component.custom.schedule.DailyScheduleGrid.intervalsPerDay;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
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
    // todo i18n
    private final Button addButton = new Button("Next day");
    private final Button removeButton = new Button("Remove day");

    public DailyScheduleGrid() {
        setWidthFull();

        grid.setAllRowsVisible(true);
        grid.setSelectionMode(SelectionMode.NONE);
        grid.setColumnReorderingAllowed(false);
        grid.setRowsDraggable(false);

        grid.addColumn(ScheduleDay::getDayNumber)
                .setHeader("Dzień")
                .setFlexGrow(0)
                .setAutoWidth(true)
                .setFrozen(true);

        grid.addComponentColumn(day -> new DayScheduleVisualization(day, grid))
                .setHeader(createScheduleHeader())
                .setFlexGrow(10);

        add(grid, configuredButtons());

        nextDay(new ScheduleDay(dayCounter));
    }

    private HorizontalLayout configuredButtons() {
        addButton.setWidth("50%");
        removeButton.setWidth("50%");

        addButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        addButton.addClickListener(e -> {
            nextDay(new ScheduleDay(++dayCounter));
        });

        removeButton.addThemeVariants(ButtonVariant.LUMO_WARNING);
        removeButton.addClickListener(event -> {
            removeDay();
        });
        var buttonContainer = new HorizontalLayout();
        buttonContainer.setWidthFull();
        buttonContainer.setSpacing(true);
        buttonContainer.setPadding(true);
        buttonContainer.add(addButton, removeButton);

        return buttonContainer;
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

    private HorizontalLayout createScheduleHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setSpacing(false);
        header.setPadding(false);

        for (int index = 0; index < intervalsPerDay; index++) {
            TimeSlot slot = TimeSlot.fromIndex(index);

            if (index % 4 == 0) {
                int hours = slot.getValue().hours();
                String formattedHour = String.valueOf(hours);

                Span label = new Span(formattedHour);
                label.getStyle().set("font-size", "15px");
                label.getStyle().set("font-weight", "bold");

                header.add(label);
            } else if (index % 4 == 2) {
                String formattedHour = "30";

                Span label = new Span(formattedHour);
                label.getStyle().set("font-size", "10px");
                label.getStyle().set("font-weight", "bold");

                header.add(label);
            } else {
                Div headerCell = new Div();
                // KLUCZOWY PUNKT: Każda komórka nagłówka musi mieć flex-grow: 1,
                // aby zajmowała dokładnie taką samą szerokość jak slot poniżej.
                headerCell.getStyle().set("flex-grow", "1");
                headerCell.getStyle().set("flex-shrink", "0");
                headerCell.getStyle().set("width", "auto");

                // Wyrównanie: tekst na początku komórki
                headerCell.getStyle().set("text-align", "left");
                headerCell.addClassName("schedule-header-cell");
                header.add(headerCell);
            }
        }
        return header;
    }
}

@Getter
final class ScheduleDay {

    @Setter
    int dayNumber;

    private final List<ShiftResource> shifts = new ArrayList<>();

    public ScheduleDay(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public void addShift(ShiftResource shift) {
        this.shifts.add(shift);
    }
}

class DayScheduleVisualization extends VerticalLayout {

    private final ScheduleDay day;
    private final Grid<ScheduleDay> grid; // Potrzebny do odświeżania

    private final List<HorizontalLayout> scheduleRows = new ArrayList<>();

    public DayScheduleVisualization(ScheduleDay day, Grid<ScheduleDay> grid) {
        this.day = day;
        this.grid = grid;

        setWidthFull();
        setSpacing(false);
        setPadding(false);
        addClassName("schedule-day-visualization");

        renderSchedule();

        addNewShiftRow();
    }

    public void renderSchedule() {
        removeAll();
        scheduleRows.clear();
    }

    private void addNewShiftRow() {
        HorizontalLayout newRow = createNewShiftRow();
        scheduleRows.add(newRow);
        add(newRow);
    }

    private HorizontalLayout createNewShiftRow() {
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
                        int shiftStartIndex = shift.getStartSlotIndex();
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
                DailyDropTimeBar dropBar = new DailyDropTimeBar(currentSlot, day, grid);

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
