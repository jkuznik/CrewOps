package pl.crewops.ui.component.custom.schedule;

import static pl.crewops.ui.component.custom.schedule.ScheduleConfigurationComponent.minutePixelWidth;

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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.Getter;
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
    private LocalDateTime from;
    private LocalDateTime to;

    public ShiftResource(String type, LocalDateTime from, LocalDateTime to) {
        this.type = type;
        this.from = from;
        this.to = to;
    }
}

abstract class TimeBar extends Div {

    public TimeBar(LocalDateTime from, LocalDateTime to) {
        var timeBetweenSeconds = to.toEpochSecond(ZoneOffset.UTC) - from.toEpochSecond(ZoneOffset.UTC);
        var timeBetweenMinutes = timeBetweenSeconds / 60; // Prawidłowe obliczenie minut

        // Szerokość w pikselach = minuty * minutePixelWidth
        var width = timeBetweenMinutes * minutePixelWidth + "px";

        setWidth(width);
        setHeight("16px");
    }
}

class DragTimeBar extends TimeBar {

    private Tooltip tooltip;

    public DragTimeBar(ShiftResource shiftResource) {
        super(shiftResource.getFrom(), shiftResource.getTo());

        // Dodaj proste style, aby pasek był widoczny
        getStyle()
                .set("background-color", "#3e70d6")
                .set("color", "white")
                .set("border-radius", "3px")
                .set("margin-left", "1px")
                .set("text-align", "center");

        DragSource<Div> dragSource = DragSource.create(this);
        dragSource.setDragData(shiftResource);
        dragSource.setEffectAllowed(EffectAllowed.COPY_MOVE);
        dragSource.addDragStartListener(event -> {});

        dragSource.addDragEndListener(event -> {});
    }
}

class DropTimeBar extends TimeBar {
    private Tooltip tooltip;
    // Wewnątrz klasy DropTimeBar
    public DropTimeBar(LocalDateTime from, LocalDateTime to, ScheduleDay day, Grid<ScheduleDay> grid) {
        super(from, to);
        // Styling bazowy
        getStyle()
                .set("background-color", "#f0f0f0")
                .set("border", "1px dashed #aaa")
                .set("box-sizing", "border-box");

        // Nadajemy klasę bazową dla naszego DropTimeBar, aby móc ją modyfikować
        addClassName("schedule-drop-target");

        // *****************************************************************
        // KLUCZOWA ZMIANA: Dodanie słuchaczy zdarzeń DOM do elementu
        // *****************************************************************

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
            // Usuń klasę aktywną po udanym upuszczeniu
            getElement().getClassList().remove("schedule-drop-active");

            event.getDragData().ifPresent(data -> {
                if (data instanceof ShiftResource droppedResource) {

                    // 1. Zapisz oryginalne dane przed utworzeniem nowego elementu
                    LocalDateTime originalFrom = droppedResource.getFrom();

                    // 2. Utwórz nowy ShiftResource
                    LocalDateTime newShiftFrom = from;
                    long durationSeconds = droppedResource.getTo().toEpochSecond(ZoneOffset.UTC)
                            - droppedResource.getFrom().toEpochSecond(ZoneOffset.UTC);
                    LocalDateTime newShiftTo = newShiftFrom.plusSeconds(durationSeconds);

                    // Dodaj nowy ShiftResource do dnia
                    day.addShift(new ShiftResource(droppedResource.getType(), newShiftFrom, newShiftTo));

                    // 3. Sprawdź, czy operacja to MOVE (usuwanie starego elementu)
                    if (event.getDropEffect().equals(DropEffect.MOVE)) {
                        // Usuń oryginalny element z kolekcji day
                        day.getShifts()
                                .removeIf(shift -> shift.getType().equals(droppedResource.getType())
                                        && shift.getFrom().isEqual(originalFrom));
                    }

                    // 4. Odśwież widok
                    grid.getDataProvider().refreshItem(day);
                }
            });
        });
    }
}
