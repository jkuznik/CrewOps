package pl.crewops.component.custom;

import com.vaadin.componentfactory.timeline.Timeline;
import com.vaadin.componentfactory.timeline.model.Item;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import pl.crewops.component.form.daily.AttendanceStatusForm;

public class DailyTimeline extends Div {

    private static final String TIMELINE_WIDTH_PX = "1200px";
    private static final String TIMELINE_HEIGHT_PX = "200px";

    private final Timeline timeline = new Timeline();

    public DailyTimeline() {
        removeAll();

        setMinWidth(TIMELINE_WIDTH_PX);
        setMaxWidth(TIMELINE_WIDTH_PX);
        setMinHeight(TIMELINE_HEIGHT_PX);
        setMaxHeight(TIMELINE_HEIGHT_PX);
        getStyle().remove("overflow-x");
        getStyle().remove("overflow-y");
        getStyle().set("border", "1px solid #ccc");
        getStyle().set("border-radius", "4px");

        var horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(configureTimeline(), new AttendanceStatusForm());

        add(horizontalLayout);
    }

    private Timeline configureTimeline() {

        LocalDate today = LocalDate.now();
        timeline.setTimelineRange(LocalDateTime.of(today, LocalTime.MIN), LocalDateTime.of(today, LocalTime.MAX));

        timeline.setWidthFull();
        timeline.setMinHeight(TIMELINE_HEIGHT_PX);
        timeline.setMoveable(true);
        timeline.setZoomable(true);
        timeline.setShowCurentTime(true);

        return timeline;
    }

    public void updateItems(List<Item> items) {
        timeline.setItems(items);
    }
}
