package pl.crewops.ui.component.custom.schedule;

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

        grid.addComponentColumn(scheduleDay -> {
                    DayScheduleVisualization visualization = new DayScheduleVisualization(scheduleDay);

                    visualization.addShiftCrossesMidnightEvent(event -> {
                        int currentDayNumber = event.getSource().getDay().getDayNumber();
                        int nextDayNumber = currentDayNumber + 1;
                        if (!hasDay(nextDayNumber)) {
                            nextDay(new ScheduleDay(nextDayNumber));
                        }

                        var nextDay = dayList.stream()
                                .filter(d -> d.getDayNumber() == nextDayNumber)
                                .findFirst()
                                .orElse(null);

                        // todo: poprawa zachowania jeśli wystąpił cross midnight event to shift resource
                        //  z flaga isCrossMidnight musi reagować na przesunięcia orginalnego ShiftResource
                        if (nextDay != null) {
                            var originalShift = event.getShift();
                            boolean segmentExists = nextDay.getShifts().stream()
                                    .anyMatch(s -> s.isCrossMidnightSegment()
                                            && s.getShiftDTO().equals(originalShift.getShiftDTO()));

                            if (!segmentExists) {
                                var nextDaySegment = new ShiftResource(originalShift.getShiftDTO());
                                nextDaySegment.setStartSlot(TimeSlot.H00_00);
                                nextDaySegment.setDurationInSlots(
                                        originalShift.getStartSlot().getIndex()
                                                + originalShift.getDurationInSlots()
                                                - 96);
                                nextDaySegment.setCrossMidnightSegment(true);
                                nextDay.getShifts().add(nextDaySegment);

                                grid.setItems(dayList);
                            }
                        }
                    });

                    visualization.addShiftNoLongerCrossesMidnightEvent(event -> {
                        int currentDayNumber = event.getSource().getDay().getDayNumber();
                        int nextDayNumber = currentDayNumber + 1;
                        ShiftResource originalShift = event.getShift();

                        dayList.stream()
                                .filter(d -> d.getDayNumber() == nextDayNumber)
                                .findFirst()
                                .ifPresent(nextDay -> {
                                    nextDay.getShifts()
                                            .removeIf(s -> s.isCrossMidnightSegment()
                                                    && s.getShiftDTO().equals(originalShift.getShiftDTO()));
                                    // Odświeżenie Grid
                                    grid.setItems(dayList);
                                });

                        removeDay(nextDayNumber);
                    });

                    return visualization;
                })
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
            removeDay(dayCounter);
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

    // DODAJ TĘ NOWĄ METODĘ
    public void removeDay(int dayNumber) {
        ScheduleDay dayToRemove = dayList.stream()
                .filter(d -> d.getDayNumber() == dayNumber)
                .findFirst()
                .orElse(null);

        if (dayToRemove != null) {
            // Sprawdzanie bezpieczeństwa: Czy dzień ma jakieś 'prawdziwe' zmiany, które nie są tylko wizualizacją?
            boolean hasRealShifts = dayToRemove.getShifts().stream()
                    // Używamy zaimplementowanej wcześniej flagi
                    .anyMatch(shift -> !shift.isCrossMidnightSegment());

            // Usuwamy tylko, jeśli dzień jest pusty (nie ma własnych, prawdziwych zmian)
            if (!hasRealShifts) {
                dayList.remove(dayToRemove);
                grid.setItems(dayList);

                // Aktualizacja dayCounter w sekwencyjnym systemie
                // Jeśli usunęliśmy ten dzień, który był ostatni w kolekcji (czyli dayCounter), musimy go
                // zdekrementować.
                if (dayNumber == dayCounter) {
                    dayCounter--;
                }
                System.out.println("LOG: Dzień " + dayNumber + " usunięty automatycznie.");
            } else {
                // Dzień jest potrzebny ze względu na inne zmiany
                System.out.println("LOG: Anulowano usunięcie Dnia " + dayNumber + ". Posiada inne przypisane zmiany.");
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

class DayScheduleVisualization extends VerticalLayout {

    @Getter
    private final ScheduleDay day;

    private boolean wasMidnightCrossed = false;

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

        // NOWE: Sprawdzanie kolizji i wywoływanie eventów (dodawanie/usuwanie dni)
        checkMidnightCrossingsAndFireEvents();

        List<ShiftResource> shifts = day.getShifts();

        // this line create a kind of matrix structure with package mechanism that try to put any ShiftResource to
        // any empty space in existing Drop Target Track (row of drop target's) and if can't create a new row
        List<List<ShiftResource>> packedRows = packShiftsIntoRows(shifts);

        // then after packaging create rows with shifts data
        packedRows.stream()
                .map(this::createShiftContentRow)
                .forEach(this::add); // this mean add to component parent which is VerticalLayout

        var dropTrack = createEmptyDropTargetRow();

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
                    row.add(createEmptySlot(i));
                }
            }

            // 2. Blok zmiany (teraz podzielony na sloty)
            createShiftSlottedBar(shift).forEach(row::add);

            // Uaktualniamy wskaźnik do końca obecnej zmiany
            currentSlotIndex = shiftEnd;
        }

        // 3. Puste sloty PO ostatniej zmianie (aż do końca dnia)
        for (int i = currentSlotIndex; i < DailyScheduleGrid.intervalsPerDay; i++) {
            row.add(createEmptySlot(i));
        }

        return row;
    }

    // W KLASIE DayScheduleVisualization
    private Div createMoveZoneOverlay(ShiftResource shift) {
        // 1. BLOKADA PRZENOSZENIA DLA SEGMENTÓW WIZUALNYCH
        if (shift.isCrossMidnightSegment()) {
            // Jeśli to jest segment wizualny, nie chcemy, aby można go było przenosić.
            var zone = new Div();
            zone.setWidthFull();
            zone.setHeightFull();
            // Ustawiamy z-index nisko, aby nie kolidowało, ale nie dodajemy logiki drag and drop.
            zone.getStyle().set("z-index", "1");
            return zone;
        }

        // 2. STANDARDOWA LOGIKA PRZENOSZENIA DLA 'PRAWDZIWYCH' SHIFTÓW
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

        // Słuchacze: Ukrywanie Div, który jest źródłem przeciągania
        dragSource.addDragStartListener(event -> {
            Div sourceDiv = event.getSource();
            sourceDiv.getStyle().set("visibility", "hidden");
        });

        dragSource.addDragEndListener(event -> {
            Div sourceDiv = event.getSource();
            sourceDiv.getStyle().set("visibility", "visible");
        });

        // Zatrzymanie propagacji
        zone.getElement()
                .executeJs("this.addEventListener('mousedown', function(e) { " + "    e.stopPropagation(); "
                        + "}, true);");

        return zone;
    }

    // W KLASIE DayScheduleVisualization
    private List<Div> createShiftSlottedBar(ShiftResource shift) {
        List<Div> shiftSlots = new ArrayList<>();

        int startSlotIndex = shift.getStartSlotIndex();
        int duration = shift.getDurationInSlots();

        // NOWA LOGIKA: Flaga kontrolująca, czy to segment wizualny
        boolean isCrossMidnightSegment = shift.isCrossMidnightSegment();

        for (int i = 0; i < duration; i++) {
            int currentSlotIndex = startSlotIndex + i;
            // WAŻNE: DropBar nadal musi mieć referencję do ScheduleDay,
            // ale musi być świadomy, że to element wizualny (później do modyfikacji DropBar)
            var dropBar = new ShiftResourceDropBar(day, currentSlotIndex);

            // Jeśli to jest segment, przypisujemy mu oryginalny ShiftResource,
            // aby ResizeHandle mógł znaleźć pierwotny Shift (w Dniu N).
            // Zakładamy, że ShiftResource ma metodę do pobrania oryginalnego DTO.
            dropBar.setDroppedResource(shift);
            dropBar.applyStyles(1);

            dropBar.getStyle().set("background-color", "#3e70d6");
            dropBar.getStyle().set("border", "none");
            dropBar.getStyle().set("position", "relative");

            // --- KONFIGURACJA D&D I UCHWYTÓW ---

            // A. Uchwyt Przenoszenia (MOVE): Zablokowany dla segmentów w createMoveZoneOverlay.
            dropBar.add(createMoveZoneOverlay(shift));

            // B. Uchwyty Rozciągania (RESIZE): Z-index 15.

            // Krawędź START (Lewy uchwyt): Tylko na Pierwszym Slocie
            // NOWA LOGIKA: BLOKUJEMY CHWYTAK, JEŚLI TO SEGMENT PRZEKRACZAJĄCY PÓŁNOC
            if (i == 0 && !isCrossMidnightSegment) {
                dropBar.add(new ShiftResizeHandle(shift, ShiftResizeHandle.ResizeEdge.START));
            }

            // Krawędź END (Prawy uchwyt): Tylko na Ostatnim Slocie
            if (i == duration - 1) {
                // TERAZ MUSIMY PRAWY CHWYTAK SKIEROWAĆ NA ORYGINALNY SHIFT

                // Jeśli to segment, musimy użyć innej logiki w ShiftResizeHandle
                if (isCrossMidnightSegment) {
                    // W tym miejscu musielibyśmy stworzyć nową implementację
                    // ResizeHandle, która wywołuje specjalny event.
                    // ZAKŁADAMY, ŻE NA BE W ShiftResizeHandle LOGIKA D&D JEST JEDNA
                    // I OBSŁUGUJE KLON.

                    // Na razie: dodajemy zwykły uchwyt, a logika modyfikacji
                    // ShiftResource z flagą isCrossMidnightSegment MUSI być obsłużona
                    // wewnątrz ResizeHandle.
                    dropBar.add(new ShiftResizeHandle(shift, ShiftResizeHandle.ResizeEdge.END));
                } else {
                    // Standardowy Shift
                    dropBar.add(new ShiftResizeHandle(shift, ShiftResizeHandle.ResizeEdge.END));
                }
            }

            // C. Aktywny DropListener: Niezbędny do odświeżenia widoku po RESIZE/MOVE
            dropBar.addDropListener(e -> renderSchedule());

            shiftSlots.add(dropBar);
        }
        return shiftSlots;
    }

    private HorizontalLayout createEmptyDropTargetRow() {
        var row = createBaseRow();

        for (int index = 0; index < DailyScheduleGrid.intervalsPerDay; index++) {
            var dropBar = new ShiftResourceDropBar(day, index);

            dropBar.applyStyles(1);
            dropBar.addDropListener(e -> renderSchedule());

            row.add(dropBar);
        }

        return row;
    }

    private Div createEmptySlot(int index) {
        var dropBar = new ShiftResourceDropBar(day, index);

        dropBar.setDroppedResource(null);
        dropBar.addDropListener(e -> renderSchedule());

        dropBar.applyStyles(1);

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

    // W KLASIE DayScheduleVisualization, ZASTĄP CAŁĄ METODĘ PONIŻSZYM KODEM:
    private void checkMidnightCrossingsAndFireEvents() {
        // 1. Sprawdzamy aktualny stan
        boolean anyShiftCrossesMidnight = day.getShifts().stream()
                // Weryfikujemy tylko "prawdziwe" zmiany
                .filter(shift -> !shift.isCrossMidnightSegment())
                .anyMatch(shift -> shift.getEndSlotIndex() > DailyScheduleGrid.intervalsPerDay);

        // 2. Porównujemy ze stanem poprzednim (wasMidnightCrossed) i reagujemy tylko na ZMIANĘ stanu

        if (anyShiftCrossesMidnight && !wasMidnightCrossed) {
            // ZMIANA STANU: Z FALSZ na PRAWDA (zaczął krzyżować północ)
            // Musimy dodać następny dzień do Grid.

            // Znajdujemy jeden ShiftResource do przekazania w Evencie.
            day.getShifts().stream()
                    .filter(shift -> !shift.isCrossMidnightSegment()
                            && shift.getEndSlotIndex() > DailyScheduleGrid.intervalsPerDay)
                    .findFirst()
                    .ifPresent(shift -> {
                        fireEvent(new ShiftCrossesMidnightEvent(this, shift));
                        System.out.println("FireEvent: ShiftCrossesMidnightEvent dla Dnia " + day.getDayNumber());
                    });

        } else if (!anyShiftCrossesMidnight && wasMidnightCrossed) {
            // ZMIANA STANU: Z PRAWDA na FALSZ (przestał krzyżować północ)
            // Musimy dać Grid informację, że następny dzień może być do usunięcia.

            // Przekazujemy dowolny shift, ponieważ Grid patrzy na numer dnia.
            day.getShifts().stream().findFirst().ifPresent(shift -> {
                fireEvent(new ShiftNoLongerCrossesMidnightEvent(this, shift));
                System.out.println("FireEvent: ShiftNoLongerCrossesMidnightEvent dla Dnia " + day.getDayNumber());
            });
        }

        // 3. Aktualizujemy stan na koniec renderowania
        wasMidnightCrossed = anyShiftCrossesMidnight;
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

    public Registration addShiftCrossesMidnightEvent(ComponentEventListener<ShiftCrossesMidnightEvent> listener) {
        return addListener(ShiftCrossesMidnightEvent.class, listener);
    }

    public Registration addShiftNoLongerCrossesMidnightEvent(
            ComponentEventListener<ShiftNoLongerCrossesMidnightEvent> listener) {
        return addListener(ShiftNoLongerCrossesMidnightEvent.class, listener);
    }
}
