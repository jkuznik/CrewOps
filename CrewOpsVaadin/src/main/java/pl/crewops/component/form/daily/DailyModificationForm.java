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
import com.vaadin.flow.component.orderedlayout.FlexComponent; // NOWY IMPORT
import com.vaadin.flow.component.orderedlayout.HorizontalLayout; // NOWY IMPORT
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip; // NOWY IMPORT
import com.vaadin.flow.shared.Registration;
import lombok.Setter;
import pl.crewops.enums.DailyEntryStatus;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.util.AuthenticationResolver;
import pl.crewops.view.DailyView;

public class DailyModificationForm extends FormLayout {

    private final AuthenticationResolver authenticationResolver;
    private final CoreAPI coreAPI;

    // todo: i18n
    private final Span headerTextLabel = new Span("Zarządzanie wpisem");
    private final Icon helpIcon = VaadinIcon.INFO_CIRCLE.create();
    private final Span entryStatusInformation = new Span();

    // === Przyciski dla wszystkich/pracownika ===
    private final Button addButton = new Button("dodaj wpis");
    private final Button confirmPresenceButton = new Button("potwierdz");
    private final Button updateButton = new Button("zapisz zmiany");
    private final Button changeAttendanceButton = new Button("zmien status obecności");

    // === Przyciski dla Kierownika/Lidera ===
    private final Button approveButton = new Button("potwierdz");

    private DailyEntryStatus currentStatus = DailyEntryStatus.EMPTY;

    @Setter
    private boolean isAttendanceSelected = false;

    public DailyModificationForm(CoreAPI coreAPI, AuthenticationResolver authenticationResolver) {
        this.authenticationResolver = authenticationResolver;
        this.coreAPI = coreAPI;

        var mainContainer = new VerticalLayout();
        mainContainer.getStyle().set("border", "1px solid #ccc");
        mainContainer.getStyle().set("border-radius", "4px");
        mainContainer.getStyle().set("padding", "10px");
        mainContainer.setMaxHeight(DailyView.FORMS_HEIGHT);
        mainContainer.setMaxWidth(DailyView.FORMS_WIDTH);

        var buttonsContainer = new VerticalLayout();
        buttonsContainer.setSpacing(true);
        buttonsContainer.setPadding(false);

        buttonsContainer.add(configuredButtons());

        var spacer = new Div();
        spacer.setHeight("400px");

        mainContainer.add(configuredHeader(), entryStatusInformation, buttonsContainer, spacer);

        add(mainContainer);

        updateState();
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

        // Tooltip jest inicjalizowany tutaj, ale jego treść będzie aktualizowana w updateState
        Tooltip.forComponent(helpIcon)
                .withText("") // Początkowo pusta treść
                .withPosition(Tooltip.TooltipPosition.BOTTOM_END);

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

    /**
     * Główna metoda aktualizująca widoczność przycisków na podstawie uprawnień i statusu wpisu.
     */
    public void updateState() {

        boolean userIsShiftLeader = authenticationResolver.principalHasShiftLeaderPermission();
        boolean userIsManager = authenticationResolver.principalHasManagerPermission();

        String entryInfoText;
        String tooltipText;

        setAllButtonsVisible(false);

        switch (currentStatus) {
            case EMPTY -> {
                entryInfoText = "W dzienniku pracy nie zarejestrowano wpisu dla wybranej daty.";
                tooltipText = "Zaktualizuj informacje aby utworzyć trwały wpis do dziennika.";
                addButton.setVisible(true);
            }
            case DRAFT -> {
                entryInfoText = "Wpis w trakcie wypełniania.";
                tooltipText = "Dowolne modyfikacje nie wygenerują powiadomień do osób nadzorujących dziennikiem pracy.";
                updateButton.setVisible(true);

                if (isAttendanceSelected) {
                    changeAttendanceButton.setVisible(true);
                } else {
                    confirmPresenceButton.setVisible(true);
                }
            }
            case PENDING -> {
                entryInfoText = "Wpis oczekuje na akceptację.";
                tooltipText =
                        "Zmiany nie są możliwe do czasu podjęcia decyzji przez kierownika. Akceptacja wpisu może nastąpić po zadeklarowanym czasie pracy.";

                updateButton.setVisible(true);

                if (userIsShiftLeader) {
                    approveButton.setVisible(true);
                    changeAttendanceButton.setVisible(true);
                }
            }
            case APPROVED -> {
                entryInfoText = "Wpis zaakceptowany.";
                tooltipText =
                        "W razie potrzeb możesz wprowadzić zmiany które będą wymagały potwierdzenia przełożonego.";

                updateButton.setVisible(true);

                if (userIsManager) {
                    changeAttendanceButton.setVisible(true);
                }
            }
            case MANUAL_EDITED -> {
                entryInfoText = "Zaktualizowano wpis.";
                tooltipText = "Wymagana akceptacja przełożonego.";

                updateButton.setVisible(true);

                if (userIsShiftLeader) {
                    approveButton.setVisible(true);
                    changeAttendanceButton.setVisible(true);
                }
            }
            case AUTO_GENERATED -> {
                entryInfoText = "Automatycznie wygenerowany wpis na podstawie ustalonego grafiku.";
                tooltipText =
                        "Wymagane jest potwierdzenie obecności pracownika. Zmiana tego wpisu będzie wymagała akceptacji przełożonego.";

                confirmPresenceButton.setVisible(true);
                updateButton.setVisible(true);

                if (userIsShiftLeader) {
                    changeAttendanceButton.setVisible(true);
                    approveButton.setVisible(true);
                }
            }
            default -> {
                entryInfoText = "Nieznany status wpisu.";
                tooltipText = "Brak dodatkowych informacji.";
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
