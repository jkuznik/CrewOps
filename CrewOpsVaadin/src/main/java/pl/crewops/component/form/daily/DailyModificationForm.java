package pl.crewops.component.form.daily;

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
import lombok.Setter;
import pl.crewops.enums.DailyEntryStatus;
import pl.crewops.util.AuthenticationResolver;
import pl.crewops.view.DailyView;

public class DailyModificationForm extends FormLayout {

    private final AuthenticationResolver authenticationResolver;

    private final Span headerTextLabel = new Span();
    private final Icon helpIcon = VaadinIcon.INFO_CIRCLE.create();
    private final Span entryStatusInformation = new Span();

    private final Button addButton = new Button();
    private final Button confirmPresenceButton = new Button();
    private final Button updateButton = new Button();
    private final Button changeAttendanceButton = new Button();
    private final Button approveButton = new Button();

    private DailyEntryStatus currentStatus = DailyEntryStatus.EMPTY;

    @Setter
    private boolean isAttendanceSelected = false;

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
        updateButton.setText(getTranslation("dailyModificationForm.updateButton"));
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
        mainContainer.getStyle().set("border", "1px solid #ccc");
        mainContainer.getStyle().set("border-radius", "4px");
        mainContainer.getStyle().set("padding", "10px");
        mainContainer.setMaxHeight(DailyView.FORMS_HEIGHT);
        mainContainer.setMaxWidth(DailyView.FORMS_WIDTH);
        return mainContainer;
    }

    private VerticalLayout configuredButtons() {
        var container = new VerticalLayout();

        setButtonIcon(addButton, VaadinIcon.CLIPBOARD_CHECK);
        setButtonIcon(updateButton, VaadinIcon.CLIPBOARD_CHECK);
        setButtonIcon(confirmPresenceButton, VaadinIcon.CHECK_CIRCLE);
        setButtonIcon(changeAttendanceButton, VaadinIcon.PAPERPLANE);
        setButtonIcon(approveButton, VaadinIcon.THUMBS_UP);

        addButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        addButton.addClickListener(event -> {
            fireEvent(new CreateDailyEntryEvent(this));
        });
        updateButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        confirmPresenceButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        changeAttendanceButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        approveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        container.add(addButton, updateButton, confirmPresenceButton, changeAttendanceButton, approveButton);

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

    public void setCurrentStatus(DailyEntryStatus status) {
        this.currentStatus = status;
        updateState();
    }

    public void updateState() {

        boolean userIsShiftLeader = authenticationResolver.principalHasShiftLeaderPermission();
        boolean userIsManager = authenticationResolver.principalHasManagerPermission();

        String entryInfoTextKey;
        String tooltipTextKey;

        setAllButtonsVisible(false);

        // W DailyModificationForm.java w metodzie updateState()

        // Zmieniamy deklaracje zmiennych na początku metody,
        // usuwając "Key", aby odzwierciedlały, że przechowują GŁÓWNY TEKST
        String entryInfoText;
        String tooltipText;

        // ... (logika setAllButtonsVisible(false) i uprawnienia)

        switch (currentStatus) {
            case EMPTY -> {
                // Bezpośrednie wywołanie getTranslation() w miejscu przypisania
                entryInfoText = getTranslation("dailyModificationForm.status.empty.info");
                tooltipText = getTranslation("dailyModificationForm.status.empty.tooltip");
                addButton.setVisible(true);
            }
            case DRAFT -> {
                entryInfoText = getTranslation("dailyModificationForm.status.draft.info");
                tooltipText = getTranslation("dailyModificationForm.status.draft.tooltip");
                updateButton.setVisible(true);

                if (isAttendanceSelected) {
                    changeAttendanceButton.setVisible(true);
                } else {
                    confirmPresenceButton.setVisible(true);
                }
            }
            case PENDING -> {
                entryInfoText = getTranslation("dailyModificationForm.status.pending.info");
                tooltipText = getTranslation("dailyModificationForm.status.pending.tooltip");

                updateButton.setVisible(true);

                if (userIsShiftLeader) {
                    approveButton.setVisible(true);
                    changeAttendanceButton.setVisible(true);
                }
            }
            case APPROVED -> {
                entryInfoText = getTranslation("dailyModificationForm.status.approved.info");
                tooltipText = getTranslation("dailyModificationForm.status.approved.tooltip");

                updateButton.setVisible(true);

                if (userIsManager) {
                    changeAttendanceButton.setVisible(true);
                }
            }
            case MANUAL_EDITED -> {
                entryInfoText = getTranslation("dailyModificationForm.status.manualEdited.info");
                tooltipText = getTranslation("dailyModificationForm.status.manualEdited.tooltip");

                updateButton.setVisible(true);

                if (userIsShiftLeader) {
                    approveButton.setVisible(true);
                    changeAttendanceButton.setVisible(true);
                }
            }
            case AUTO_GENERATED -> {
                entryInfoText = getTranslation("dailyModificationForm.status.autoGenerated.info");
                tooltipText = getTranslation("dailyModificationForm.status.autoGenerated.tooltip");

                confirmPresenceButton.setVisible(true);
                updateButton.setVisible(true);

                if (userIsShiftLeader) {
                    changeAttendanceButton.setVisible(true);
                    approveButton.setVisible(true);
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
        updateButton.setVisible(visible);
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
        public ChangeAttendanceEvent(DailyModificationForm source) {
            super(source);
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

    public Registration addUpdateDailyEventListener(ComponentEventListener<UpdateDailyEntryEvent> listener) {
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
