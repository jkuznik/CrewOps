package pl.crewops.component.custom;

import com.vaadin.componentfactory.timeline.Timeline;
import com.vaadin.componentfactory.timeline.model.Item;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span; // Używamy Span zamiast RadioButtonGroup
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import pl.crewops.enums.DailyAttendanceStatus;

public class DailyTimeline extends Div {

    private static final String TIMELINE_WIDTH_PX = "1200px";
    private static final String TIMELINE_HEIGHT_PX = "200px";

    private final Timeline timeline = new Timeline();
    private final Span statusDisplay = new Span();
    private final Icon helpIcon = VaadinIcon.INFO_CIRCLE.create();

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

        var attendanceLayout = configuredAttendanceContainer();

        var horizontalLayout = new HorizontalLayout();

        horizontalLayout.add(configuredTimeline(), attendanceLayout);

        add(horizontalLayout);
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

    private String getTranslatedLabel(DailyAttendanceStatus item) {
        switch (item) {
            case PRESENT -> {
                return getTranslation("Potwierdzona obecność");
            }
            case VACATION -> {
                return getTranslation("Urlop");
            }
            case SICK_LEAVE -> {
                return getTranslation("Zwolnienie z pracy");
            }
            case OTHER -> {
                return getTranslation("Inne");
            }
            case ABSENT -> {
                return getTranslation("Nieobecność w pracy");
            }
            default -> {
                return "";
            }
        }
    }

    private String getHelpText() {
        return "1. **Obecność w pracy :** Jesteś / byłeś w pracy danego dnia.\n"
                + "2. **Urlop :** Zaplanowany urlop wypoczynkowy / okolicznościowy / bezpłatny  lub dzień wolny.\n"
                + "3. **Zwolnienie z pracy:** Zwolnienie lekarskie, opieka nad dzieckiem/inną osobą.\n"
                + "4. **Inne :** Akceptowana podróż służbowa, szkolenie, dyżur, medycyna pracy, itp.\n"
                + "5. **Nieobecność w pracy :** Nieusprawiedliwiona lub nieautoryzowana nieobecność.";
    }
}
