package pl.crewops.ui.component.custom;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import io.overcoded.vaadin.panel.Panel;
import io.overcoded.vaadin.panel.PanelConfig;
import io.overcoded.vaadin.panel.PanelType;

@CssImport("./styles/component/panel.css")
public class PanelCustom extends Panel {

    private static final PanelConfig timelineConfig = PanelType.PRIMARY.getConfig().toBuilder()
            .collapsable(false)
            .primaryColor("#00adb5")
            .closeable(false)
            .icon(VaadinIcon.PIE_BAR_CHART)
            .titleFontSize("var(--lumo-font-size-xl)")
            .build();

    public PanelCustom() {
        super(timelineConfig, "", new VerticalLayout());

        setClassName("custom-timeline-panel");
    }
}
