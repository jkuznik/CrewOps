package pl.crewops.ui.component.dialog.dailNoteDialog;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import pl.crewops.model.dto.dailyEntry.DailyEntryDTO;

public class CreateDailyNoteDialog extends Dialog {

    private final CreateDailyNoteForm noteForm = new CreateDailyNoteForm();

    private final DailyEntryDTO dailyEntryDTO;

    public CreateDailyNoteDialog(DailyEntryDTO dailyEntryDTO) {
        this.dailyEntryDTO = dailyEntryDTO;

        setModal(true);
        setDraggable(true);
        setResizable(true);
        setCloseOnOutsideClick(false);

        setWidth("45vw");
        setHeight("55vh");

        // 1. noteForm zajmuje 100% wysokości Dialogu, aby przekazać ją do wewnętrznego VerticalLayout
        noteForm.setSizeFull();

        // 2. Dodajemy tylko główny formularz do Dialogu.
        // Dialog wewnętrznie używa układu, który zajmuje pełną wysokość,
        // a my zmuszamy formularz do jej zajęcia.
        add(noteForm);

        // 3. Przycisk zamknięcia przenosimy do stopki Dialogu.
        var closeButton = new Button(getTranslation("qualificationManagerDialog.closeButton"), event -> close());
        closeButton.addClickShortcut(Key.ESCAPE);

        getFooter().add(closeButton);

        open();
    }
}
