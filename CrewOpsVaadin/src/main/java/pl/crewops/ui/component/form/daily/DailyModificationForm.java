package pl.crewops.ui.component.form.daily;

import static pl.crewops.enums.DailyAttendanceStatus.*;
import static pl.crewops.enums.DailyEntryStatus.*;
import static pl.crewops.ui.view.DailyView.FORMS_BORDER_PX;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.shared.Registration;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Getter;
import pl.crewops.enums.DailyAttendanceStatus;
import pl.crewops.enums.DailyEntryStatus;
import pl.crewops.model.dto.dailyEntry.DailyEntryDTO;
import pl.crewops.ui.component.dialog.dailyEntryDialog.AttendanceSelectorDialog;
import pl.crewops.ui.view.DailyView;
import pl.crewops.util.AuthenticationResolver;

public class DailyModificationForm extends FormLayout {

    private final AuthenticationResolver authenticationResolver;

    private final Span headerTextLabel = new Span();
    private final Icon helpIcon = VaadinIcon.INFO_CIRCLE.create();
    private final Span entryStatusInformation = new Span();

    private final Button addButton = new Button();
    private final Button confirmPresenceButton = new Button();
    private final Button changeTimesheetButton = new Button();
    // TODO: that option should be avail able only for managers, consider allow shift leader mark attendance as present
    // or absent in daily raports form <-
    private final Button changeAttendanceButton = new Button();
    private final Button approveButton = new Button();

    private DailyEntryDTO dailyEntryDTO = DailyEntryDTO.builder().status(EMPTY).build();

    public DailyModificationForm(AuthenticationResolver authenticationResolver) {
        this.authenticationResolver = authenticationResolver;

        localize();

        var mainContainer = configuredMainContainer();

        var buttonsContainer = configuredButtonsContainer();

        mainContainer.add(configuredHeader(), entryStatusInformation, buttonsContainer, spacer());

        add(mainContainer);

        updateState();
    }

    private void localize() {
        headerTextLabel.setText(getTranslation("dailyModificationForm.headerTextLabel"));
        addButton.setText(getTranslation("dailyModificationForm.addButton"));
        confirmPresenceButton.setText(getTranslation("dailyModificationForm.confirmPresenceButton"));
        changeTimesheetButton.setText(getTranslation("dailyModificationForm.updateButton"));
        changeAttendanceButton.setText(getTranslation("dailyModificationForm.changeAttendanceButton"));
        approveButton.setText(getTranslation("dailyModificationForm.approveButton"));
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
        mainContainer.getStyle().set("border", FORMS_BORDER_PX + " solid #ccc");
        mainContainer.getStyle().set("border-radius", "4px");
        mainContainer.getStyle().set("padding", "10px");
        mainContainer.setMaxHeight(DailyView.FORMS_HEIGHT);
        mainContainer.setMaxWidth(DailyView.FORMS_WIDTH);
        return mainContainer;
    }

    private VerticalLayout configuredButtons() {
        var container = new VerticalLayout();

        setButtonIcon(addButton, VaadinIcon.CLIPBOARD_CHECK);
        addButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        setButtonIcon(changeTimesheetButton, VaadinIcon.CLIPBOARD_CHECK);
        changeTimesheetButton.addThemeVariants(ButtonVariant.LUMO_WARNING);
        setButtonIcon(confirmPresenceButton, VaadinIcon.CHECK_CIRCLE);
        confirmPresenceButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        setButtonIcon(changeAttendanceButton, VaadinIcon.PAPERPLANE);
        changeAttendanceButton.addThemeVariants(ButtonVariant.LUMO_WARNING);
        setButtonIcon(approveButton, VaadinIcon.THUMBS_UP);
        approveButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

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

    private HorizontalLayout configuredHeader() {
        headerTextLabel.getStyle().set("font-weight", "bold");
        headerTextLabel.getStyle().set("font-size", "1.1em");

        helpIcon.setColor("var(--lumo-contrast-50pct)");
        helpIcon.getStyle().set("cursor", "pointer");

        Tooltip.forComponent(helpIcon).withPosition(Tooltip.TooltipPosition.BOTTOM_END);

        var headerLayout = new HorizontalLayout();
        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        headerLayout.add(headerTextLabel, helpIcon);

        return headerLayout;
    }

    public void setDailyEntry(DailyEntryDTO dailyEntryDTO) {
        this.dailyEntryDTO = dailyEntryDTO;
        updateState();
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
        } else {
            if (dailyEntryDTO.status().equals(DRAFT) && dailyEntryDTO.endTime().isBefore(Instant.now())) {
                entryStatus = PENDING;
            } else {
                entryStatus = dailyEntryDTO.status();
            }
        }

        switch (entryStatus) {
            case EMPTY -> {
                // The entry does not exist. The only possible action is to create a new entry.
                // The 'addButton' should be visible regardless of user permissions.
                entryInfoText = getTranslation("dailyModificationForm.status.empty.info");
                tooltipText = getTranslation("dailyModificationForm.status.empty.tooltip");
                addButton.setVisible(true);
            }

            case DRAFT -> {
                // The entry exists but is in the Draft state. It does not necessarily await supervision confirmation.
                // Access depends on whether the entry date is in the past/today or the future.
                entryInfoText = getTranslation("dailyModificationForm.status.draft.info");
                tooltipText = getTranslation("dailyModificationForm.status.draft.tooltip");

                LocalDate entryDate = dailyEntryDTO.entryDate();
                boolean isFutureEntry = entryDate.isAfter(LocalDate.now());

                if (isFutureEntry) {
                    // Future Entry: Only supervision (Shift Leader) can modify.
                    if (userIsShiftLeader) {
                        changeTimesheetButton.setVisible(true);
                        changeAttendanceButton.setVisible(true);
                    }
                    // Regular user: No actions allowed for future drafts.

                } else {
                    // Past or Today Entry: Regular user and supervision can modify.
                    changeTimesheetButton.setVisible(true); // User and supervision modification enabled.

                    // Regular user action: Confirm presence if attendance is NULL/missing.
                    if (dailyEntryDTO.attendance() == NULL
                            || dailyEntryDTO.attendance() == OTHER
                            || dailyEntryDTO.attendance() == null) {
                        confirmPresenceButton.setVisible(true);
                    }

                    // Supervision actions: Change attendance.
                    if (userIsShiftLeader) {
                        changeAttendanceButton.setVisible(true);
                    }
                }
            }

            case PENDING -> {
                entryInfoText = getTranslation("dailyModificationForm.status.pending.info");
                tooltipText = getTranslation("dailyModificationForm.status.pending.tooltip");

                changeTimesheetButton.setVisible(true);

                if (userIsShiftLeader) {
                    changeAttendanceButton.setVisible(true);
                    if (!dailyEntryDTO.entryDate().isAfter(LocalDate.now()) && dailyEntryDTO.attendance() == PRESENT) {
                        approveButton.setVisible(true);
                    }
                }
            }
            case APPROVED -> {
                entryInfoText = getTranslation("dailyModificationForm.status.approved.info");
                tooltipText = getTranslation("dailyModificationForm.status.approved.tooltip");

                changeTimesheetButton.setVisible(true);

                if (userIsManager) {
                    changeAttendanceButton.setVisible(true);
                }
            }
            case MANUAL_EDITED -> {
                entryInfoText = getTranslation("dailyModificationForm.status.manualEdited.info");
                tooltipText = getTranslation("dailyModificationForm.status.manualEdited.tooltip");

                changeTimesheetButton.setVisible(true);

                if (userIsShiftLeader) {
                    changeAttendanceButton.setVisible(true);
                    if (!dailyEntryDTO.entryDate().isAfter(LocalDate.now()) && dailyEntryDTO.attendance() == PRESENT) {
                        approveButton.setVisible(true);
                    }
                }
            }
            case AUTO_GENERATED -> {
                entryInfoText = getTranslation("dailyModificationForm.status.autoGenerated.info");
                tooltipText = getTranslation("dailyModificationForm.status.autoGenerated.tooltip");

                confirmPresenceButton.setVisible(true);
                changeTimesheetButton.setVisible(true);

                if (userIsShiftLeader) {
                    changeAttendanceButton.setVisible(true);
                    if (!dailyEntryDTO.entryDate().isAfter(LocalDate.now()) && dailyEntryDTO.attendance() == PRESENT) {
                        approveButton.setVisible(true);
                    }
                }
            }
            default -> {
                entryInfoText = getTranslation("dailyModificationForm.status.unknown.info");
                tooltipText = getTranslation("dailyModificationForm.status.unknown.tooltip");
            }
        }
        entryStatusInformation.setText(entryInfoText);
        Tooltip.forComponent(helpIcon).setText(tooltipText);
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

    public abstract static class DailyModificationFormEvent extends ComponentEvent<DailyModificationForm> {
        public DailyModificationFormEvent(DailyModificationForm source) {
            super(source, false);
        }
    }

    public static class CreateDailyEntryEvent extends DailyModificationFormEvent {
        public CreateDailyEntryEvent(DailyModificationForm source) {
            super(source);
        }
    }

    public static class UpdateDailyEntryEvent extends DailyModificationFormEvent {
        public UpdateDailyEntryEvent(DailyModificationForm source) {
            super(source);
        }
    }

    public static class ConfirmAttendanceEvent extends DailyModificationFormEvent {
        public ConfirmAttendanceEvent(DailyModificationForm source) {
            super(source);
        }
    }

    public static class ChangeAttendanceEvent extends DailyModificationFormEvent {
        @Getter
        private final DailyAttendanceStatus status;

        public ChangeAttendanceEvent(DailyModificationForm source, DailyAttendanceStatus status) {
            super(source);
            this.status = status;
        }
    }

    public static class ApproveDailyEntryEvent extends DailyModificationFormEvent {
        public ApproveDailyEntryEvent(DailyModificationForm source) {
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
