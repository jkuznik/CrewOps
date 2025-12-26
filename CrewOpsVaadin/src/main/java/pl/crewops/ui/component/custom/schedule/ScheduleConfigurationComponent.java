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
import pl.crewops.model.dto.shift.ShiftDTO;

@CssImport("./styles/component/schedule-content-component.css")
public class ScheduleConfigurationComponent extends VerticalLayout {

    private final VerticalLayout contentContainer;

    private final Button toggleVisibilityButton = new Button(VaadinIcon.ANGLE_UP.create());

    private final Tab dailySchedule = new Tab(getTranslation("scheduleConfigurationComponent.dailySchedule"));
    private final Tab weeklySchedule = new Tab(getTranslation("scheduleConfigurationComponent.weeklySchedule"));
    private final Tabs tabs = new Tabs(dailySchedule, weeklySchedule);

    private final VerticalLayout shiftsPaletteContainer = new VerticalLayout();
    private final HorizontalLayout shiftsItemsLayout = new HorizontalLayout();

    private final DailyScheduleGenerator dailyScheduleGenerator;

    private boolean isContentVisible = true;

    public ScheduleConfigurationComponent() {
        this.dailyScheduleGenerator = new DailyScheduleGenerator();
        this.contentContainer = new VerticalLayout(tabs, shiftsPaletteContainer, dailyScheduleGenerator);

        addClassName("component-content-border");

        configureTabs();
        configureShiftsPalette();

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

    private void configureShiftsPalette() {
        shiftsPaletteContainer.setPadding(false);
        shiftsPaletteContainer.setSpacing(true);
        shiftsPaletteContainer.getStyle().set("margin-top", "10px");

        // 1. Nagłówek sekcji
        Span paletteHeader = new Span(getTranslation("scheduleConfigurationComponent.schedulePaletteTitle"));
        paletteHeader
                .getStyle()
                .set("font-size", "var(--lumo-font-size-s)")
                .set("font-weight", "bold")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "1px");

        // 2. Layout na kafelki (stara shiftsPalette)
        shiftsItemsLayout.setWidthFull();
        shiftsItemsLayout.setMinHeight("65px");
        shiftsItemsLayout.setPadding(false);
        shiftsItemsLayout
                .getStyle()
                .set("gap", "12px")
                .set("overflow-x", "auto")
                .set("padding-bottom", "5px"); // Miejsce na scrollbar

        shiftsPaletteContainer.add(paletteHeader, shiftsItemsLayout);
    }

    public void addShiftResourceDragBar(ShiftDTO dto) {
        removeShiftFromPalette(dto.id());

        ShiftPaletteItem item = new ShiftPaletteItem(dto);
        shiftsItemsLayout.add(item); // Zmieniono na shiftsItemsLayout

        dailyScheduleGenerator.addShiftToPalette(dto);
    }

    public void removeShiftFromPalette(UUID shiftId) {
        shiftsItemsLayout
                .getChildren() // Zmieniono na shiftsItemsLayout
                .filter(ShiftPaletteItem.class::isInstance)
                .map(ShiftPaletteItem.class::cast)
                .filter(item -> item.getShiftDTO().id().equals(shiftId))
                .findFirst()
                .ifPresent(shiftsItemsLayout::remove);

        dailyScheduleGenerator.removeShiftFromPalette(shiftId);
    }

    public void updateShiftResourceDragBar(ShiftDTO dto) {
        shiftsItemsLayout
                .getChildren()
                .filter(ShiftPaletteItem.class::isInstance)
                .map(ShiftPaletteItem.class::cast)
                .filter(item -> item.getShiftDTO().id().equals(dto.id()))
                .findFirst()
                .ifPresent(shiftsItemsLayout::remove);

        ShiftPaletteItem newItem = new ShiftPaletteItem(dto);
        shiftsItemsLayout.add(newItem);

        dailyScheduleGenerator.updateShiftInPalette(dto);
    }
}
