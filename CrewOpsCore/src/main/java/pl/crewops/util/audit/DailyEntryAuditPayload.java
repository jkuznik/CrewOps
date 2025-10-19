package pl.crewops.util.audit;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
import lombok.Builder;
import pl.crewops.enums.DailyEntryAuditType;

/**
 * Structured representation of audit data stored as JSON payload.
 * Includes metadata (actor, timestamp), old and new field values.
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DailyEntryAuditPayload(

        /** Payload structure version (supports future evolution). */
        int version,

        /** Type of operation (e.g., WORK_TIME_MODIFIED, ATTENDANCE_STATUS_CHANGED). */
        DailyEntryAuditType operationType,

        /** Metadata about who performed the action and when. */
        Map<String, Object> metadata,

        /** Field values BEFORE the change. */
        Map<String, Object> oldValues,

        /** Field values AFTER the change. */
        Map<String, Object> newValues) {}
