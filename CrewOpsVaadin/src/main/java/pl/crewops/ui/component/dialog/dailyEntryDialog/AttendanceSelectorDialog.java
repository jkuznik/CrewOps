package pl.crewops.ui.component.dialog.dailyEntryDialog;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.shared.Registration;
import java.time.LocalDate;
import java.util.Arrays;
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
        configuredButtonContainer();
        add(configuredMainContainer());
        open();

        getFooter().add(cancelButton);
    }

    private void configureDialog() {
        setHeaderTitle(getTranslation("attendanceSelectorDialog.headerTitle")); // np. "Wybierz status obecności"
        setCloseOnEsc(true);

        attendanceSelector.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    }

    private VerticalLayout configuredMainContainer() {
        VerticalLayout content = new VerticalLayout(attendanceSelector, confirmButton);
        content.setPadding(true);
        content.setAlignItems(FlexComponent.Alignment.CENTER);

        attendanceSelector.setLabel(getTranslation("attendanceSelectorDialog.status.label")); // np. "Status"
        if (dailyEntryDTO.entryDate().isAfter(LocalDate.now())) {
            attendanceSelector.setItems(Arrays.stream(DailyAttendanceStatus.values())
                    .filter(status -> {
                        return !status.equals(dailyEntryDTO.attendance())
                                && !status.equals(DailyAttendanceStatus.PRESENT)
                                && !status.equals(DailyAttendanceStatus.NULL);
                    })
                    .toList());
        } else {
            attendanceSelector.setItems(Arrays.stream(DailyAttendanceStatus.values())
                    .filter(status -> {
                        return !status.equals(dailyEntryDTO.attendance()) && !status.equals(DailyAttendanceStatus.NULL);
                    })
                    .toList());
        }

        attendanceSelector.setItemLabelGenerator(this::getTranslatedLabel);

        attendanceSelector.addValueChangeListener(e -> {
            confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            confirmButton.setEnabled(e.getValue() != null);
        });

        return content;
    }

    private void configuredButtonContainer() {
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
