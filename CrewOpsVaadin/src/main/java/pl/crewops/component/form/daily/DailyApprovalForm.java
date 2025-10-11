package pl.crewops.component.form.daily;

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
import pl.crewops.enums.DailyEntryStatus;
import pl.crewops.util.AuthenticationResolver;
import pl.crewops.util.SpringContextBridge;
import pl.crewops.view.DailyView;

public class DailyApprovalForm extends FormLayout {

    private final AuthenticationResolver authenticationResolver;

    // todo: i18n
    private final Span headerTextLabel = new Span("Zarządzanie wpisem");
    private final Icon helpIcon = VaadinIcon.INFO_CIRCLE.create();
    private final Span noEntryInformation = new Span();

    // === Przyciski dla wszystkich/pracownika ===
    private final Button saveButton = createActionButton("Zapisz Wpis", VaadinIcon.CLIPBOARD_CHECK);
    private final Button confirmPresenceButton = createActionButton("Potwierdź Obecność", VaadinIcon.CHECK_CIRCLE);
    private final Button requestChangeButton = createActionButton("Zawnioskuj o Zmianę", VaadinIcon.PAPERPLANE);

    // === Przyciski dla Kierownika/Lidera ===
    private final Button approveButton = createActionButton("Zatwierdź Wpis", VaadinIcon.THUMBS_UP);
    private final Button rejectButton = createActionButton("Odrzuć Wpis", VaadinIcon.THUMBS_DOWN);

    // Domyślny stan wpisu (dla pracownika)
    private DailyEntryStatus currentStatus = DailyEntryStatus.EMPTY;

    public DailyApprovalForm() {
        this.authenticationResolver = SpringContextBridge.getBean(AuthenticationResolver.class);

        var verticalLayout = new VerticalLayout();
        verticalLayout.getStyle().set("border", "1px solid #ccc");
        verticalLayout.getStyle().set("border-radius", "4px");
        verticalLayout.getStyle().set("padding", "10px");

        var actionsLayout = new VerticalLayout();
        actionsLayout.setSpacing(true);
        actionsLayout.setPadding(false);

        // Dodanie wszystkich przycisków. Ich widocznością steruje updateState().
        actionsLayout.add(
                noEntryInformation,
                saveButton,
                confirmPresenceButton,
                requestChangeButton,
                approveButton,
                rejectButton);

        var spacer = new Div();
        spacer.setHeight("400px");

        verticalLayout.setMaxHeight("400px");
        verticalLayout.setMaxWidth(DailyView.FORMS_WIDTH);
        verticalLayout.setPadding(false);

        // Dodajemy nową konfigurację nagłówka z ikoną pomocy i Tooltip
        verticalLayout.add(configuredHeader(), actionsLayout, spacer);

        add(verticalLayout);

        // Konfiguracja przycisków Lidera/Kierownika (stałe warianty - przeniesione z konstruktora)
        approveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        rejectButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        // Konfiguracja przycisków Pracownika (stałe warianty - przeniesione z konstruktora)
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        confirmPresenceButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        requestChangeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        updateState(DailyEntryStatus.EMPTY, false);
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

    /**
     * Główna metoda aktualizująca widoczność przycisków na podstawie uprawnień i statusu wpisu.
     * @param status Aktualny status wpisu (EMPTY, AUTO_GENERATED, MANUAL_EDITED, etc.)
     * @param isModified Czy wpis został zmieniony przez użytkownika w formularzu TimesheetEntryForm.
     */
    public void updateState(DailyEntryStatus status, boolean isModified) {
        this.currentStatus = status;

        boolean isShiftLeaderOrManager = authenticationResolver.principalHasShiftLeaderPermission();

        setAllButtonsVisible(false);

        String entryInfoText;
        String tooltipText;

        switch (status) {
            case EMPTY -> {
                entryInfoText = "W dzienniku pracy nie zarejestrowano wpisu dla wybranej daty.";
                tooltipText = "Zaktualizuj informacje aby utworzyć trwały wpis do dziennika.";
            }
            case PENDING -> {
                entryInfoText = "Wpis oczekuje na akceptację.";
                tooltipText =
                        "Zmiany nie są możliwe do czasu podjęcia decyzji przez kierownika. Akceptacja wpisu może nastąpić po zadeklarowanym czasie pracy.";
            }
            case APPROVED -> {
                entryInfoText = "Wpis zaakceptowany.";
                tooltipText =
                        "W razie potrzeb możesz wprowadzić zmiany które będą wymagały potwierdzenia przełożonego.";
            }
            case MANUAL_EDITED -> {
                entryInfoText = "Zaktualizowano wpis.";
                tooltipText = "Wymagana akceptacja przełożonego.";
            }
            case AUTO_GENERATED -> {
                entryInfoText = "Automatycznie wygenerowany wpis na podstawie ustalonego grafiku.";
                tooltipText =
                        "Wymagane jest potwierdzenie obecności pracownika. Zmiana tego wpisu będzie wymagała akceptacji przełożonego.";
            }
            default -> {
                entryInfoText = "Nieznany status wpisu.";
                tooltipText = "Brak dodatkowych informacji.";
            }
        }

        // Ustawienie tekstu informacyjnego i Tooltipa
        noEntryInformation.setText(entryInfoText);
        Tooltip.forComponent(helpIcon).setText(tooltipText);

        if (isShiftLeaderOrManager) {
            updateForShiftLeaderOrManager(status);
        } else {
            updateForRegularEmployee(status, isModified);
        }
    }

    // todo: i18n
    private void updateForRegularEmployee(DailyEntryStatus status, boolean isModified) {

        switch (status) {
            case EMPTY:
                // Może tylko ZAPISAĆ (dodać) nowy wpis
                saveButton.setVisible(true);
                break;

            case AUTO_GENERATED:
                // Może tylko POTWIERDZIĆ OBECNOŚĆ
                confirmPresenceButton.setVisible(true);
                break;

            case APPROVED:
            case PENDING:
                // Brak akcji, tylko podgląd
                break;

            case MANUAL_EDITED:
                // W tym kontekście, jeśli MANUAL_EDITED, to już musi być isModified
                // Jeśli wpis istnieje, ale nie jest edytowany (np. po wczytaniu) - może go tylko podglądać
                break;
        }
    }

    // todo: implement
    private void updateForShiftLeaderOrManager(DailyEntryStatus status) {
        // Logika dla Lidera/Kierownika (na razie tylko uproszczona widoczność przycisków akceptacji)

        switch (status) {
            case PENDING:
                // Tylko jeśli wpis czeka na akceptację, może go Zatwierdzić lub Odrzucić
                approveButton.setVisible(true);
                rejectButton.setVisible(true);
                break;

            case MANUAL_EDITED:
            case AUTO_GENERATED:
            case EMPTY:
                // Może ZAPISAĆ
                saveButton.setVisible(true);
                break;

            case APPROVED:
                // Może tylko podglądać (lub ewentualnie ZAPISAĆ, jeśli zezwolimy na modyfikacje zatwierdzonego wpisu)
                // saveButton.setVisible(true); // Opcjonalnie
                break;
        }
    }

    private void setAllButtonsVisible(boolean visible) {
        saveButton.setVisible(visible);
        confirmPresenceButton.setVisible(visible);
        requestChangeButton.setVisible(visible);
        approveButton.setVisible(visible);
        rejectButton.setVisible(visible);
    }

    // Metoda pomocnicza do tworzenia przycisków
    private Button createActionButton(String text, VaadinIcon iconType) {
        Button button = new Button(text);
        button.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        button.setIcon(new Icon(iconType));
        button.setWidthFull();

        button.getElement().executeJs("this.style.setProperty('justify-content', 'flex-start')");

        return button;
    }

    // Publiczne metody do pobierania przycisków
    public Button getSaveButton() {
        return saveButton;
    }

    public Button getConfirmPresenceButton() {
        return confirmPresenceButton;
    }

    public Button getRequestChangeButton() {
        return requestChangeButton;
    }

    public Button getApproveButton() {
        return approveButton;
    }

    public Button getRejectButton() {
        return rejectButton;
    }
}
