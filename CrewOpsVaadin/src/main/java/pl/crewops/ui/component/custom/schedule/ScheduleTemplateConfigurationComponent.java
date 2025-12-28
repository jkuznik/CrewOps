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
class ScheduleTemplateConfigurationComponent extends VerticalLayout {

    private final VerticalLayout contentContainer;

    private final Button toggleVisibilityButton = new Button(VaadinIcon.ANGLE_DOWN.create());

    private final Tab dailySchedule = new Tab(getTranslation("scheduleConfigurationComponent.dailySchedule"));
    private final Tab weeklySchedule = new Tab(getTranslation("scheduleConfigurationComponent.weeklySchedule"));
    private final Tabs tabs = new Tabs(dailySchedule, weeklySchedule);

    private final VerticalLayout shiftsPaletteContainer = new VerticalLayout();
    private final HorizontalLayout shiftsItemsLayout = new HorizontalLayout();

    private final DailyGenerator dailyGenerator;

    private boolean isContentVisible = false;

    public ScheduleTemplateConfigurationComponent() {
        this.dailyGenerator = new DailyGenerator();
        this.contentContainer = new VerticalLayout(tabs, shiftsPaletteContainer, dailyGenerator);

        addClassName("component-content-border");

        configureTabs();
        configureShiftsPalette();
        contentContainer.setVisible(isContentVisible);

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
                dailyGenerator.setVisible(true);
            } else {
                dailyGenerator.setVisible(false);
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

        dailyGenerator.addShiftToPalette(dto);
    }

    public void removeShiftFromPalette(UUID shiftId) {
        shiftsItemsLayout
                .getChildren() // Zmieniono na shiftsItemsLayout
                .filter(ShiftPaletteItem.class::isInstance)
                .map(ShiftPaletteItem.class::cast)
                .filter(item -> item.getShiftDTO().id().equals(shiftId))
                .findFirst()
                .ifPresent(shiftsItemsLayout::remove);

        dailyGenerator.removeShiftFromPalette(shiftId);
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

        dailyGenerator.updateShiftInPalette(dto);
    }
}

class DailyGenerator extends VerticalLayout {

    private final NativeScheduleGrid nativeScheduleGrid = new NativeScheduleGrid();

    private final ScheduleTemplateForm form = new ScheduleTemplateForm(nativeScheduleGrid);

    public DailyGenerator() {

        setSizeFull();
        setPadding(false);
        setSpacing(true);

        nativeScheduleGrid.updateClientSideData();

        add(nativeScheduleGrid, form);
    }

    public void addShiftToPalette(ShiftDTO dto) {
        nativeScheduleGrid.registerPaletteTemplate(dto);
    }

    public void removeShiftFromPalette(UUID shiftId) {
        nativeScheduleGrid.removeShiftsByTemplate(shiftId);
    }

    public void updateShiftInPalette(ShiftDTO dto) {
        nativeScheduleGrid.updateShiftsFromTemplate(dto);
    }
}
