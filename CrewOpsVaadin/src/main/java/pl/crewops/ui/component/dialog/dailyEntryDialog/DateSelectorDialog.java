package pl.crewops.ui.component.dialog.dailyEntryDialog;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Locale;
import lombok.Getter;
import pl.crewops.model.dto.dailyEntry.DailyEntryDTO;
import pl.crewops.ui.component.custom.FullCalendarCustom;

public class DateSelectorDialog extends Dialog {

    private final FullCalendarCustom calendar = new FullCalendarCustom();
    private final Button close = new Button("close");

    public DateSelectorDialog() {
        setSizeFull();
        setModal(true);

        localize();
        configureCalendar();

        close.setWidth("200px");
        close.addClickListener(event -> this.close());

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setPadding(true);
        content.setSpacing(true);
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        calendar.setWidthFull();
        calendar.setHeightFull();

        VerticalLayout calendarContainer = new VerticalLayout(calendar);
        calendarContainer.setSizeFull();
        calendarContainer.setPadding(false);
        calendarContainer.setSpacing(false);
        calendarContainer.setAlignItems(FlexComponent.Alignment.CENTER);

        content.add(calendarContainer, close);
        content.setFlexGrow(1, calendarContainer); // kalendarz wypełnia całą dostępną przestrzeń
        content.setFlexGrow(0, close); // przycisk zajmuje minimalną przestrzeń

        add(content);
    }

    public void setDailyEntries(Collection<DailyEntryDTO> dailyEntries) {
        calendar.setDailyEntries(dailyEntries);
    }

    private void configureCalendar() {

        this.addOpenedChangeListener(event -> {
            if (event.isOpened()) {
                // Gdy dialog się otworzy (czyli komponent jest w DOM), wstrzykujemy JS.
                // Używamy UI.getCurrent().beforeClientResponse, aby mieć pewność, że komponent JS jest gotowy.
                UI.getCurrent().beforeClientResponse(this, executionContext -> {
                    calendar.injectDateClickListener();
                });
            }
        });

        calendar.addSelectedDateListener(event -> {
            this.close();
            fireEvent(new SelectDateEvent(this, event.getSelectedDate()));
        });
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
