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
import pl.crewops.infrastructure.core.CoreAPI;

@CssImport("./styles/component/schedule-content-component.css")
public class ScheduleConfigurationComponent extends VerticalLayout {

    private final CoreAPI coreAPI;

    private final Tab dailySchedule = new Tab(getTranslation("scheduleConfigurationComponent.dailySchedule"));
    private final Tab weeklySchedule = new Tab(getTranslation("scheduleConfigurationComponent.weeklySchedule"));
    private final Tabs tabs = new Tabs(dailySchedule, weeklySchedule);
    private final Button toggleVisibilityButton = new Button(VaadinIcon.ANGLE_UP.create());

    private final DailyScheduleGenerator dailyScheduleGenerator = new DailyScheduleGenerator();

    private final VerticalLayout contentContainer;
    private boolean isContentVisible = true;

    public ScheduleConfigurationComponent(CoreAPI coreAPI) {
        this.coreAPI = coreAPI;
        this.contentContainer = new VerticalLayout(tabs, dailyScheduleGenerator);

        addClassName("component-content-border");

        configureTabs();

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
}
