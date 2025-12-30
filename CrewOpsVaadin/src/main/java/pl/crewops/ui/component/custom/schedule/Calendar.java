package pl.crewops.ui.component.custom.schedule;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;

@Tag("schedule-full-calendar")
@JsModule("./ts/ScheduleFullCalendar.ts")
// Usuwamy @JavaScript i pozwalamy, aby TS sam zaimportował bibliotekę
@CssImport("./styles/component/dailyView/schedule-full-calendar.css")
class Calendar extends Component implements HasSize, HasStyle {

    public Calendar() {
        getElement().setProperty("initialOptions", createDefaultOptions());
        setSizeFull();
        addClassName("calendar-full-view");

        getElement().setProperty("initialOptions", createDefaultOptions());
    }

    private String createDefaultOptions() {
        return """
        {
            "initialView": "dayGridMonth",
            "editable": true,
            "droppable": true,
            "firstDay": 1,
            "headerToolbar": {
                "left": "prev,next today",
                "center": "title",
                "right": "dayGridMonth,timeGridWeek"
            }
        }
        """;
    }

    public void addEvent(String id, String title, String start, String end, String color) {
        getElement().executeJs("this.addEvent($0, $1, $2, $3, $4)", id, title, start, end, color);
    }
}
