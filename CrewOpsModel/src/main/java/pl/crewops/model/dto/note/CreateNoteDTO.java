package pl.crewops.model.dto.note;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;
import pl.crewops.enums.NoteType;

@Builder
public record CreateNoteDTO(
        @NotNull LocalDate date,
        @NotNull NoteType type,
        @NotNull UUID reportedByEmployeeId,
        @NotNull @Size(max = 32767) String content) {}
