package pl.crewops.domain.dailyNote;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;
import pl.crewops.model.dto.dailyNote.CreateDailyNoteDTO;
import pl.crewops.model.dto.dailyNote.DailyNoteDTO;

@Validated
public interface DailyNoteAPI {

    DailyNoteDTO createDailyNote(@NotNull @Valid CreateDailyNoteDTO createDailyNoteDTO);

    List<DailyNoteDTO> getNotesByDailyEntryId(@NotNull UUID dailyEntryId);
}
