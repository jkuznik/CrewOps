package pl.crewops.ui.component.panel.daily;

import static pl.crewops.enums.DailyAttendanceStatus.*;
import static pl.crewops.enums.DailyEntryStatus.*;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Getter;
import pl.crewops.enums.DailyAttendanceStatus;
import pl.crewops.enums.DailyEntryStatus;
import pl.crewops.model.dto.dailyEntry.DailyEntryDTO;
import pl.crewops.ui.component.custom.PanelCustom;
import pl.crewops.ui.component.dialog.dailyEntryDialog.AttendanceSelectorDialog;
import pl.crewops.util.AuthenticationResolver;

public class DailyModificationPanel extends PanelCustom {

    private final AuthenticationResolver authenticationResolver;

    private final Span attendanceText = new Span();
    private final Span attendanceStatusDisplay = new Span();
    private final Span entryStatusText = new Span();
    private final Span entryStatusDisplay = new Span();

    private final Button addButton = new Button();
    private final Button confirmPresenceButton = new Button();
    private final Button changeTimesheetButton = new Button();
    private final Button changeAttendanceButton = new Button();
    private final Button approveButton = new Button();

    private DailyEntryDTO dailyEntryDTO =
            DailyEntryDTO.builder().status(EMPTY).attendance(NULL).build();

    public DailyModificationPanel(AuthenticationResolver authenticationResolver) {

        this.authenticationResolver = authenticationResolver;

        localize();

        var mainContainer = configuredMainContainer();
        var buttonsContainer = configuredButtonsContainer();

        mainContainer.add(configuredEntryInformationContainer(), buttonsContainer, spacer());

        setContent(mainContainer);

        updateState();
    }

    private void localize() {
        setSummary(VaadinIcon.CALENDAR_CLOCK, getTranslation("dailyModificationForm.headerTextLabel"));
        attendanceText.setText(getTranslation("dailyModificationForm.attendanceStatusHeader"));
        entryStatusText.setText(getTranslation("dailyModificationForm.entryStatusHeader"));
        addButton.setText(getTranslation("dailyModificationForm.addButton"));
        confirmPresenceButton.setText(getTranslation("dailyModificationForm.confirmPresenceButton"));
        changeTimesheetButton.setText(getTranslation("dailyModificationForm.updateButton"));
        changeAttendanceButton.setText(getTranslation("dailyModificationForm.changeAttendanceButton"));
        approveButton.setText(getTranslation("dailyModificationForm.approveButton"));
    }

    private Component configuredEntryInformationContainer() {

        var container = new VerticalLayout();
        container.setSizeUndefined();
        container.setPadding(true);
        container.setSpacing(true);

        var badgeWidth = "130px";

        var horizontalLayout1 = new HorizontalLayout(attendanceText, attendanceStatusDisplay);
        horizontalLayout1.setWidthFull();
        horizontalLayout1.setAlignItems(FlexComponent.Alignment.START);

        attendanceText.getElement().getThemeList().add("badge small contrast");
        attendanceText.getStyle().set("font-size", "1.0em");
        attendanceText.getStyle().set("padding", "0.2em 0.4em");
        attendanceText.getStyle().set("border-radius", "8px");
        attendanceText.getStyle().set("border-style", "solid");
        attendanceText.getStyle().set("border", "1px solid #B0B0B0");

        attendanceText.getStyle().set("width", badgeWidth);
        attendanceText.getStyle().set("flex-shrink", "0");

        attendanceStatusDisplay.getStyle().set("font-weight", "bold");
        attendanceStatusDisplay.getStyle().set("line-height", "1.5");
        attendanceStatusDisplay.getStyle().set("flex-grow", "1");

        var horizontalLayout2 = new HorizontalLayout(entryStatusText, entryStatusDisplay);
        horizontalLayout2.setWidthFull();
        horizontalLayout2.setAlignItems(FlexComponent.Alignment.START);

        entryStatusText.getElement().getThemeList().add("badge small contrast");
        entryStatusText.getStyle().set("font-size", "1.0em");
        entryStatusText.getStyle().set("padding", "0.2em 0.4em");
        entryStatusText.getStyle().set("border-radius", "8px");
        entryStatusText.getStyle().set("border-style", "solid");
        entryStatusText.getStyle().set("border", "1px solid #B0B0B0");

        entryStatusText.getStyle().set("width", badgeWidth);
        entryStatusText.getStyle().set("flex-shrink", "0");

        entryStatusDisplay.getStyle().set("font-weight", "bold");
        entryStatusDisplay.getStyle().set("line-height", "1.5");
        entryStatusDisplay.getStyle().set("flex-grow", "1");

        container.add(horizontalLayout1, horizontalLayout2);

        return container;
    }

    public void updateState() {
        boolean userIsShiftLeader = authenticationResolver.principalHasShiftLeaderPermission();
        boolean userIsManager = authenticationResolver.principalHasManagerPermission();

        setAllButtonsVisible(false);

        String entryInfoText;
        String tooltipText;
        DailyEntryStatus entryStatus;

        if (dailyEntryDTO == null) {
            entryStatus = EMPTY;
            setAttendanceStatus(DailyAttendanceStatus.NULL);
        } else {
            if (dailyEntryDTO.status().equals(DRAFT) && shiftEndsMoreThanOneHourAgo()) {
                entryStatus = PENDING;
            } else {
                entryStatus = dailyEntryDTO.status();
            }
            setAttendanceStatus(dailyEntryDTO.attendance());
        }

        switch (entryStatus) {
                // todo : Zmieniające się kolory informacji w zależności od statusu wpisu
            case EMPTY -> {
                entryStatusDisplay.getStyle().set("color", "gray");
                entryInfoText = getTranslation("dailyTimeline.noEntry");
                tooltipText = getTranslation("dailyModificationForm.status.empty.tooltip");
                addButton.setVisible(true);
            }

            case DRAFT -> {
                entryStatusDisplay.getStyle().set("color", "white");
                entryInfoText = getTranslation("dailyModificationForm.status.draft.info");
                tooltipText = getTranslation("dailyModificationForm.status.draft.tooltip");

                LocalDate entryDate = dailyEntryDTO.entryDate();
                boolean isFutureEntry = entryDate.isAfter(LocalDate.now());

                if (isFutureEntry) {
                    if (userIsShiftLeader) {
                        changeTimesheetButton.setVisible(true);
                        changeAttendanceButton.setVisible(true);
                    }
                } else {
                    changeTimesheetButton.setVisible(true);

                    if (dailyEntryDTO.attendance() == NULL
                            || dailyEntryDTO.attendance() == OTHER
                            || dailyEntryDTO.attendance() == null) {
                        confirmPresenceButton.setVisible(true);
                    }

                    if (userIsShiftLeader) {
                        changeAttendanceButton.setVisible(true);
                    }
                }
            }

            case PENDING -> {
                entryStatusDisplay.getStyle().set("color", "white");
                entryInfoText = getTranslation("dailyModificationForm.status.pending.info");
                tooltipText = getTranslation("dailyModificationForm.status.pending.tooltip");

                changeTimesheetButton.setVisible(true);

                if (dailyEntryDTO.attendance() == NULL
                        || dailyEntryDTO.attendance() == OTHER
                        || dailyEntryDTO.attendance() == null) {
                    confirmPresenceButton.setVisible(true);
                }

                if (userIsShiftLeader) {
                    changeAttendanceButton.setVisible(true);
                    if (!dailyEntryDTO.entryDate().isAfter(LocalDate.now())
                            && (dailyEntryDTO.attendance() != null && dailyEntryDTO.attendance() != NULL)) {
                        approveButton.setVisible(true);
                    }
                }
            }
            case APPROVED -> {
                entryStatusDisplay.getStyle().set("color", "#10D965"); // LUMO SUCCESS green color
                entryInfoText = getTranslation("dailyModificationForm.status.approved.info");
                tooltipText = getTranslation("dailyModificationForm.status.approved.tooltip");

                changeTimesheetButton.setVisible(true);

                if (userIsManager) {
                    changeAttendanceButton.setVisible(true);
                }
            }
            case MANUAL_EDITED -> {
                entryStatusDisplay.getStyle().set("color", "white");
                entryInfoText = getTranslation("dailyModificationForm.status.manualEdited.info");
                tooltipText = getTranslation("dailyModificationForm.status.manualEdited.tooltip");

                LocalDate entryDate = dailyEntryDTO.entryDate();
                boolean isFutureEntry = entryDate.isAfter(LocalDate.now());

                if (isFutureEntry) {
                    if (userIsShiftLeader) {
                        changeTimesheetButton.setVisible(true);
                        changeAttendanceButton.setVisible(true);
                    }
                } else {
                    changeTimesheetButton.setVisible(true);

                    if (dailyEntryDTO.attendance() == NULL
                            || dailyEntryDTO.attendance() == OTHER
                            || dailyEntryDTO.attendance() == null) {
                        confirmPresenceButton.setVisible(true);
                    }

                    if (userIsShiftLeader) {
                        changeAttendanceButton.setVisible(true);
                    }
                    if (userIsManager) {
                        approveButton.setVisible(true);
                    }
                }
            }
            case AUTO_GENERATED -> {
                entryStatusDisplay.getStyle().set("color", "white");
                entryInfoText = getTranslation("dailyModificationForm.status.autoGenerated.info");
                tooltipText = getTranslation("dailyModificationForm.status.autoGenerated.tooltip");

                changeTimesheetButton.setVisible(true);

                if (dailyEntryDTO.attendance() == NULL
                        || dailyEntryDTO.attendance() == OTHER
                        || dailyEntryDTO.attendance() == null) {
                    confirmPresenceButton.setVisible(true);
                }

                if (userIsShiftLeader) {
                    changeAttendanceButton.setVisible(true);
                    if (!dailyEntryDTO.entryDate().isAfter(LocalDate.now())
                            && (dailyEntryDTO.attendance() != null
                                    && dailyEntryDTO.attendance() != OTHER
                                    && dailyEntryDTO.attendance() != NULL)) {
                        approveButton.setVisible(true);
                    }
                }
            }
            default -> {
                entryStatusDisplay.getStyle().set("color", "white");
                entryInfoText = getTranslation("dailyModificationForm.status.unknown.info");
                tooltipText = getTranslation("dailyModificationForm.status.unknown.tooltip");
            }
        }
        entryStatusDisplay.setText(entryInfoText);
    }

    public void setAttendanceStatus(DailyAttendanceStatus status) {

        attendanceStatusDisplay.getStyle().remove("color");
        attendanceStatusDisplay.getStyle().set("font-weight", "bold");

        switch (status) {
            case PRESENT -> {
                attendanceStatusDisplay.getStyle().set("color", "#10D965"); // LUMO_SUCCESS green like
                attendanceStatusDisplay.setText(getTranslation("dailyTimeline.present"));
            }
            case VACATION -> {
                attendanceStatusDisplay.getStyle().set("color", "#007bff"); // Blue
                attendanceStatusDisplay.setText(getTranslation("dailyTimeline.vacation"));
            }
            case SICK_LEAVE -> {
                attendanceStatusDisplay.getStyle().set("color", "#007bff"); // Blue
                attendanceStatusDisplay.setText(getTranslation("dailyTimeline.sickLeave"));
            }
            case ABSENT -> {
                attendanceStatusDisplay.getStyle().set("color", "red");
                attendanceStatusDisplay.setText(getTranslation("dailyTimeline.absent"));
            }
            case OTHER -> {
                attendanceStatusDisplay.getStyle().set("color", "gray");
                attendanceStatusDisplay.setText(getTranslation("dailyTimeline.other"));
            }
            case NULL -> {
                attendanceStatusDisplay.getStyle().set("color", "gray");
                attendanceStatusDisplay.setText(getTranslation("dailyTimeline.noEntry"));
            }
        }
    }

    private static Div spacer() {
        var spacer = new Div();
        spacer.setHeight("400px");
        return spacer;
    }

    private VerticalLayout configuredButtonsContainer() {
        var buttonsContainer = new VerticalLayout();
        buttonsContainer.setSpacing(true);
        buttonsContainer.setPadding(false);

        buttonsContainer.add(configuredButtons());
        return buttonsContainer;
    }

    private static VerticalLayout configuredMainContainer() {
        var mainContainer = new VerticalLayout();
        mainContainer.getStyle().set("padding", "10px");
        return mainContainer;
    }

    private VerticalLayout configuredButtons() {
        var container = new VerticalLayout();

        setButtonIcon(addButton, VaadinIcon.CLIPBOARD_CHECK);
        addButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        addButton.setWidthFull();
        setButtonIcon(changeTimesheetButton, VaadinIcon.CLIPBOARD_CHECK);
        changeTimesheetButton.addThemeVariants(ButtonVariant.LUMO_WARNING);
        changeTimesheetButton.setWidthFull();
        setButtonIcon(confirmPresenceButton, VaadinIcon.CHECK_CIRCLE);
        confirmPresenceButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        confirmPresenceButton.setWidthFull();
        setButtonIcon(changeAttendanceButton, VaadinIcon.PAPERPLANE);
        changeAttendanceButton.addThemeVariants(ButtonVariant.LUMO_WARNING);
        changeAttendanceButton.setWidthFull();
        setButtonIcon(approveButton, VaadinIcon.HANDSHAKE);
        approveButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        approveButton.setWidthFull();

        addButton.addClickListener(event -> {
            fireEvent(new CreateDailyEntryEvent(this));
        });

        changeTimesheetButton.addClickListener(event -> {
            fireEvent(new UpdateDailyEntryEvent(this));
        });

        confirmPresenceButton.addClickListener(event -> {
            fireEvent(new ConfirmAttendanceEvent(this));
        });

        changeAttendanceButton.addClickListener(event -> {
            AttendanceSelectorDialog attendanceSelectorDialog = new AttendanceSelectorDialog(dailyEntryDTO);
            Registration registration = attendanceSelectorDialog.addAttendanceChangeListener(event1 -> {
                fireEvent(new ChangeAttendanceEvent(this, event1.getSelectedStatus()));
            });
            attendanceSelectorDialog.addDialogCloseActionListener(close -> registration.remove());
            attendanceSelectorDialog.open();
        });

        approveButton.addClickListener(event -> {
            fireEvent(new ApproveDailyEntryEvent(this));
        });

        container.add(addButton, changeTimesheetButton, confirmPresenceButton, changeAttendanceButton, approveButton);

        return container;
    }

    public void setDailyEntry(DailyEntryDTO dailyEntryDTO) {
        this.dailyEntryDTO = dailyEntryDTO;
        updateState();
    }

    private boolean shiftEndsMoreThanOneHourAgo() {
        return dailyEntryDTO.endTime() != null
                && dailyEntryDTO.endTime().plus(Duration.ofHours(1)).isBefore(Instant.now());
    }

    private void setAllButtonsVisible(boolean visible) {
        addButton.setVisible(visible);
        changeTimesheetButton.setVisible(visible);
        confirmPresenceButton.setVisible(visible);
        changeAttendanceButton.setVisible(visible);
        approveButton.setVisible(visible);
    }

    private void setButtonIcon(Button button, VaadinIcon iconType) {
        button.setIcon(new Icon(iconType));
    }

    public abstract static class DailyModificationFormEvent extends ComponentEvent<DailyModificationPanel> {
        public DailyModificationFormEvent(DailyModificationPanel source) {
            super(source, false);
        }
    }

    public static class CreateDailyEntryEvent extends DailyModificationFormEvent {
        public CreateDailyEntryEvent(DailyModificationPanel source) {
            super(source);
        }
    }

    public static class UpdateDailyEntryEvent extends DailyModificationFormEvent {
        public UpdateDailyEntryEvent(DailyModificationPanel source) {
            super(source);
        }
    }

    public static class ConfirmAttendanceEvent extends DailyModificationFormEvent {
        public ConfirmAttendanceEvent(DailyModificationPanel source) {
            super(source);
        }
    }

    public static class ChangeAttendanceEvent extends DailyModificationFormEvent {
        @Getter
        private final DailyAttendanceStatus status;

        public ChangeAttendanceEvent(DailyModificationPanel source, DailyAttendanceStatus status) {
            super(source);
            this.status = status;
        }
    }

    public static class ApproveDailyEntryEvent extends DailyModificationFormEvent {
        public ApproveDailyEntryEvent(DailyModificationPanel source) {
            super(source);
        }
    }

    public Registration addCreateDailyEventListener(ComponentEventListener<CreateDailyEntryEvent> listener) {
        return addListener(CreateDailyEntryEvent.class, listener);
    }

    public Registration addChangeTimesheetEventListener(ComponentEventListener<UpdateDailyEntryEvent> listener) {
        return addListener(UpdateDailyEntryEvent.class, listener);
    }

    public Registration addConfirmAttendanceEventListener(ComponentEventListener<ConfirmAttendanceEvent> listener) {
        return addListener(ConfirmAttendanceEvent.class, listener);
    }

    public Registration addApproveDailyEventListener(ComponentEventListener<ApproveDailyEntryEvent> listener) {
        return addListener(ApproveDailyEntryEvent.class, listener);
    }

    public Registration addChangeAttendanceEventListener(ComponentEventListener<ChangeAttendanceEvent> listener) {
        return addListener(ChangeAttendanceEvent.class, listener);
    }
}
