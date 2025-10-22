package pl.crewops.util.audit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.crewops.enums.DailyEntryAuditType;
import pl.crewops.model.tenantSchema.DailyEntry;
import pl.crewops.model.tenantSchema.JobPosition;

/**
 * Utility responsible for building structured JSON payloads for DailyEntry audit events.
 * <p>
 * Captures both old and new field values, along with operation metadata and the actor (who performed the change).
 */
@Component
@RequiredArgsConstructor
public class AuditDetailsBuilder {

    private static final int PAYLOAD_VERSION = 1;

    private final ObjectMapper objectMapper;

    /**
     * Creates an audit payload for a specific DailyEntry change.
     *
     * @param eventType           type of audit event (e.g., WORK_TIME_MODIFIED)
     * @param oldEntry            previous state of the entry (nullable)
     * @param newEntry            updated state of the entry
     * @param actionByEmployeeId  employee who performed the action
     * @return JSON payload with before/after values and metadata
     */
    public JsonNode createPayload(
            DailyEntryAuditType eventType, DailyEntry oldEntry, DailyEntry newEntry, UUID actionByEmployeeId) {

        Map<String, Object> oldValues = new HashMap<>();
        Map<String, Object> newValues = new HashMap<>();

        switch (eventType) {
            case ATTENDANCE_STATUS_CHANGED -> {
                if (oldEntry == null || !Objects.equals(oldEntry.getAttendance(), newEntry.getAttendance())) {
                    oldValues.put("attendance", oldEntry != null ? oldEntry.getAttendance() : null);
                    newValues.put("attendance", newEntry.getAttendance());
                }
            }
            case INFORMATION_MODIFIED -> {
                if (oldEntry == null || !Objects.equals(oldEntry.getStartTime(), newEntry.getStartTime())) {
                    oldValues.put("startTime", oldEntry != null ? oldEntry.getStartTime() : null);
                    newValues.put("startTime", newEntry.getStartTime());
                }
                if (oldEntry == null || !Objects.equals(oldEntry.getEndTime(), newEntry.getEndTime())) {
                    oldValues.put("endTime", oldEntry != null ? oldEntry.getEndTime() : null);
                    newValues.put("endTime", newEntry.getEndTime());
                }

                // 3. Sprawdzenie Overtime (POPRAWIONA LOGIKA DLA BIGDECIMAL)
                BigDecimal oldOvertime = oldEntry != null ? oldEntry.getOvertime() : null;
                BigDecimal newOvertime = newEntry.getOvertime();
                boolean overtimeChanged = false;

                // Sprawdzenie, czy któraś z wartości jest null (audyt inicjalny)
                if (oldOvertime == null || newOvertime == null) {
                    // Jest to zmiana, chyba że oba są null
                    if (!Objects.equals(oldOvertime, newOvertime)) {
                        overtimeChanged = true;
                    }
                }
                // Obie wartości nie są null, porównujemy ich wartości liczbowe (compareTo)
                else if (oldOvertime.compareTo(newOvertime) != 0) {
                    overtimeChanged = true;
                }
                // Jeśli 0 vs 0 lub 2 vs 2, to compareTo zwróci 0, changed będzie false. POPRAWNE.

                if (overtimeChanged) {
                    oldValues.put("overtime", oldOvertime);
                    newValues.put("overtime", newOvertime);
                }

                // 4. Sprawdzenie JobPosition (POPRAWIONA LOGIKA DLA RELACJI JPA)
                JobPosition oldJobPosition = oldEntry != null ? oldEntry.getJobPosition() : null;
                JobPosition newJobPosition = newEntry.getJobPosition();

                // Sprawdzamy, czy obiekty JobPosition się różnią.
                // ZAKŁADAMY, że oldJobPosition jest poprawnie załadowane (nie jest niezainicjalizowanym proxy).
                boolean jobPositionChanged = !Objects.equals(oldJobPosition, newJobPosition);

                if (jobPositionChanged) {
                    String oldEntryValue = null;
                    if (oldJobPosition != null) {
                        // Tworzenie wartości dla starego wpisu
                        oldEntryValue = oldJobPosition.getMachine() != null
                                ? oldJobPosition.getName() + " ("
                                        + oldJobPosition.getMachine().getRegisterNumber() + ")"
                                : oldJobPosition.getName();
                    }

                    // Tworzenie wartości dla nowego wpisu (newJobPosition nie powinien być null, jeśli jest zmiana)
                    String newEntryValue = null;
                    if (newJobPosition != null) {
                        newEntryValue = newJobPosition.getMachine() != null
                                ? newJobPosition.getName() + " ("
                                        + newJobPosition.getMachine().getRegisterNumber() + ")"
                                : newJobPosition.getName();
                    }

                    oldValues.put("jobPosition", oldEntryValue);
                    newValues.put("jobPosition", newEntryValue);
                }
            }
            case ENTRY_STATUS_CHANGED -> {
                if (oldEntry == null || !Objects.equals(oldEntry.getStatus(), newEntry.getStatus())) {
                    oldValues.put("status", oldEntry != null ? oldEntry.getStatus() : null);
                    newValues.put("status", newEntry.getStatus());
                }
            }
            case DAILY_NOTE_ADDED, SAFETY_NOTE_ADDED -> {
                newValues.put("noteAdded", newEntry.getDailyNotes());
            }
            case REPORT_STATUS_CHANGED -> {
                oldValues.put("reportStatus", oldEntry != null ? oldEntry.getStatus() : null);
                newValues.put("reportStatus", newEntry.getStatus());
            }
            default -> throw new IllegalArgumentException("Unsupported audit type: " + eventType);
        }

        Map<String, Object> metadata =
                Map.of("performedBy", actionByEmployeeId, "timestamp", System.currentTimeMillis());

        Map<String, Object> payload = Map.of(
                "version", PAYLOAD_VERSION,
                "operationType", eventType,
                "metadata", metadata,
                "oldValues", oldValues,
                "newValues", newValues);

        return objectMapper.valueToTree(payload);
    }
}
