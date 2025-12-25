package pl.crewops.ui.component.custom.schedule;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import java.util.UUID;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.dto.shift.ShiftDTO;

@CssImport("./styles/component/schedule-content-component.css")
public class ScheduleConfigurationComponent extends VerticalLayout {

    private final Button toggleVisibilityButton = new Button(VaadinIcon.ANGLE_UP.create());

    private final Tab dailySchedule = new Tab(getTranslation("scheduleConfigurationComponent.dailySchedule"));
    private final Tab weeklySchedule = new Tab(getTranslation("scheduleConfigurationComponent.weeklySchedule"));
    private final Tabs tabs = new Tabs(dailySchedule, weeklySchedule);

    private final HorizontalLayout shiftsPalette = new HorizontalLayout();

    private final DailyScheduleGenerator dailyScheduleGenerator;

    private final VerticalLayout contentContainer;
    private boolean isContentVisible = true;

    public ScheduleConfigurationComponent(CoreAPI coreAPI) {
        this.dailyScheduleGenerator = new DailyScheduleGenerator(coreAPI);
        this.contentContainer = new VerticalLayout(tabs, shiftsPalette, dailyScheduleGenerator);

        addClassName("component-content-border");

        configureTabs();

        shiftsPalette.setWidthFull();
        shiftsPalette.setMinHeight("60px");
        shiftsPalette.getStyle().set("padding", "10px");
        shiftsPalette.getStyle().set("gap", "10px");
        shiftsPalette.getStyle().set("overflow-x", "auto");

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

    private void configureTabs() {
        tabs.addSelectedChangeListener(event -> {
            Tab selectedTab = event.getSelectedTab();
            if (selectedTab.equals(dailySchedule)) {
                dailyScheduleGenerator.setVisible(true);
            } else {
                dailyScheduleGenerator.setVisible(false);
            }
        });
    }

    public void addShiftResourceDragBar(ShiftDTO dto) {
        removeShiftFromPalette(dto.id());

        ShiftPaletteItem item = new ShiftPaletteItem(dto);
        shiftsPalette.add(item);

        dailyScheduleGenerator.addShiftToPalette(dto);
    }

    public void removeShiftFromPalette(UUID shiftId) {
        shiftsPalette
                .getChildren()
                .filter(ShiftPaletteItem.class::isInstance)
                .map(ShiftPaletteItem.class::cast)
                .filter(item -> item.getShiftDTO().id().equals(shiftId))
                .findFirst()
                .ifPresent(shiftsPalette::remove);

        dailyScheduleGenerator.removeShiftFromPalette(shiftId);
    }

    public void updateShiftResourceDragBar(ShiftDTO dto) {
        shiftsPalette
                .getChildren()
                .filter(ShiftPaletteItem.class::isInstance)
                .map(ShiftPaletteItem.class::cast)
                .filter(item -> item.getShiftDTO().id().equals(dto.id()))
                .findFirst()
                .ifPresent(shiftsPalette::remove);

        ShiftPaletteItem newItem = new ShiftPaletteItem(dto);
        shiftsPalette.add(newItem);

        dailyScheduleGenerator.updateShiftInPalette(dto);
    }
}
