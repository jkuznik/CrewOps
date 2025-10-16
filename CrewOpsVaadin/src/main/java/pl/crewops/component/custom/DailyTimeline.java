package pl.crewops.component.custom;

import com.vaadin.componentfactory.timeline.Timeline;
import com.vaadin.componentfactory.timeline.model.Item;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import pl.crewops.enums.DailyAttendanceStatus;

public class DailyTimeline extends HorizontalLayout {

    private static final String TIMELINE_WIDTH_PX = "1200px";
    private static final int TIMELINE_HEIGHT_PX = 200;

    private final Timeline timeline = new Timeline();
    private final Span statusDisplay = new Span();
    private final Icon helpIcon = VaadinIcon.INFO_CIRCLE.create();

    public DailyTimeline() {
        configureStyles();

        add(configuredTimeline(), configuredAttendanceContainer());
    }

    private void configureStyles() {

        setMinWidth(TIMELINE_WIDTH_PX);
        setMaxWidth(TIMELINE_WIDTH_PX);
        setMaxHeight(TIMELINE_HEIGHT_PX - 1 + "px");
        getStyle().remove("overflow-x");
        getStyle().remove("overflow-y");
        getStyle().set("border", "1px solid #ccc");
        getStyle().set("border-radius", "4px");
    }

    private HorizontalLayout configuredAttendanceContainer() {
        var attendanceLayout = new HorizontalLayout();

        attendanceLayout.setPadding(true);
        attendanceLayout.setJustifyContentMode(HorizontalLayout.JustifyContentMode.END);

        statusDisplay.getStyle().set("font-weight", "bold");
        statusDisplay.getStyle().set("padding", "var(--lumo-space-s)");
        statusDisplay.getStyle().set("line-height", "1.5");

        helpIcon.setColor("var(--lumo-contrast-50pct)");
        helpIcon.getStyle().set("cursor", "pointer");

        Tooltip.forComponent(helpIcon).withText(getHelpText()).withPosition(Tooltip.TooltipPosition.BOTTOM_END);

        attendanceLayout.add(statusDisplay, helpIcon);
        return attendanceLayout;
    }

    public void setAttendanceStatus(DailyAttendanceStatus status) {

        statusDisplay.getStyle().remove("color");

        String translatedLabel = getTranslatedLabel(status);
        statusDisplay.setText(translatedLabel);
        statusDisplay.getStyle().set("font-weight", "bold");

        switch (status) {
            case PRESENT -> {
                // LUMO_SUCCESS green like
                statusDisplay.getStyle().set("color", "#10D965");
            }
            case VACATION -> {
                // Blue
                statusDisplay.getStyle().set("color", "#007bff");
            }
            case SICK_LEAVE -> {
                statusDisplay.getStyle().set("color", "orange");
            }
            case ABSENT -> {
                statusDisplay.getStyle().set("color", "red");
            }
            case OTHER -> {
                statusDisplay.getStyle().set("color", "gray");
            }
            default -> {
                statusDisplay.getStyle().set("color", "black");
            }
        }
    }

    private Timeline configuredTimeline() {
        LocalDate today = LocalDate.now();
        setTimelineRange(LocalDateTime.of(today, LocalTime.MIN), LocalDateTime.of(today, LocalTime.MAX));

        timeline.setWidthFull();
        timeline.setHeight(TIMELINE_HEIGHT_PX + "px");
        timeline.setMoveable(true);
        timeline.setZoomable(true);
        timeline.setShowCurentTime(true);

        return timeline;
    }

    public void setTimelineRange(LocalDateTime from, LocalDateTime to) {
        timeline.setTimelineRange(from, to);
    }

    public void updateItems(List<Item> items) {
        timeline.setItems(items);
    }

    private String getTranslatedLabel(DailyAttendanceStatus item) {
        switch (item) {
            case PRESENT -> {
                return getTranslation("dailyTimeline.present");
            }
            case VACATION -> {
                return getTranslation("dailyTimeline.vacation");
            }
            case SICK_LEAVE -> {
                return getTranslation("dailyTimeline.sickLeave");
            }
            case OTHER -> {
                return getTranslation("dailyTimeline.other");
            }
            case ABSENT -> {
                return getTranslation("dailyTimeline.absent");
            }
            default -> {
                return "";
            }
        }
    }

    private String getHelpText() {
        return getTranslation("dailyTimeline.helpText.title") + "\n"
                + getTranslation("dailyTimeline.helpText.1") + "\n"
                + getTranslation("dailyTimeline.helpText.2") + "\n"
                + getTranslation("dailyTimeline.helpText.3") + "\n"
                + getTranslation("dailyTimeline.helpText.4") + "\n"
                + getTranslation("dailyTimeline.helpText.5") + "\n"
                + getTranslation("dailyTimeline.helpText.6");
    }
}
