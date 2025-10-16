package pl.crewops.util.audit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.crewops.enums.DailyEntryAuditType;
import pl.crewops.model.tenantSchema.DailyEntry;

@Component
@RequiredArgsConstructor
public class AuditDetailsBuilder {

    private static final int PAYLOAD_VERSION = 1;

    private final ObjectMapper objectMapper;

    /**
     * Tworzy payload audytowy dla danej zmiany w DailyEntry.
     * Logika zależy od typu zdarzenia (eventType).
     */
    public JsonNode createPayload(DailyEntryAuditType eventType, DailyEntry oldEntry, DailyEntry newEntry) {

        Map<String, Object> oldValues = new HashMap<>();
        Map<String, Object> newValues = new HashMap<>();

        switch (eventType) {
            case ATTENDANCE_STATUS_CHANGED -> {
                if (oldEntry == null || !Objects.equals(oldEntry.getAttendance(), newEntry.getAttendance())) {
                    oldValues.put("attendance", oldEntry != null ? oldEntry.getAttendance() : null);
                    newValues.put("attendance", newEntry.getAttendance());
                }
            }

            case WORK_TIME_MODIFIED -> {
                if (oldEntry == null || !Objects.equals(oldEntry.getStartTime(), newEntry.getStartTime())) {
                    oldValues.put("startTime", oldEntry != null ? oldEntry.getStartTime() : null);
                    newValues.put("startTime", newEntry.getStartTime());
                }
                if (oldEntry == null || !Objects.equals(oldEntry.getEndTime(), newEntry.getEndTime())) {
                    oldValues.put("endTime", oldEntry != null ? oldEntry.getEndTime() : null);
                    newValues.put("endTime", newEntry.getEndTime());
                }
            }

            case OVERTIME_MODIFIED -> {
                if (oldEntry == null || !Objects.equals(oldEntry.getOvertime(), newEntry.getOvertime())) {
                    oldValues.put("overtime", oldEntry != null ? oldEntry.getOvertime() : null);
                    newValues.put("overtime", newEntry.getOvertime());
                }
            }

            case ENTRY_STATUS_CHANGED -> {
                if (oldEntry == null || !Objects.equals(oldEntry.getStatus(), newEntry.getStatus())) {
                    oldValues.put("status", oldEntry != null ? oldEntry.getStatus() : null);
                    newValues.put("status", newEntry.getStatus());
                }
            }

            case DAILY_NOTE_ADDED, SAFETY_NOTE_ADDED -> {
                // zakładamy, że notatka jest dodawana osobno, więc zapisujemy nową notatkę
                newValues.put("noteAdded", newEntry.getDailyNotes());
            }

            case REPORT_STARTED, REPORT_COMPLETED -> {
                // możesz dodać logikę dla raportów, np. zapis statusu raportu
                oldValues.put("reportStatus", oldEntry != null ? oldEntry.getStatus() : null);
                newValues.put("reportStatus", newEntry.getStatus());
            }

            default -> throw new IllegalArgumentException("Nieobsługiwany typ audytu: " + eventType);
        }

        Map<String, Object> payload = Map.of(
                "version", PAYLOAD_VERSION,
                "operationType", eventType,
                "oldValues", oldValues,
                "newValues", newValues);

        return objectMapper.valueToTree(payload);
    }
}
