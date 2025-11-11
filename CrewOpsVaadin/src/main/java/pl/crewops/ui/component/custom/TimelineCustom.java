package pl.crewops.ui.component.custom;

import com.vaadin.componentfactory.timeline.Timeline;
import com.vaadin.flow.component.dependency.CssImport;
import pl.crewops.ui.component.panel.daily.DailyTimelinePanel;

@CssImport("./styles/component/timeline.css")
public class TimelineCustom extends Timeline {

    public TimelineCustom() {
        setClassName("crewops-timeline");

        setWidthFull();
        setHeight(DailyTimelinePanel.TIMELINE_CONTAINER_HEIGHT_PX - 40 + "px");
        setMoveable(true);
        setZoomable(true);
        setShowCurentTime(true);
    }
}
