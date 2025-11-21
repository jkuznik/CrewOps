package pl.crewops.ui.component.custom.schedule;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropEffect;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.dnd.EffectAllowed;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import lombok.Getter;
import pl.crewops.enums.TimeSlot;
import pl.crewops.infrastructure.core.CoreAPI;

@CssImport("./styles/component/schedule-content-component.css")
public class ScheduleConfigurationComponent extends VerticalLayout {

    static final int minutePixelWidth = 2;

    private final CoreAPI coreAPI;

    private final Button toggleVisibilityButton = new Button(VaadinIcon.ANGLE_UP.create());

    private final DailyScheduleGenerator dailyScheduleGenerator = new DailyScheduleGenerator();

    private final VerticalLayout contentContainer;
    private boolean isContentVisible = true;

    public ScheduleConfigurationComponent(CoreAPI coreAPI) {
        this.coreAPI = coreAPI;
        // todo i18n
        final Tab dailySchedule = new Tab("Daily schedule");
        final Tab weeklySchedule = new Tab("Weekly schedule");
        var tabs = new Tabs(dailySchedule, weeklySchedule);
        this.contentContainer = new VerticalLayout(tabs, dailyScheduleGenerator);

        addClassName("component-content-border");

        setSizeFull();
        add(createToolbar(), contentContainer);
    }

    private HorizontalLayout createToolbar() {
        HorizontalLayout bar = new HorizontalLayout();
        bar.setWidthFull();
        bar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        bar.setAlignItems(FlexComponent.Alignment.CENTER);

        bar.getStyle().set("background-color", "transparent");

        Span title = new Span();
        title.setText(getTranslation("scheduleConfigurationComponent.title"));
        title.getStyle().set("font-weight", "bold");

        title.setWidth("50%");
        toggleVisibilityButton.setWidth("50%");
        toggleVisibilityButton.addClickListener(e -> toggleContentVisibility());

        bar.add(title, toggleVisibilityButton);
        bar.setFlexGrow(1, toggleVisibilityButton);

        return bar;
    }

    private void toggleContentVisibility() {
        isContentVisible = !isContentVisible;
        contentContainer.setVisible(isContentVisible);

        toggleVisibilityButton.setIcon(
                isContentVisible ? VaadinIcon.ANGLE_UP.create() : VaadinIcon.ANGLE_DOWN.create());
    }
}

@Getter
final class ShiftResource {
    private final String type;
    private final TimeSlot startSlot;
    private final int durationInSlots;

    public ShiftResource(String type, TimeSlot startSlot, int durationInSlots) {
        this.type = type;
        this.startSlot = startSlot;
        this.durationInSlots = durationInSlots;
    }

    // Metoda kluczowa: Zwraca indeks slotu, w którym zmiana się kończy (może być > 96)
    public int getEndSlotIndex() {
        // Używamy zdefiniowanej metody getIndex()
        return startSlot.getIndex() + durationInSlots;
    }

    // Inne metody powinny być dostosowane do nowej logiki getIndex()
    // np. getEndSlotForDay
    public TimeSlot getEndSlotForDay() {
        int endSlotIndex = getEndSlotIndex();
        // 96 to liczba slotów (H00_00 do H23_45)
        int slotsPerDay = 96;

        // Indeks końca w Dniu 1 (np. 120 slots % 96 = 24)
        int indexInDay = endSlotIndex % slotsPerDay;

        // Zwracamy slot, używając skorygowanej metody fromIndex
        return TimeSlot.fromIndex(indexInDay);
    }
}

abstract class TimeBar extends Div {
    final int PIXELS_PER_SLOT = 15 * 2;

    public TimeBar() {
        getStyle()
                .set("box-sizing", "border-box") // Krytyczne dla marginesów/paddingów
                .set("margin", "0") // Zapobieganie domyślnym marginesom Vaadin
                .set("padding", "0");
    }
}

class DragTimeBar extends TimeBar {

    private Tooltip tooltip;

    public DragTimeBar(ShiftResource resource) {
        super();

        int durationInSlots = resource.getDurationInSlots();
        var width = durationInSlots * PIXELS_PER_SLOT + "px";
        setWidth(width);

        getStyle()
                .set("background-color", "#3e70d6")
                .set("color", "white")
                .set("border-radius", "3px")
                .set("text-align", "center");

        DragSource<Div> dragSource = DragSource.create(this);
        dragSource.setDragData(resource);
        dragSource.setEffectAllowed(EffectAllowed.COPY_MOVE);
        dragSource.addDragStartListener(event -> {});

        dragSource.addDragEndListener(event -> {});
    }
}

class DropTimeBar extends TimeBar {

    private final ScheduleDay day;
    private final Grid<ScheduleDay> grid;
    private final TimeSlot targetSlot; // Zmienione pole

    // NOWY KONSTRUKTOR
    public DropTimeBar(TimeSlot slot, ScheduleDay day, Grid<ScheduleDay> grid) {
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
        DropTarget<DropTimeBar> dropTarget = DropTarget.create(this);
        dropTarget.setDropEffect(DropEffect.COPY);

        dropTarget.addDropListener(event -> {
            getElement().getClassList().remove("schedule-drop-active");

            event.getDragData().ifPresent(data -> {
                if (data instanceof ShiftResource droppedResource) {

                    // 1. Zidentyfikuj slot startowy z tego DropTarget
                    TimeSlot newShiftStartSlot = this.targetSlot;

                    // 2. Pobierz długość w slotach z przeciąganego obiektu
                    int newDurationInSlots = droppedResource.getDurationInSlots();

                    // 3. Utwórz nowy ShiftResource
                    ShiftResource newShift =
                            new ShiftResource(droppedResource.getType(), newShiftStartSlot, newDurationInSlots);

                    // 4. Dodaj nowy ShiftResource do dnia
                    day.addShift(newShift);

                    // 5. Odśwież widok
                    // Używamy refreshAll(), bo logika wizualizacji jest w DayScheduleVisualization
                    grid.getDataProvider().refreshAll();
                }
            });
        });
    }
}
