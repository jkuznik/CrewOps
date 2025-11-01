package pl.crewops.ui.component.dialog.dailNoteDialog;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.shared.Registration;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import pl.crewops.enums.NoteType;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.dto.dailyEntry.DailyEntryDTO;
import pl.crewops.model.dto.note.CreateNoteDTO;
import pl.crewops.model.dto.note.NoteDTO;
import pl.crewops.ui.component.notification.FailNotification;
import pl.crewops.ui.component.notification.NotAuthenticatedNotification;
import pl.crewops.ui.component.notification.SuccessNotification;
import pl.crewops.util.AuthenticationResolver;
import pl.crewops.util.SpringContextBridge;

public class CreateDailyNoteDialog extends Dialog {

    private final CoreAPI coreAPI;
    private final AuthenticationResolver authenticationResolver;
    private final CreateDailyNoteForm noteForm = new CreateDailyNoteForm();

    private final Span header = new Span();

    private final DailyEntryDTO dailyEntryDTO;
    private final LocalDate date;

    public CreateDailyNoteDialog(DailyEntryDTO dailyEntryDTO, LocalDate date) {
        this.coreAPI = SpringContextBridge.getBean(CoreAPI.class);
        this.authenticationResolver = SpringContextBridge.getBean(AuthenticationResolver.class);
        this.dailyEntryDTO = dailyEntryDTO;
        this.date = date;

        setModal(true);
        setDraggable(true);
        setResizable(true);
        setCloseOnOutsideClick(false);

        setSizeUndefined();

        // todo i18n + formatowanie
        header.setText("CrewOps Note " + date.toString());

        // 1. noteForm zajmuje 100% wysokości Dialogu, aby przekazać ją do wewnętrznego VerticalLayout
        noteForm.setSizeFull();
        noteForm.addCreateNoteListener(event -> {
            UUID id = null;
            if (dailyEntryDTO != null) {
                id = dailyEntryDTO.id();
            }

            NoteType type = event.isPrivateNote() ? NoteType.PRIVATE : NoteType.PUBLIC;

            var createDailyNote = CreateNoteDTO.builder()
                    .date(date)
                    .content(event.getContent())
                    .reportedByEmployeeId(authenticationResolver.getPrincipal().getEmployeeId())
                    .type(type)
                    .build();

            try {
                Optional<NoteDTO> dailyNote = coreAPI.createNote(createDailyNote);
                if (dailyNote.isPresent()) {
                    NoteDTO noteDTO = dailyNote.get();
                    // todo i18n
                    new SuccessNotification("Notatka utworzona pomyślnie");
                } else {
                    new FailNotification("failNotification");
                }
            } catch (NotAuthenticatedException e) {
                new NotAuthenticatedNotification(e.getMessage()).open();
            }
            close();
        });

        // 2. Dodajemy tylko główny formularz do Dialogu.
        // Dialog wewnętrznie używa układu, który zajmuje pełną wysokość,
        // a my zmuszamy formularz do jej zajęcia.
        add(header, noteForm);

        // 3. Przycisk zamknięcia przenosimy do stopki Dialogu.
        var closeButton = new Button(getTranslation("qualificationManagerDialog.closeButton"), event -> close());
        closeButton.addClickShortcut(Key.ESCAPE);

        getFooter().add(closeButton);

        open();
    }

    public abstract static class CreateDailyNoteDialogEvent extends ComponentEvent<CreateDailyNoteDialog> {
        public CreateDailyNoteDialogEvent(CreateDailyNoteDialog source) {
            super(source, false);
        }
    }

    public static class AddNoteEvent extends CreateDailyNoteDialogEvent {
        public AddNoteEvent(CreateDailyNoteDialog source) {
            super(source);
        }
    }

    public Registration addCreateNoteListener(ComponentEventListener<CreateDailyNoteForm.AddNoteEvent> listener) {
        return addListener(CreateDailyNoteForm.AddNoteEvent.class, listener);
    }
}
