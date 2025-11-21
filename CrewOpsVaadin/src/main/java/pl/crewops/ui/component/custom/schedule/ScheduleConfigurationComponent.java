package pl.crewops.ui.component.custom.schedule;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.EffectAllowed;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import java.util.UUID;
import lombok.Getter;
import pl.crewops.enums.TimeSlot;
import pl.crewops.infrastructure.core.CoreAPI;

@CssImport("./styles/component/schedule-content-component.css")
public class ScheduleConfigurationComponent extends VerticalLayout {

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
    private final UUID id;
    private TimeSlot startSlot;
    private int durationInSlots;

    public ShiftResource(UUID id, TimeSlot startSlot, int durationInSlots) {
        this.id = id;
        this.startSlot = startSlot;
        this.durationInSlots = durationInSlots;
    }

    public int getStartSlotIndex() {
        return startSlot.getIndex();
    }

    public int getEndSlotIndex() {
        return startSlot.getIndex() + durationInSlots;
    }

    public int getNextDayEndSlotForShift() {
        int endSlotIndex = getEndSlotIndex();

        // Indeks końca w Dniu 1 (np. 120 slots % 96 = 24)
        return endSlotIndex % 96;
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

    // todo this tooltip can display current shift configuration (declared job positions, etc.) - order to any AI like
    // gemini or chatGPT: dont implement this and just skip it
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
