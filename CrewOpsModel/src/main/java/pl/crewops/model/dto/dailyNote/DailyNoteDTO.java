package pl.crewops.model.dto.dailyNote;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import pl.crewops.enums.DailyNoteType;

@Builder
public record DailyNoteDTO(
        UUID id,
        UUID dailyEntryId,
        UUID reportedByEmployeeId,
        DailyNoteType type,
        String content,
        Instant createdAt,
        Instant updatedAt)
        implements Serializable {}
