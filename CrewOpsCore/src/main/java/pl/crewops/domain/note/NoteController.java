package pl.crewops.domain.note;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.crewops.enums.ControllerURL;
import pl.crewops.model.dto.note.CreateNoteDTO;
import pl.crewops.model.dto.note.NoteDTO;

@RestController
@RequiredArgsConstructor
@Validated
class NoteController {

    private final NoteAPI noteAPI;

    @PostMapping(ControllerURL.NOTES)
    public ResponseEntity<NoteDTO> createDailyNote(@RequestBody @Valid CreateNoteDTO dailyNoteDTO) {
        return ResponseEntity.ok(noteAPI.createDailyNote(dailyNoteDTO));
    }

    @GetMapping(ControllerURL.NOTES)
    public ResponseEntity<List<NoteDTO>> getAllPublicNotesByDate(@RequestParam(value = "date") LocalDate date) {
        return ResponseEntity.ok(noteAPI.getPublicNotesByDate(date));
    }
}
