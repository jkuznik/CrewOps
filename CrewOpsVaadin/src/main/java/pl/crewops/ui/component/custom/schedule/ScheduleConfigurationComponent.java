package pl.crewops.ui.component.custom.schedule;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import pl.crewops.infrastructure.core.CoreAPI;

public class ScheduleConfigurationComponent extends VerticalLayout {

    private final CoreAPI coreAPI;

    private final VerticalLayout contentContainer = new VerticalLayout();
    private final Button toggleVisibilityButton = new Button(VaadinIcon.ANGLE_DOWN.create());

    private boolean isContentVisible = false;
    private boolean isFirstOpen = true;

    public ScheduleConfigurationComponent(CoreAPI coreAPI) {
        this.coreAPI = coreAPI;

        add(createToolbar(toggleVisibilityButton), contentContainer);
    }

    private HorizontalLayout createToolbar(Button button) {
        HorizontalLayout bar = new HorizontalLayout();
        bar.setWidthFull();
        bar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        bar.setAlignItems(FlexComponent.Alignment.CENTER);

        bar.getStyle().set("background-color", "transparent");

        Span title = new Span();
        title.setText("Konfiguracja szablonu grafiku");
        title.getStyle().set("font-weight", "bold");

        button.addClickListener(e -> toggleContentVisibility());

        bar.add(title, button);
        bar.setFlexGrow(1, button);

        return bar;
    }

    private void toggleContentVisibility() {
        isContentVisible = !isContentVisible;
        if (isFirstOpen) {
            //            updateShiftsLayout();
            isFirstOpen = false;
        }
        contentContainer.setVisible(isContentVisible);

        toggleVisibilityButton.setIcon(
                isContentVisible ? VaadinIcon.ANGLE_UP.create() : VaadinIcon.ANGLE_DOWN.create());
    }

    public void ensureFirstShiftPanelExists() {
        //        if (shiftContainers.isEmpty()) {
        //            addShiftPanel();
        //        }
        if (!isContentVisible) {
            toggleContentVisibility();
        }
    }
}
