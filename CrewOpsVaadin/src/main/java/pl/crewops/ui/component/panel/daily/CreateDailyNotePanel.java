package pl.crewops.ui.component.panel.daily;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.shared.Registration;
import java.time.LocalDate;
import java.util.Optional;
import lombok.Setter;
import pl.crewops.enums.NoteType;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.dto.note.CreateNoteDTO;
import pl.crewops.model.dto.note.NoteDTO;
import pl.crewops.ui.component.custom.PanelCustom;
import pl.crewops.ui.component.notification.FailNotification;
import pl.crewops.ui.component.notification.NotAuthenticatedNotification;
import pl.crewops.ui.component.notification.SuccessNotification;
import pl.crewops.util.AuthenticationResolver;
import pl.crewops.util.SpringContextBridge;

public class CreateDailyNotePanel extends PanelCustom {

    private final CoreAPI coreAPI;
    private final AuthenticationResolver authenticationResolver;

    private final TextArea noteTextArea = new TextArea();
    private final RadioButtonGroup<Boolean> noteType = new RadioButtonGroup<>();
    private final Button addButton = new Button();
    private final Button closeButton = new Button();

    @Setter
    private LocalDate date = LocalDate.now();

    public CreateDailyNotePanel() {
        this.coreAPI = SpringContextBridge.getBean(CoreAPI.class);
        this.authenticationResolver = SpringContextBridge.getBean(AuthenticationResolver.class);

        // todo i18n + formatowanie
        setSummary(VaadinIcon.NOTEBOOK, "CrewOps Note ");

        localize();

        noteTextArea.setSizeFull();
        noteTextArea.setMaxRows(4);

        var configuredCheckboxAndButtonContainer = configuredCheckboxAndButtonContainer();

        addContent(noteTextArea, configuredCheckboxAndButtonContainer);
    }

    private VerticalLayout configuredCheckboxAndButtonContainer() {
        var configuredCheckboxAndButtonContainer = new VerticalLayout();
        configuredCheckboxAndButtonContainer.setSpacing(true);
        configuredCheckboxAndButtonContainer.setPadding(true);

        configuredCheckboxAndButtonContainer.add(noteType, addButton, closeButton);

        noteType.setItems(true, false);
        noteType.setItemLabelGenerator(booleanItem -> {
            if (booleanItem) {
                return getTranslation("dailyActivityForm.noteTypePrivate");
            } else {
                return getTranslation("dailyActivityForm.noteTypePublic");
            }
        });
        noteType.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);

        noteType.addValueChangeListener(event -> {
            addButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            addButton.setEnabled(true);
        });

        addButton.setEnabled(false);
        addButton.setWidthFull();
        addButton.addClickListener(event -> {
            NoteType type = noteType.getValue() ? NoteType.PRIVATE : NoteType.PUBLIC;

            var createDailyNote = CreateNoteDTO.builder()
                    .date(date)
                    .content(noteTextArea.getValue())
                    .reportedByEmployeeId(authenticationResolver.getPrincipal().getEmployeeId())
                    .type(type)
                    .build();

            try {
                Optional<NoteDTO> dailyNote = coreAPI.createNote(createDailyNote);
                if (dailyNote.isPresent()) {
                    NoteDTO noteDTO = dailyNote.get();
                    // todo i18n
                    new SuccessNotification("Notatka utworzona pomyślnie" + noteDTO.date());
                } else {
                    new FailNotification("failNotification");
                }
                fireEvent(new CreateNoteEvent(this));
            } catch (NotAuthenticatedException e) {
                new NotAuthenticatedNotification(e.getMessage()).open();
            }
        });

        closeButton.setWidthFull();
        closeButton.addClickListener(event -> fireEvent(new CloseEvent(this)));
        closeButton.addClickShortcut(Key.ESCAPE);

        return configuredCheckboxAndButtonContainer;
    }

    private void localize() {
        noteType.setLabel(getTranslation("dailyActivityForm.noteType"));
        addButton.setText(getTranslation("dailyActivityForm.addNote"));
        closeButton.setText(getTranslation("qualificationManagerDialog.closeButton"));
    }

    public abstract static class CreateDailyNoteDialogEvent extends ComponentEvent<CreateDailyNotePanel> {
        public CreateDailyNoteDialogEvent(CreateDailyNotePanel source) {
            super(source, false);
        }
    }

    public static class CreateNoteEvent extends CreateDailyNoteDialogEvent {
        public CreateNoteEvent(CreateDailyNotePanel source) {
            super(source);
        }
    }

    public static class CloseEvent extends CreateDailyNoteDialogEvent {
        public CloseEvent(CreateDailyNotePanel source) {
            super(source);
        }
    }

    public Registration addCreateNoteListener(ComponentEventListener<CreateNoteEvent> listener) {
        return addListener(CreateNoteEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }
}
