package pl.crewops.model.dto.dailyEntry;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import pl.crewops.enums.DailyNoteType;

@Builder
public record DailyNoteDTO(
        UUID id,
        UUID dailyEntryId,
        DailyNoteType type,
        UUID reportedByEmployeeId,
        String content,
        Instant createdAt,
        Instant updatedAt) {}
