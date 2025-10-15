package pl.crewops.component.dialog.dateSelectorDialog;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.shared.Registration;
import java.time.LocalDate;
import java.util.Arrays;
import lombok.Getter;

public class DateSelectorDialog extends Dialog {

    private final DatePicker datePicker = new DatePicker();

    public DateSelectorDialog() {

        //        // todo: i18n
        datePicker.setPlaceholder("Wybierz datę");

        // todo: implement logic to auto configure I18n for date picker depends on current language selected
        datePicker.setI18n(createPolishI18n());

        datePicker.addValueChangeListener(event -> {
            this.close();
            fireEvent(new SelectDateEvent(this, event.getValue()));
        });

        add(datePicker);
        open();
    }

    private DatePicker.DatePickerI18n createPolishI18n() {
        DatePicker.DatePickerI18n i18n = new DatePicker.DatePickerI18n();
        i18n.setWeekdays(Arrays.asList("Niedziela", "Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota"));
        i18n.setWeekdaysShort(Arrays.asList("Ndz", "Pon", "Wt", "Śr", "Czw", "Pt", "Sob"));
        i18n.setMonthNames(Arrays.asList(
                "Styczeń",
                "Luty",
                "Marzec",
                "Kwiecień",
                "Maj",
                "Czerwiec",
                "Lipiec",
                "Sierpień",
                "Wrzesień",
                "Październik",
                "Listopad",
                "Grudzień"));
        i18n.setToday("Dzisiaj");
        i18n.setCancel("Zamknij");
        i18n.setFirstDayOfWeek(1); // Poniedziałek
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
