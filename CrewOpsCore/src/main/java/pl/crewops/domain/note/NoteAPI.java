package pl.crewops.domain.note;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import pl.crewops.model.dto.note.CreateNoteDTO;
import pl.crewops.model.dto.note.FetchNotesRequest;
import pl.crewops.model.dto.note.NoteDTO;

@Validated
public interface NoteAPI {

    NoteDTO createDailyNote(@NotNull @Valid CreateNoteDTO createNoteDTO);

    List<NoteDTO> getAllPublicAndUserPrivateNotesByDate(@NotNull FetchNotesRequest fetchNotesRequest);
}
