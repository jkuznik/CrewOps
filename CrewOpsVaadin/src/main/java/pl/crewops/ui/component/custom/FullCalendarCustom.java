package pl.crewops.ui.component.custom;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.shared.Registration;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.dataprovider.InMemoryEntryProvider;
import org.vaadin.stefan.fullcalendar.model.Header;
import org.vaadin.stefan.fullcalendar.model.HeaderFooterItem;
import org.vaadin.stefan.fullcalendar.model.HeaderFooterPart;
import org.vaadin.stefan.fullcalendar.model.HeaderFooterPartPosition;
import pl.crewops.model.dto.dailyEntry.DailyEntryDTO;

public class FullCalendarCustom extends FullCalendar {

    public FullCalendarCustom() {
        super();
        setWeekNumbersVisible(false);
        setSizeFull();
        setHeight("100%");

        setHeaderToolbar(createCustomHeaderToolbar());
    }

    public void setDailyEntries(Collection<DailyEntryDTO> dailyEntries) {
        List<Entry> entries = new ArrayList<>();

        if (dailyEntries != null) {
            for (DailyEntryDTO dto : dailyEntries) {
                // Entry dla attendance
                if (dto.attendance() != null) {
                    Entry attendanceEntry = new Entry(UUID.randomUUID().toString());
                    attendanceEntry.setTitle("Attendance: " + dto.attendance().name());
                    attendanceEntry.setAllDay(true);
                    attendanceEntry.setStart(
                            dto.entryDate().atStartOfDay(ZoneOffset.UTC).toInstant());
                    attendanceEntry.setEnd(dto.entryDate()
                            .plusDays(1)
                            .atStartOfDay(ZoneOffset.UTC)
                            .toInstant());
                    entries.add(attendanceEntry);
                }

                // Entry dla status
                if (dto.status() != null) {
                    Entry statusEntry = new Entry(UUID.randomUUID().toString());
                    statusEntry.setTitle("Status: " + dto.status().name());
                    statusEntry.setAllDay(true);
                    statusEntry.setStart(
                            dto.entryDate().atStartOfDay(ZoneOffset.UTC).toInstant());
                    statusEntry.setEnd(dto.entryDate()
                            .plusDays(1)
                            .atStartOfDay(ZoneOffset.UTC)
                            .toInstant());
                    entries.add(statusEntry);
                }
            }
        }

        // Ustawiamy provider z wszystkimi Entry
        InMemoryEntryProvider<Entry> provider = new InMemoryEntryProvider<>();
        provider.addEntries(entries);
        setEntryProvider(provider);
    }

    /**
     * Tworzy i konfiguruje Header Toolbar z nawigacją i wyborem widoków.
     *
     * @return skonfigurowany obiekt Header
     */
    private Header createCustomHeaderToolbar() {
        Header header = new Header();

        HeaderFooterPart endPart = new HeaderFooterPart(HeaderFooterPartPosition.END);
        endPart.addItem(HeaderFooterItem.BUTTON_PREVIOUS_YEAR);
        endPart.addItem(HeaderFooterItem.BUTTON_PREVIOUS);
        endPart.addItem(HeaderFooterItem.BUTTON_TODAY);
        endPart.addItem(HeaderFooterItem.BUTTON_NEXT);
        endPart.addItem(HeaderFooterItem.BUTTON_NEXT_YEAR);
        header.addPart(endPart);

        return header;
    }

    /**
     * Wstrzykuje kod JavaScript nasłuchujący na natywne zdarzenie 'dateClick'.
     */
    public void injectDateClickListener() {
        setOption(Option.SELECTABLE, true);

        getElement()
                .executeJs(
                        """
                        const calendar = this.calendar;
                        if (calendar && !this.__dateClickInjected) {
                            this.__dateClickInjected = true;
                            calendar.on('dateClick', (info) => {
                                this.$server.onDateClick(info.dateStr);
                            });
                        }
                        """);

        getElement()
                .executeJs(
                        """
                const el = this;
                const calendar = this.calendar;
                if (calendar) {
                    // Ukryj kalendarz na moment
                    el.style.visibility = 'hidden';

                    requestAnimationFrame(() => {
                        // Poczekaj aż DOM się ustabilizuje, a potem przelicz i pokaż
                        setTimeout(() => {
                            try {
                                calendar.updateSize();
                            } catch (e) {
                                console.warn('FullCalendar updateSize error:', e);
                            }
                            el.style.visibility = 'visible';
                        }, 200);
                    });
                }
                """);
    }

    // -------------------------------------------------------------------------
    // Metoda Javy odbierająca wywołanie z JS
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
    // Definicja zdarzenia Flow
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
