package pl.crewops.ui.component.custom.schedule;

import static pl.crewops.ui.component.custom.schedule.DailyScheduleGrid.INTERVALS_PER_DAY;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dnd.*;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import pl.crewops.enums.TimeSlot;

@CssImport("./styles/component/dailyView/schedule-grid.css")
class DailyScheduleGrid extends VerticalLayout {

    public static final int INTERVALS_PER_DAY = 96;

    private final List<ScheduleDay> dayList = new ArrayList<>();

    @Getter
    private final Grid<ScheduleDay> grid = new Grid<>();

    private final Button addButton = new Button(getTranslation("dailyScheduleGrid.nextDayButton"));
    private final Button removeButton = new Button(getTranslation("dailyScheduleGrid.removeDayButton"));

    private int dayCounter = 1;

    public DailyScheduleGrid() {
        setWidthFull();

        grid.setAllRowsVisible(true);
        grid.setSelectionMode(SelectionMode.NONE);
        grid.setColumnReorderingAllowed(false);
        grid.setRowsDraggable(false);

        grid.addColumn(ScheduleDay::getDayNumber)
                .setHeader(getTranslation("dailyScheduleGrid.day"))
                .setFlexGrow(0)
                .setAutoWidth(true)
                .setFrozen(true);

        grid.addComponentColumn(scheduleDay -> {
                    DayScheduleVisualization visualization = new DayScheduleVisualization(scheduleDay);

                    visualization.addShiftCrossesMidnightEvent(event -> {
                        int currentDayNumber = event.getSource().getDay().getDayNumber();
                        int nextDayNumber = currentDayNumber + 1;
                        ScheduleDay nextDay;
                        if (!hasDay(nextDayNumber)) {
                            nextDay = new ScheduleDay(nextDayNumber);
                            addDay(nextDay);
                        } else {
                            nextDay = dayList.stream()
                                    .filter(d -> d.getDayNumber() == nextDayNumber)
                                    .findFirst()
                                    .orElse(new ScheduleDay(nextDayNumber));
                        }

                        var originalShift = event.getShift();
                        originalShift.setHasCrossMidnightSegment(true);

                        Optional<ShiftResource> crossMidnightShift = nextDay.getShifts().stream()
                                .filter(shift -> shift.getShiftDTO()
                                        .id()
                                        .equals(originalShift.getShiftDTO().id()))
                                .findFirst();

                        if (crossMidnightShift.isPresent()) {
                            var nextDaySegment = crossMidnightShift.get();
                            nextDaySegment.setDurationInSlots(originalShift.getNextDayEndSlotForShift());

                        } else {
                            var nextDaySegment = new ShiftResource(originalShift.getShiftDTO());
                            nextDaySegment.setStartSlot(TimeSlot.H00_00);
                            nextDaySegment.setDurationInSlots(originalShift.getNextDayEndSlotForShift());

                            nextDaySegment.setCrossMidnightSegment(true);

                            nextDay.getShifts().add(nextDaySegment);
                        }
                        grid.setItems(dayList);
                    });

                    visualization.addShiftNoLongerCrossesMidnightEvent(event -> {
                        int currentDayNumber = event.getSource().getDay().getDayNumber();
                        int nextDayNumber = currentDayNumber + 1;

                        var originalShift = event.getShift();

                        dayList.stream()
                                .filter(d -> d.getDayNumber() == nextDayNumber)
                                .findFirst()
                                .ifPresent(nextDay -> {
                                    nextDay.getShifts()
                                            .removeIf(shift ->
                                                    shift.isCrossMidnightSegment() && shift.equals(originalShift));

                                    if (nextDay.getShifts().isEmpty()) {
                                        removeDay(nextDayNumber);
                                    }
                                });

                        originalShift.setHasCrossMidnightSegment(false);
                        grid.setItems(dayList);
                    });

                    visualization.addShiftResizeListener(event -> {
                        dayList.stream()
                                .flatMap(d -> d.getShifts().stream())
                                .filter(shiftResource -> event.getShift().equals(shiftResource))
                                .forEach(shiftResource -> {
                                    shiftResource.setDurationInSlots(
                                            shiftResource.getDurationInSlots() + event.getValue());
                                });
                        grid.setItems(dayList);
                    });

                    return visualization;
                })
                .setHeader(createScheduleHeader())
                .setFlexGrow(10);

        add(grid, configuredButtons());

        addDay(new ScheduleDay(dayCounter));
    }

    private HorizontalLayout configuredButtons() {
        addButton.setWidth("50%");
        removeButton.setWidth("50%");

        addButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        addButton.addClickListener(e -> {
            addDay(new ScheduleDay(++dayCounter));
        });

        removeButton.addThemeVariants(ButtonVariant.LUMO_WARNING);
        removeButton.addClickListener(event -> {
            removeDay(dayCounter);
        });
        var buttonContainer = new HorizontalLayout();
        buttonContainer.setWidthFull();
        buttonContainer.setSpacing(true);
        buttonContainer.setPadding(true);
        buttonContainer.add(addButton, removeButton);

        return buttonContainer;
    }

    public void addDay(ScheduleDay scheduleDay) {
        dayList.add(scheduleDay);
        grid.setItems(dayList);
    }

    public void removeDay(int dayNumber) {
        ScheduleDay dayToRemove = dayList.stream()
                .filter(d -> d.getDayNumber() == dayNumber)
                .findFirst()
                .orElse(null);

        if (dayToRemove != null) {
            boolean hasRealShifts = dayToRemove.getShifts().stream().anyMatch(shift -> !shift.isCrossMidnightSegment());

            if (!hasRealShifts) {
                dayList.remove(dayToRemove);
                grid.setItems(dayList);

                if (dayNumber == dayCounter) {
                    dayCounter--;
                }
            }
        }
    }

    public boolean hasDay(int dayNumber) {
        return dayList.stream().anyMatch(d -> d.getDayNumber() == dayNumber);
    }

    private HorizontalLayout createScheduleHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setSpacing(false);
        header.setPadding(false);

        for (int index = 0; index < INTERVALS_PER_DAY; index++) {
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
                headerCell.getStyle().set("flex-grow", "1");
                headerCell.getStyle().set("flex-shrink", "0");
                headerCell.getStyle().set("width", "auto");

                headerCell.getStyle().set("text-align", "left");
                headerCell.addClassName("schedule-header-cell");
                header.add(headerCell);
            }
        }
        return header;
    }
}

@CssImport("./styles/component/dailyView/daily-schedule-visualization.css")
@Slf4j
class DayScheduleVisualization extends VerticalLayout {

    @Getter
    private final ScheduleDay day;

    private final List<Registration> listeners = new ArrayList<>();

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
        listeners.forEach(Registration::remove);
        listeners.clear();

        List<ShiftResource> shifts = day.getShifts();

        // this line create a kind of matrix structure with package mechanism that try to put any ShiftResource to
        // any empty space in existing Drop Target Track (row of drop target's) and if can't create a new row
        List<List<ShiftResource>> packedRows = packShiftsIntoRows(shifts);

        // then after packaging create rows with shifts data
        packedRows.stream()
                .map(this::createShiftDnDRow)
                .forEach(this::add); // this mean add to component parent which is VerticalLayout

        var dropTrack = createEmptyDropTargetRow();

        add(dropTrack);
    }

    private HorizontalLayout createShiftDnDRow(List<ShiftResource> rowShifts) {
        HorizontalLayout row = createBaseRow();

        rowShifts.sort(Comparator.comparingInt(ShiftResource::getStartSlotIndex));

        int index = 0;

        for (ShiftResource shift : rowShifts) {
            int shiftStartIndex = shift.getStartSlotIndex();
            int shiftEndIndex = shift.getEndSlotIndex();
            var isCurrentShiftEnd = false;

            for (; index < INTERVALS_PER_DAY; index++) {
                if (index == shiftStartIndex) {
                    for (; index < shiftEndIndex; index++) {
                        if (index == INTERVALS_PER_DAY) {
                            break;
                        }
                        row.add(createShiftSlottedBar(shift, index));
                    }
                    index--;
                    isCurrentShiftEnd = true;
                } else {
                    if (isCurrentShiftEnd && !rowShifts.getLast().equals(shift)) {
                        break;
                    }
                    row.add(createEmptySlot(index));
                }
            }
        }

        return row;
    }

    private Div createDragShiftSlot(ShiftResource shift) {
        if (shift.isCrossMidnightSegment()) {
            var zone = new Div();
            zone.setWidthFull();
            zone.setHeightFull();
            zone.getStyle().set("z-index", "1");
            return zone;
        }

        var zone = new Div();
        zone.setWidthFull();
        zone.setHeightFull();
        zone.getStyle()
                .set("position", "absolute")
                .set("top", "0")
                .set("left", "0")
                .set("cursor", "grab")
                .set("background-color", "transparent")
                .set("z-index", "4");

        // KONFIGURACJA DRAG SOURCE DLA PRZENOSZENIA
        DragSource<Div> dragSource = DragSource.create(zone);
        dragSource.setDragData(shift);
        dragSource.setEffectAllowed(EffectAllowed.MOVE);

        Registration dragStartListener = dragSource.addDragStartListener(event -> {
            Div sourceDiv = event.getSource();
            sourceDiv.getStyle().set("visibility", "hidden");
        });
        listeners.add(dragStartListener);

        Registration dragEndListener = dragSource.addDragEndListener(event -> {
            Div sourceDiv = event.getSource();
            sourceDiv.getStyle().set("visibility", "visible");
            renderSchedule();
        });
        listeners.add(dragEndListener);

        // Zatrzymanie propagacji
        zone.getElement()
                .executeJs("this.addEventListener('mousedown', function(e) { " + "    e.stopPropagation(); "
                        + "}, true);");

        return zone;
    }

    private Div createShiftSlottedBar(ShiftResource shift, int index) {

        var dropBar = new ShiftResourceDropBar(day, index);

        Registration crossMidnightEvent = dropBar.addCrossMidnightEvent(event -> {
            fireEvent(new ShiftCrossesMidnightEvent(this, event.getShiftResource()));
        });
        listeners.add(crossMidnightEvent);
        Registration shiftNoLongerCrossesMidnightEvent = dropBar.addShiftNoLongerCrossesMidnightEvent(event -> {
            fireEvent(new ShiftNoLongerCrossesMidnightEvent(this, event.getShiftResource()));
        });
        listeners.add(shiftNoLongerCrossesMidnightEvent);
        Registration crossMidnightResizeEvent = dropBar.addCrossMidnightResizeEvent(event -> {
            fireEvent(new ShiftResizeEvent(this, event.getShiftResource(), event.getValue()));
        });
        listeners.add(crossMidnightResizeEvent);

        dropBar.setDroppedResource(shift);
        dropBar.applyStyles(1);
        dropBar.getStyle().set("position", "relative");

        dropBar.add(createDragShiftSlot(shift));

        boolean isCrossMidnightSegment = shift.isCrossMidnightSegment();

        if (index == shift.getStartSlotIndex() && !isCrossMidnightSegment) {
            ShiftResourceResizeBar shiftResourceResizeBar =
                    new ShiftResourceResizeBar(shift, ShiftResourceResizeBar.ResizeEdge.START);
            Registration dragEndListener = shiftResourceResizeBar.addDragEndListener(event -> {
                renderSchedule();
            });
            listeners.add(dragEndListener);
            dropBar.add(shiftResourceResizeBar);
        }

        if (index == shift.getEndSlotIndex() - 1) {
            ShiftResourceResizeBar shiftResourceResizeBar =
                    new ShiftResourceResizeBar(shift, ShiftResourceResizeBar.ResizeEdge.END);
            Registration dragEndListener = shiftResourceResizeBar.addDragEndListener(event -> {
                renderSchedule();
            });
            listeners.add(dragEndListener);
            dropBar.add(shiftResourceResizeBar);
        }

        return dropBar;
    }

    private Div createEmptySlot(int index) {
        var dropBar = new ShiftResourceDropBar(day, index);

        dropBar.setDroppedResource(null);
        Registration crossMidnightEvent = dropBar.addCrossMidnightEvent(event -> {
            fireEvent(new ShiftCrossesMidnightEvent(this, event.getShiftResource()));
        });
        listeners.add(crossMidnightEvent);
        Registration shiftNoLongerCrossesMidnightEvent = dropBar.addShiftNoLongerCrossesMidnightEvent(event -> {
            fireEvent(new ShiftNoLongerCrossesMidnightEvent(this, event.getShiftResource()));
        });
        listeners.add(shiftNoLongerCrossesMidnightEvent);
        Registration crossMidnightResizeEvent = dropBar.addCrossMidnightResizeEvent(event -> {
            fireEvent(new ShiftResizeEvent(this, event.getShiftResource(), event.getValue()));
        });
        listeners.add(crossMidnightResizeEvent);

        dropBar.applyStyles(1);

        return dropBar;
    }

    private HorizontalLayout createEmptyDropTargetRow() {
        var row = createBaseRow();

        for (int index = 0; index < INTERVALS_PER_DAY; index++) {
            row.add(createEmptySlot(index));
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

    private List<List<ShiftResource>> packShiftsIntoRows(List<ShiftResource> allShifts) {
        if (allShifts.isEmpty()) {
            return new ArrayList<>();
        }

        allShifts.sort(Comparator.comparingInt(ShiftResource::getBeforeMoveStartSlot));

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

            if (start1 < end2 && end1 > start2) {
                return true;
            }
        }
        return false;
    }

    abstract static class DayScheduleVisualisationEvent extends ComponentEvent<DayScheduleVisualization> {
        @Getter
        private final ShiftResource shift;

        public DayScheduleVisualisationEvent(DayScheduleVisualization source, ShiftResource shift) {
            super(source, false);
            this.shift = shift;
        }
    }

    static class ShiftCrossesMidnightEvent extends DayScheduleVisualisationEvent {
        public ShiftCrossesMidnightEvent(DayScheduleVisualization source, ShiftResource shift) {
            super(source, shift);
        }
    }

    static class ShiftNoLongerCrossesMidnightEvent extends DayScheduleVisualisationEvent {
        public ShiftNoLongerCrossesMidnightEvent(DayScheduleVisualization source, ShiftResource shift) {
            super(source, shift);
        }
    }

    static class ShiftResizeEvent extends DayScheduleVisualisationEvent {
        @Getter
        private final int value;

        public ShiftResizeEvent(DayScheduleVisualization source, ShiftResource shift, int value) {
            super(source, shift);
            this.value = value;
        }
    }

    public Registration addShiftCrossesMidnightEvent(ComponentEventListener<ShiftCrossesMidnightEvent> listener) {
        return addListener(ShiftCrossesMidnightEvent.class, listener);
    }

    public Registration addShiftNoLongerCrossesMidnightEvent(
            ComponentEventListener<ShiftNoLongerCrossesMidnightEvent> listener) {
        return addListener(ShiftNoLongerCrossesMidnightEvent.class, listener);
    }

    public Registration addShiftResizeListener(ComponentEventListener<ShiftResizeEvent> listener) {
        return addListener(ShiftResizeEvent.class, listener);
    }
}
