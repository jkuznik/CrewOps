package pl.crewops.ui.component.dialog.dailyEntryDialog;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.shared.Registration;
import java.time.LocalDate;
import lombok.Getter;
import pl.crewops.enums.DailyAttendanceStatus;
import pl.crewops.model.dto.dailyEntry.DailyEntryDTO;

public class AttendanceSelectorDialog extends Dialog {

    private final RadioButtonGroup<DailyAttendanceStatus> attendanceSelector = new RadioButtonGroup<>();
    private final Button confirmButton = new Button();
    private final Button cancelButton = new Button();
    private final DailyEntryDTO dailyEntryDTO;

    public AttendanceSelectorDialog(DailyEntryDTO dailyEntryDTO) {
        this.dailyEntryDTO = dailyEntryDTO;
        configureDialog();
        var buttonLayout = configuredButtonContainer();
        var mainContainer = configuredMainContainer(buttonLayout);
        add(mainContainer);
        open();
    }

    private void configureDialog() {
        setHeaderTitle(getTranslation("attendanceSelectorDialog.headerTitle")); // np. "Wybierz status obecności"
        setCloseOnEsc(true);
    }

    private VerticalLayout configuredMainContainer(HorizontalLayout buttonLayout) {
        VerticalLayout content = new VerticalLayout(attendanceSelector, buttonLayout);

        attendanceSelector.setLabel(getTranslation("attendanceSelectorDialog.status.label")); // np. "Status"
        if (dailyEntryDTO.entryDate().isAfter(LocalDate.now())) {
            attendanceSelector.setItems(
                    DailyAttendanceStatus.VACATION,
                    DailyAttendanceStatus.SICK_LEAVE,
                    DailyAttendanceStatus.OTHER,
                    DailyAttendanceStatus.ABSENT);
        } else {
            attendanceSelector.setItems(
                    DailyAttendanceStatus.PRESENT,
                    DailyAttendanceStatus.VACATION,
                    DailyAttendanceStatus.SICK_LEAVE,
                    DailyAttendanceStatus.OTHER,
                    DailyAttendanceStatus.ABSENT);
        }

        attendanceSelector.setItemLabelGenerator(this::getTranslatedLabel);

        attendanceSelector.addValueChangeListener(e -> confirmButton.setEnabled(e.getValue() != null));
        content.setPadding(true);
        return content;
    }

    private HorizontalLayout configuredButtonContainer() {
        HorizontalLayout buttonLayout = new HorizontalLayout(confirmButton, cancelButton);

        confirmButton.setText(getTranslation("attendanceSelectorDialog.button.confirm"));
        cancelButton.setText(getTranslation("attendanceSelectorDialog.button.cancel"));

        confirmButton.setEnabled(false);
        confirmButton.addClickListener(event -> {
            DailyAttendanceStatus selectedStatus = attendanceSelector.getValue();
            if (selectedStatus != null) {
                fireEvent(new AttendanceChangeEvent(this, selectedStatus));
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

    private String getTranslatedLabel(DailyAttendanceStatus status) {
        String result = "";
        switch (status) {
            case PRESENT -> result = getTranslation("attendanceSelectorDialog.status.present");
            case VACATION -> result = getTranslation("attendanceSelectorDialog.status.vacation");
            case SICK_LEAVE -> result = getTranslation("attendanceSelectorDialog.status.sickLeave");
            case OTHER -> result = getTranslation("attendanceSelectorDialog.status.other");
            case ABSENT -> result = getTranslation("attendanceSelectorDialog.status.absent");
        }
        ;
        return result;
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
