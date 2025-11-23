package pl.crewops.ui.component.custom.schedule;

import static pl.crewops.ui.component.custom.schedule.DailyScheduleGrid.intervalsPerDay;

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
import java.util.Comparator;
import java.util.List;
import lombok.Getter;
import pl.crewops.enums.TimeSlot;

@CssImport("./styles/component/dailyView/schedule-grid.css")
class DailyScheduleGrid extends VerticalLayout {

    static final int intervalsPerDay = 96;

    private final List<ScheduleDay> dayList = new ArrayList<>();

    @Getter
    private final Grid<ScheduleDay> grid = new Grid<>();
    // todo i18n
    private final Button addButton = new Button("Next day");
    private final Button removeButton = new Button("Remove day");

    private int dayCounter = 1;

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

        grid.addComponentColumn(DayScheduleVisualization::new)
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
                String halfHourPresentation = "30";

                Span label = new Span(halfHourPresentation);
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

class DayScheduleVisualization extends VerticalLayout {

    private final ScheduleDay day;

    private final List<HorizontalLayout> rows = new ArrayList<>();

    public DayScheduleVisualization(ScheduleDay day) {
        this.day = day;

        setWidthFull();
        setSpacing(false);
        setPadding(false);
        addClassName("schedule-day-visualization");

        renderSchedule();
    }

    private void renderSchedule() {
        removeAll();

        List<ShiftResource> shifts = day.getShifts();

        // this line create a kind of matrix structure with package mechanism that try to put any ShiftResource to
        // any empty space in existing Drop Target Track (row of drop target's) and if can't create a new row
        List<List<ShiftResource>> packedRows = packShiftsIntoRows(shifts);

        // then after packaging create rows with shifts data
        packedRows.stream()
                .map(this::createShiftContentRow)
                .forEach(this::add); // this mean add to component parent which is VerticalLayout

        var dropTrack = createDropTargetTrack();

        add(dropTrack);
    }

    private HorizontalLayout createShiftContentRow(List<ShiftResource> rowShifts) {
        HorizontalLayout row = createBaseRow();

        rowShifts.sort(Comparator.comparingInt(ShiftResource::getStartSlotIndex));

        int currentSlotIndex = 0;

        for (ShiftResource shift : rowShifts) {
            int shiftStart = shift.getStartSlotIndex();
            int shiftEnd = shift.getEndSlotIndex();

            // 1. Puste sloty PRZED obecną zmianą
            if (shiftStart > currentSlotIndex) {
                for (int i = currentSlotIndex; i < shiftStart; i++) {
                    // PRZEKAZUJEMY PRAWIDŁOWY INDEKS: i
                    row.add(createEmptySlot(i, false));
                }
            }

            // 2. Blok zmiany
            row.add(createShiftBar(shift));

            // Uaktualniamy wskaźnik do końca obecnej zmiany
            currentSlotIndex = shiftEnd;
        }

        // 3. Puste sloty PO ostatniej zmianie (aż do końca dnia)
        for (int i = currentSlotIndex; i < DailyScheduleGrid.intervalsPerDay; i++) {
            // PRZEKAZUJEMY PRAWIDŁOWY INDEKS: i
            row.add(createEmptySlot(i, false));
        }

        return row;
    }

    private HorizontalLayout createDropTargetTrack() {
        HorizontalLayout row = createBaseRow();

        for (int index = 0; index < DailyScheduleGrid.intervalsPerDay; index++) {
            var dropBar = new ShiftResourceDropBar(day, index);

            dropBar.applyStyles(true, 1);

            dropBar.addDropListener(e -> renderSchedule());

            row.add(dropBar);
        }

        return row;
    }

    private HorizontalLayout createBaseRow() {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setHeight("30px");
        row.setSpacing(false);
        row.setPadding(false);
        row.addClassName("schedule-shift-row");
        row.getStyle().set("display", "flex");
        row.getStyle().set("margin", "0");
        return row;
    }

    private Div createShiftBar(ShiftResource shift) {
        var dropBar = new ShiftResourceDropBar(day, shift.getStartSlotIndex());
        dropBar.setDroppedResource(shift);

        int duration = shift.getDurationInSlots();

        dropBar.applyStyles(false, duration);

        return dropBar;
    }

    private Div createEmptySlot(int index, boolean isDropTargetTrack) {
        var dropBar = new ShiftResourceDropBar(day, index);
        dropBar.setDroppedResource(null);

        dropBar.applyStyles(isDropTargetTrack, 1);

        return dropBar;
    }

    private List<List<ShiftResource>> packShiftsIntoRows(List<ShiftResource> allShifts) {
        if (allShifts.isEmpty()) {
            return new ArrayList<>();
        }

        allShifts.sort(Comparator.comparingInt(ShiftResource::getStartSlotIndex));

        List<List<ShiftResource>> rows = new ArrayList<>();

        for (ShiftResource newShift : allShifts) {
            boolean requireNewRow = true;

            for (List<ShiftResource> alreadyExistingRow : rows) {
                if (!shiftsOverlap(alreadyExistingRow, newShift)) {
                    alreadyExistingRow.add(newShift);
                    requireNewRow = false;
                    break;
                }
            }

            if (requireNewRow) {
                List<ShiftResource> newRow = new ArrayList<>();
                newRow.add(newShift);
                rows.add(newRow);
            }
        }
        return rows;
    }

    private boolean shiftsOverlap(List<ShiftResource> existingShifts, ShiftResource newShift) {
        int start1 = newShift.getStartSlotIndex();
        int end1 = newShift.getEndSlotIndex();

        for (ShiftResource existing : existingShifts) {
            int start2 = existing.getStartSlotIndex();
            int end2 = existing.getEndSlotIndex();

            // Nakładanie zachodzi, jeśli (Start1 < End2) i (End1 > Start2)
            if (start1 < end2 && end1 > start2) {
                return true;
            }
        }
        return false;
    }
}
