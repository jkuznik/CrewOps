package pl.crewops.model.dto.dailyNote;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Builder;
import pl.crewops.enums.DailyNoteType;

@Builder
public record CreateDailyNoteDTO(
        UUID dailyEntryId,
        @NotNull DailyNoteType type,
        @NotNull UUID reportedByEmployeeId,
        @NotNull @Size(max = 32767) String content) {}
