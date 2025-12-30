package pl.crewops.ui.component.custom.schedule;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.server.VaadinSession;
import java.util.Locale;

@Tag("schedule-full-calendar")
@JsModule("./ts/ScheduleFullCalendar.ts")
@CssImport("./styles/component/dailyView/schedule-full-calendar.css")
class Calendar extends Component implements HasSize, HasStyle {

    public Calendar() {
        setSizeFull();
        addClassName("calendar-full-view");

        updateLocale();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        updateLocale();
    }

    private void updateLocale() {
        Locale currentLocale =
                VaadinSession.getCurrent() != null ? VaadinSession.getCurrent().getLocale() : Locale.getDefault();
        getElement().setProperty("locale", currentLocale.getLanguage());
    }

    public void addEvent(String id, String title, String start, String end, String color) {
        getElement().executeJs("this.addEvent($0, $1, $2, $3, $4)", id, title, start, end, color);
    }
}
