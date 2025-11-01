package pl.crewops.ui.component.form.daily;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Setter;
import pl.crewops.enums.DateState;
import pl.crewops.model.dto.dailyEntry.DailyEntryDTO;
import pl.crewops.ui.component.custom.PanelCustom;
import pl.crewops.util.AuthenticationResolver;

public class DailyActivityForm extends PanelCustom {

    private final AuthenticationResolver authenticationResolver;

    private final Button checkSubordinates = new Button();
    private final Button jobRaport = new Button();
    private final Button addNote = new Button();
    private final Button safetyRaport = new Button();
    private final Button requestLeave = new Button();

    @Setter
    private DailyEntryDTO dailyEntry = null;

    public DailyActivityForm(AuthenticationResolver authenticationResolver) {
        this.authenticationResolver = authenticationResolver;

        localize();

        var actionsButtons = configuredButtons();

        var formContainer = configuredMainContainer();

        formContainer.add(actionsButtons, spacer());

        setContent(formContainer);
    }

    private static Div spacer() {
        var spacer = new Div();
        spacer.setHeight("400px");
        return spacer;
    }

    private VerticalLayout configuredButtons() {
        var actionsLayout = new VerticalLayout();
        actionsLayout.setSpacing(true);
        actionsLayout.setPadding(false);

        checkSubordinates.setIcon(new Icon(VaadinIcon.USERS));
        jobRaport.setIcon(new Icon(VaadinIcon.CLIPBOARD_TEXT));

        addNote.setIcon(new Icon(VaadinIcon.NOTEBOOK));
        addNote.addClickListener(event -> {
            fireEvent(new AddNoteEvent(this));
        });

        safetyRaport.setIcon(new Icon(VaadinIcon.WARNING));
        requestLeave.setIcon(new Icon(VaadinIcon.CALENDAR_CLOCK));

        applyButtonStyles(checkSubordinates);
        applyButtonStyles(jobRaport);
        applyButtonStyles(addNote);
        applyButtonStyles(safetyRaport);
        applyButtonStyles(requestLeave);

        if (authenticationResolver.principalHasShiftLeaderPermission()) {
            actionsLayout.add(checkSubordinates);
        }

        actionsLayout.add(jobRaport, addNote, safetyRaport, requestLeave);
        return actionsLayout;
    }

    @Override
    protected void onAttach(com.vaadin.flow.component.AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        applyButtonStyles(checkSubordinates);
        applyButtonStyles(jobRaport);
        applyButtonStyles(addNote);
        applyButtonStyles(safetyRaport);
        applyButtonStyles(requestLeave);
    }

    private void localize() {
        setSummary(VaadinIcon.LINES_LIST, getTranslation("dailyActivityForm.headerTextLabel"));

        checkSubordinates.setText(getTranslation("dailyActivityForm.checkSubordinates"));
        jobRaport.setText(getTranslation("dailyActivityForm.jobRaport"));
        addNote.setText(getTranslation("dailyActivityForm.addNote"));
        safetyRaport.setText(getTranslation("dailyActivityForm.safetyRaport"));
        requestLeave.setText(getTranslation("dailyActivityForm.requestLeave"));
    }

    private static VerticalLayout configuredMainContainer() {
        var mainContainer = new VerticalLayout();
        mainContainer.getStyle().set("padding", "10px");
        return mainContainer;
    }

    private void applyButtonStyles(Button button) {
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        button.setWidthFull();

        button.getElement().executeJs("this.style.setProperty('justify-content', 'flex-start')");
    }

    public void updateDependsOnSelectedDate(LocalDate localDate) {
        DateState state = DateState.fromLocalDate(localDate);

        setAllButtonsVisible(false);
        switch (state) {
            case PAST -> {
                if (authenticationResolver.principalHasManagerPermission()) {
                    checkSubordinates.setVisible(true);
                }

                addNote.setVisible(true);
            }

            case TODAY -> {
                if (authenticationResolver.principalHasShiftLeaderPermission()) {
                    checkSubordinates.setVisible(true);
                }
                if (dailyEntry != null) {
                    jobRaport.setVisible(true);
                    ifDailyEntryExistButShiftNotStartYetThenAllowRequestLeave();
                } else {
                    requestLeave.setVisible(true);
                }
                addNote.setVisible(true);
                safetyRaport.setVisible(true);
            }
            case FUTURE -> {
                addNote.setVisible(true);
                requestLeave.setVisible(true);
            }
        }
    }

    private void ifDailyEntryExistButShiftNotStartYetThenAllowRequestLeave() {
        if (dailyEntry.startTime().isAfter(Instant.now())) {
            requestLeave.setVisible(true);
        }
    }

    private void setAllButtonsVisible(boolean visible) {
        checkSubordinates.setVisible(visible);
        jobRaport.setVisible(visible);
        addNote.setVisible(visible);
        safetyRaport.setVisible(visible);
        requestLeave.setVisible(visible);
    }

    public abstract static class DailyActivityFormEvents extends ComponentEvent<DailyActivityForm> {
        public DailyActivityFormEvents(DailyActivityForm source) {
            super(source, false);
        }
    }

    public static class AddNoteEvent extends DailyActivityFormEvents {
        public AddNoteEvent(DailyActivityForm source) {
            super(source);
        }
    }

    public Registration addCreateNoteListener(ComponentEventListener<AddNoteEvent> listener) {
        return addListener(AddNoteEvent.class, listener);
    }
}
