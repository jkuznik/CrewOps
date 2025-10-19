package pl.crewops.model.dto.dailyEntry;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import pl.crewops.enums.DailyEntryAuditType;

@Builder
public record DailyEntryAuditDTO(
        UUID id,
        UUID dailyEntryId,
        DailyEntryAuditType eventType,
        JsonNode payload,
        String comment,
        Instant createdAt) {}
