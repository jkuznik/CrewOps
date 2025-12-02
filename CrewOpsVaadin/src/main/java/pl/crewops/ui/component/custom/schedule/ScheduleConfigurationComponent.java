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

    public void addShiftResourceDragBar(ShiftDTO shiftDTO) {
        dailyScheduleGenerator.addShiftResourceDragBar(shiftDTO);
    }

    public void updateShiftResourceDragBar(ShiftDTO shiftDTO) {
        dailyScheduleGenerator.updateShiftResourceDragBar(shiftDTO);
    }

    public void removeShiftResourceDragBar(UUID shiftId) {
        dailyScheduleGenerator.removeShiftResourceDragBar(shiftId);
    }
}
