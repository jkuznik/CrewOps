package pl.crewops.component.form.daily;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.time.LocalDate;
import pl.crewops.enums.DateState;
import pl.crewops.util.AuthenticationResolver;
import pl.crewops.view.DailyView;

public class DailyActivityForm extends FormLayout {

    private final AuthenticationResolver authenticationResolver;

    // todo: i18n
    private final Span headerTextLabel = new Span("Panel aktywności");

    // todo: this feature allow shift leaders, managers and admins to monitor current attendance of employees and
    //  approve each employee attendance
    private final Button checkSubordinates = createActionButton("Obecność Pracowników", VaadinIcon.USERS);

    // todo: this feature need dedicated db table job_report related to daily_entry
    private final Button jobRaport = createActionButton("Raport Stanowiskowy", VaadinIcon.CLIPBOARD_TEXT);

    // todo: this feature need dedicated db table like shift_note related many to one with daily_entry,
    //  each emploee can add as many notes to single daily_entry as needed
    private final Button addNote = createActionButton("Dodaj notatke", VaadinIcon.NOTEBOOK);

    // todo: this feature need deicated db table like safety_report
    private final Button safetyRaport = createActionButton("Zgłoś Uwagę BHP", VaadinIcon.WARNING);

    private final Button requestLeave = createActionButton("Zgłoś Wniosek Urlopowy", VaadinIcon.CALENDAR_CLOCK);

    public DailyActivityForm(AuthenticationResolver authenticationResolver) {

        this.authenticationResolver = authenticationResolver;

        var actionsButtons = configuredButtons(authenticationResolver);

        var spacer = new Div();
        spacer.setHeight("200px");

        var mainContainer = configuredMainContainer();

        mainContainer.add(configuredHeader(), actionsButtons, spacer);

        add(mainContainer);
    }

    private VerticalLayout configuredButtons(AuthenticationResolver authenticationResolver) {
        var actionsLayout = new VerticalLayout();
        actionsLayout.setSpacing(true);
        actionsLayout.setPadding(false);

        if (authenticationResolver.principalHasShiftLeaderPermission()) {
            actionsLayout.add(checkSubordinates);
        }

        actionsLayout.add(jobRaport, addNote, safetyRaport, requestLeave);
        return actionsLayout;
    }

    private static VerticalLayout configuredMainContainer() {
        var mainContainer = new VerticalLayout();
        mainContainer.getStyle().set("border", "1px solid #ccc");
        mainContainer.getStyle().set("border-radius", "4px");
        mainContainer.setMaxHeight("400px");
        mainContainer.setMaxWidth(DailyView.FORMS_WIDTH);

        return mainContainer;
    }

    private Component configuredHeader() {
        headerTextLabel.getStyle().set("font-weight", "bold");
        headerTextLabel.getStyle().set("font-size", "1.1em");

        return headerTextLabel;
    }

    private Button createActionButton(String text, VaadinIcon iconType) {
        Button button = new Button(text);
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        button.setIcon(new Icon(iconType));
        button.setWidthFull();

        button.getElement().executeJs("this.style.setProperty('justify-content', 'flex-start')");

        return button;
    }

    // todo: jeśli potwierdzono obecność to wniosek o urlop dla "dzisiaj" nie powinien być widoczny
    public void updateDependsOnDate(LocalDate localDate) {
        DateState state = DateState.fromLocalDate(localDate);

        switch (state) {
            case PAST -> {
                checkSubordinates.setVisible(true);
                jobRaport.setVisible(true);
                addNote.setVisible(true);
                safetyRaport.setVisible(false);
                requestLeave.setVisible(false);
            }
            case TODAY -> {
                checkSubordinates.setVisible(true);
                jobRaport.setVisible(true);
                addNote.setVisible(true);
                safetyRaport.setVisible(true);
                requestLeave.setVisible(true);
            }
            case FUTURE -> {
                checkSubordinates.setVisible(false);
                jobRaport.setVisible(false);
                addNote.setVisible(true);
                safetyRaport.setVisible(false);
                requestLeave.setVisible(true);
            }
        }
    }
}
