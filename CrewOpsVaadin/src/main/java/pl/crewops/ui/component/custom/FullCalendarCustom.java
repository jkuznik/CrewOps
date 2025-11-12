package pl.crewops.ui.component.custom;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.shared.Registration;
import java.time.LocalDate;
import org.vaadin.stefan.fullcalendar.FullCalendar;

public class FullCalendarCustom extends FullCalendar {

    public FullCalendarCustom() {
        super();
        setHeight("800px");
        setWidth("100%");

        // UWAGA: Logika JS została usunięta z konstruktora
    }

    /**
     * Wstrzykuje kod JavaScript nasłuchujący na natywne zdarzenie 'dateClick'
     * i mapujący je na metodę Javy (@ClientCallable).
     * * Ta metoda jest wywoływana z okna dialogowego po jego otwarciu,
     * aby upewnić się, że komponent FullCalendar jest gotowy do interakcji.
     */
    public void injectDateClickListener() {
        // Opcja 'selectable' musi być włączona, aby event 'dateClick' zadziałał
        setOption("selectable", true);

        // Używamy executeJs do wstrzyknięcia listenera
        getElement()
                .executeJs(
                        """
            this.calendar.on('dateClick', (info) => {
                // Wywołanie metody Javy 'onDateClick' z datą w formacie ISO (np. 'YYYY-MM-DD')
                this.$server.onDateClick(info.dateStr);

                // Wizualna zmiana, którą widziałeś, jest efektem ubocznym 'dateClick',
                // ale ten kod gwarantuje wywołanie serwera.
            });
        """);
    }

    // -------------------------------------------------------------------------
    // Metoda Javy Odbierająca Wywołanie z JS
    // -------------------------------------------------------------------------

    /**
     * Odbiera klikniętą datę jako ciąg znaków z JavaScriptu.
     */
    @ClientCallable
    private void onDateClick(String dateStr) {
        LocalDate selectedDate = LocalDate.parse(dateStr);
        fireEvent(new DateSelectedEvent(this, selectedDate));
    }

    // -------------------------------------------------------------------------
    // Definicja Zdarzenia Flow
    // -------------------------------------------------------------------------

    /**
     * Rejestruje nasłuchiwacz na zdarzenie wyboru daty.
     */
    public Registration addSelectedDateListener(ComponentEventListener<DateSelectedEvent> listener) {
        return addListener(DateSelectedEvent.class, listener);
    }

    /**
     * Zdarzenie emitowane po wybraniu daty w kalendarzu.
     */
    public static class DateSelectedEvent extends ComponentEvent<FullCalendarCustom> {
        private final LocalDate selectedDate;

        public DateSelectedEvent(FullCalendarCustom source, LocalDate selectedDate) {
            super(source, false);
            this.selectedDate = selectedDate;
        }

        public LocalDate getSelectedDate() {
            return selectedDate;
        }
    }
}
