package pl.crewops.ui.component.custom.schedule;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;
import elemental.json.JsonObject;
import java.time.LocalDate;
import java.util.Locale;
import lombok.Getter;

@Tag("schedule-full-calendar")
@JsModule("./ts/ScheduleFullCalendar.ts")
@CssImport("./styles/component/dailyView/schedule-full-calendar.css")
public class Calendar extends Component implements HasSize, HasStyle {

    public Calendar() {
        setSizeFull();
        addClassName("calendar-full-view");

        updateLocale();

        getElement()
                .addEventListener("date-selected", event -> {
                    String dateStr = event.getEventData().getString("event.detail.date");
                    if (dateStr != null) {
                        LocalDate selectedDate = LocalDate.parse(dateStr);
                        fireEvent(new SelectDateEvent(this, true, selectedDate));
                    }
                })
                .addEventData("event.detail.date");

        getElement()
                .addEventListener("template-dropped", event -> {
                    JsonObject detail = event.getEventData().getObject("event.detail");
                    if (detail != null) {
                        fireEvent(new TemplateDroppedEvent(this, true, detail));
                    }
                })
                .addEventData("event.detail");
    }

    // Klasa zdarzenia dla dropu
    @DomEvent("template-dropped")
    public static class TemplateDroppedEvent extends ComponentEvent<Calendar> {
        private final JsonObject detail;

        public TemplateDroppedEvent(Calendar source, boolean fromClient, @EventData("event.detail") JsonObject detail) {
            super(source, fromClient);
            this.detail = detail;
        }

        public JsonObject getDetail() {
            return detail;
        }
    }

    public Registration addTemplateDroppedListener(ComponentEventListener<TemplateDroppedEvent> listener) {
        return addListener(TemplateDroppedEvent.class, listener);
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

    public void forceRender() {
        getElement()
                .executeJs(
                        """
        setTimeout(() => {
            if (this.updateSize) {
                this.updateSize();
                if (this.calendar) {
                    this.calendar.render();
                }
            }
        }, 100);
    """);
    }

    public void addEvent(String id, String title, String start, String end, String color) {
        getElement().executeJs("this.addEvent($0, $1, $2, $3, $4)", id, title, start, end, color);
    }

    public static class SelectDateEvent extends ComponentEvent<Calendar> {
        @Getter
        private final LocalDate selectedDate;

        public SelectDateEvent(Calendar source, boolean fromClient, LocalDate selectedDate) {
            super(source, fromClient);
            this.selectedDate = selectedDate;
        }
    }

    @Override
    public void setVisible(boolean visible) {
        getElement().setProperty("hiddenMode", !visible);
    }

    public Registration addSelectDateListener(ComponentEventListener<SelectDateEvent> listener) {
        return addListener(SelectDateEvent.class, listener);
    }
}
