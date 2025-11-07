package pl.crewops.domain.note;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.crewops.enums.ControllerURL;
import pl.crewops.model.dto.note.CreateNoteDTO;
import pl.crewops.model.dto.note.FetchNotesRequest;
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
    public ResponseEntity<List<NoteDTO>> getAllPublicAndPrincipalPrivateNotesByDate(
            @RequestParam("employeeId") UUID employeeId, @RequestParam("date") LocalDate date) {
        var fetchNotesRequest =
                FetchNotesRequest.builder().employeeId(employeeId).date(date).build();
        return ResponseEntity.ok(noteAPI.getPublicAndPrincipalPrivateNotesByDate(fetchNotesRequest));
    }
}
