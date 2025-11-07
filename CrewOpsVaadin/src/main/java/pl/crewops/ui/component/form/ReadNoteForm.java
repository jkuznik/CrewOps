package pl.crewops.ui.component.form;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import java.util.Optional;
import java.util.UUID;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.NoteFormModel;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.ui.component.notification.FailNotification;
import pl.crewops.util.AuthenticationResolver;
import pl.crewops.util.SpringContextBridge;

public class ReadNoteForm extends VerticalLayout {

    private final AuthenticationResolver authenticationResolver;
    private final CoreAPI coreAPI;

    private final TextArea noteContent = new TextArea();
    private final TextField author = new TextField();

    private final Button showAuthor = new Button();
    private final Button close = new Button();

    private NoteFormModel noteFormModel;

    public ReadNoteForm() {
        this.authenticationResolver = SpringContextBridge.getBean(AuthenticationResolver.class);
        this.coreAPI = SpringContextBridge.getBean(CoreAPI.class);

        setSizeFull();

        setSpacing(true);
        setPadding(true);

        localize();

        configureButtons();
        configureForm();

        add(noteContent, author, showAuthor, close);
    }

    public void setNoteFormModel(NoteFormModel noteFormModel) {
        this.noteFormModel = noteFormModel;
        updateForm();
    }

    private void configureButtons() {
        showAuthor.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        showAuthor.setWidthFull();
        showAuthor.addClickListener(event -> {
            if (noteFormModel != null) {
                try {
                    Optional<EmployeeDTO> employeeById =
                            coreAPI.getEmployeeById(UUID.fromString(noteFormModel.getReportedByEmployeeId()));
                    employeeById.ifPresent(employeeDTO -> {
                        author.setValue(employeeDTO.firstName() + " " + employeeDTO.lastName());
                    });
                } catch (NotAuthenticatedException e) {
                    new FailNotification(e.getMessage());
                }
            }
        });

        close.setWidthFull();
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.addClickListener(event -> {
            noteFormModel = null;
            this.setVisible(false);
        });
    }

    private void configureForm() {
        noteContent.setWidthFull();
        noteContent.setMinRows(4);
        noteContent.setEnabled(false);
        author.setWidthFull();
        author.setEnabled(false);

        if (!authenticationResolver.principalHasManagerPermission()) {
            author.setVisible(false);
            showAuthor.setVisible(false);
        }
    }

    private void updateForm() {
        if (noteFormModel != null) {
            noteContent.setValue(noteFormModel.getContent());
        } else {
            noteContent.setValue("");
        }
        author.setValue("");
    }

    private void localize() {
        noteContent.setLabel(getTranslation("readNotesForm.noteContent"));
        author.setLabel(getTranslation("readNotesForm.author"));
        showAuthor.setText(getTranslation("readNotesForm.showAuthor"));
        close.setText(getTranslation("readNotesForm.close"));
    }
}
