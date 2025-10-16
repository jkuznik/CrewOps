package pl.crewops.model.dto.dailyEntry;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import pl.crewops.enums.DailyEntryAuditType;

@Builder
public record DailyEntryAuditDTO(
        UUID id, UUID dailyEntryId, DailyEntryAuditType eventType, String details, Instant createdAt) {}
