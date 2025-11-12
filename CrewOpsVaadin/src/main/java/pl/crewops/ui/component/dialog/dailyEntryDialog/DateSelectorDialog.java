package pl.crewops.ui.component.dialog.dailyEntryDialog;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.shared.Registration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Locale;
import lombok.Getter;
import pl.crewops.ui.component.custom.FullCalendarCustom;

public class DateSelectorDialog extends Dialog {

    private final FullCalendarCustom calendar = new FullCalendarCustom();
    private final Button close = new Button("close");

    public DateSelectorDialog() {
        setSizeFull();
        localize();

        calendar.addSelectedDateListener(event -> {
            // Zdarzenie z FullCalendarCustom jest wywoływane w Javie,
            // co powoduje wyemitowanie zdarzenia SelectDateEvent i zamknięcie dialogu.
            this.close();
            fireEvent(new SelectDateEvent(this, event.getSelectedDate()));
        });

        close.addClickListener(event -> {
            this.close();
        });

        // KLUCZOWA ZMIANA: Dodanie listenera na otwarcie dialogu
        this.addOpenedChangeListener(event -> {
            if (event.isOpened()) {
                // Gdy dialog się otworzy (czyli komponent jest w DOM), wstrzykujemy JS.
                // Używamy UI.getCurrent().beforeClientResponse, aby mieć pewność, że komponent JS jest gotowy.
                UI.getCurrent().beforeClientResponse(this, executionContext -> {
                    calendar.injectDateClickListener();
                });
            }
        });

        add(calendar, close);
    }

    public void setDate(LocalDate localDate) {
        calendar.gotoDate(localDate);
    }

    private void localize() {
        // Ustawienie tekstu placeholder
        //        datePicker.setPlaceholder(getTranslation("dateSelectorDialog.placeholder"));

        // Ustawienie dynamicznej konfiguracji I18n dla DatePickera
        calendar.setLocale(Locale.getDefault());
    }

    private DatePicker.DatePickerI18n createI18n() {
        DatePicker.DatePickerI18n i18n = new DatePicker.DatePickerI18n();

        // Tłumaczenia Dni Tygodnia
        i18n.setWeekdays(Arrays.asList(
                getTranslation("datePicker.weekday.sunday"),
                getTranslation("datePicker.weekday.monday"),
                getTranslation("datePicker.weekday.tuesday"),
                getTranslation("datePicker.weekday.wednesday"),
                getTranslation("datePicker.weekday.thursday"),
                getTranslation("datePicker.weekday.friday"),
                getTranslation("datePicker.weekday.saturday")));

        // Tłumaczenia Skrótów Dni Tygodnia
        i18n.setWeekdaysShort(Arrays.asList(
                getTranslation("datePicker.weekdayShort.sun"),
                getTranslation("datePicker.weekdayShort.mon"),
                getTranslation("datePicker.weekdayShort.tue"),
                getTranslation("datePicker.weekdayShort.wed"),
                getTranslation("datePicker.weekdayShort.thu"),
                getTranslation("datePicker.weekdayShort.fri"),
                getTranslation("datePicker.weekdayShort.sat")));

        // Tłumaczenia Nazw Miesięcy
        i18n.setMonthNames(Arrays.asList(
                getTranslation("datePicker.month.january"),
                getTranslation("datePicker.month.february"),
                getTranslation("datePicker.month.march"),
                getTranslation("datePicker.month.april"),
                getTranslation("datePicker.month.may"),
                getTranslation("datePicker.month.june"),
                getTranslation("datePicker.month.july"),
                getTranslation("datePicker.month.august"),
                getTranslation("datePicker.month.september"),
                getTranslation("datePicker.month.october"),
                getTranslation("datePicker.month.november"),
                getTranslation("datePicker.month.december")));

        // Przyciski i opcje kalendarza
        i18n.setToday(getTranslation("datePicker.today"));
        i18n.setCancel(getTranslation("datePicker.cancel"));

        // Ważne: firstDayOfWeek (0 = Niedziela, 1 = Poniedziałek).
        // W PL to jest 1, ale w USA to jest 0.
        // Musisz to ustawić dynamicznie, jeśli chcesz pełnej obsługi i18n.
        // Zakładamy, że masz gdzieś dostęp do aktualnej Locale:
        Locale currentLocale = UI.getCurrent().getLocale(); // Lub inny sposób pobrania locale
        if (currentLocale.getLanguage().equals("pl")) {
            i18n.setFirstDayOfWeek(1); // Poniedziałek dla PL
        } else {
            i18n.setFirstDayOfWeek(0); // Niedziela dla EN
        }

        return i18n;
    }

    public abstract class DateSelectorDialogEvent extends ComponentEvent<DateSelectorDialog> {

        public DateSelectorDialogEvent(DateSelectorDialog source, boolean fromClient) {
            super(source, fromClient);
        }
    }

    public class SelectDateEvent extends DateSelectorDialogEvent {

        @Getter
        private final LocalDate selectedDate;

        public SelectDateEvent(DateSelectorDialog source, LocalDate selectedDate) {
            super(source, false);
            this.selectedDate = selectedDate;
        }
    }

    public Registration addSelectDateListener(ComponentEventListener<SelectDateEvent> listener) {
        return addListener(SelectDateEvent.class, listener);
    }
}
