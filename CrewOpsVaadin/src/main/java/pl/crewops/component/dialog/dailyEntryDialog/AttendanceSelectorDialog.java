package pl.crewops.component.dialog.dailyEntryDialog;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;
import pl.crewops.enums.DailyAttendanceStatus;

public class AttendanceSelectorDialog extends Dialog {

    private final RadioButtonGroup<DailyAttendanceStatus> attendanceSelector = new RadioButtonGroup<>();
    private final Button confirmButton = new Button("Potwierdź");
    private final Button cancelButton = new Button("Anuluj");

    public AttendanceSelectorDialog(DailyAttendanceStatus currentStatus) {
        configureDialog();

        var buttonLayout = configuredButtonContainer();

        var mainContainer = configuredMainContainer(buttonLayout);

        add(mainContainer);

        open();
    }

    private void configureDialog() {
        setHeaderTitle("Wybierz Status Obecności");
        setCloseOnEsc(true);
    }

    private VerticalLayout configuredMainContainer(HorizontalLayout buttonLayout) {
        VerticalLayout content = new VerticalLayout(attendanceSelector, buttonLayout);
        // 1. Konfiguracja RadioButtonGroup
        attendanceSelector.setLabel("Status");
        attendanceSelector.setItems(
                DailyAttendanceStatus.PRESENT,
                DailyAttendanceStatus.VACATION,
                DailyAttendanceStatus.SICK_LEAVE,
                DailyAttendanceStatus.OTHER,
                DailyAttendanceStatus.ABSENT);

        attendanceSelector.addValueChangeListener(e -> confirmButton.setEnabled(e.getValue() != null));
        content.setPadding(true);
        return content;
    }

    private HorizontalLayout configuredButtonContainer() {
        HorizontalLayout buttonLayout = new HorizontalLayout(confirmButton, cancelButton);

        // 3. Konfiguracja przycisków
        confirmButton.setEnabled(false);
        confirmButton.addClickListener(event -> {
            DailyAttendanceStatus selectedStatus = attendanceSelector.getValue();
            if (selectedStatus != null) {
                fireEvent(new AttendanceChangeEvent(this, attendanceSelector.getValue()));
                fireEvent(new DialogCloseActionEvent(this, true));
                fireEvent(new DialogCloseActionEvent(this, false));
            }
            close();
        });

        cancelButton.addClickListener(event -> {
            fireEvent(new DialogCloseActionEvent(this, true));
            close();
        });

        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setWidthFull();
        return buttonLayout;
    }

    public abstract class AttendanceSelectorDialogEvent extends ComponentEvent<AttendanceSelectorDialog> {
        public AttendanceSelectorDialogEvent(AttendanceSelectorDialog source) {
            super(source, false);
        }
    }

    public class AttendanceChangeEvent extends AttendanceSelectorDialogEvent {
        @Getter
        private final DailyAttendanceStatus selectedStatus;

        public AttendanceChangeEvent(AttendanceSelectorDialog source, DailyAttendanceStatus selectedStatus) {
            super(source);
            this.selectedStatus = selectedStatus;
        }
    }

    public Registration addAttendanceChangeListener(ComponentEventListener<AttendanceChangeEvent> listener) {
        return addListener(AttendanceChangeEvent.class, listener);
    }
}
