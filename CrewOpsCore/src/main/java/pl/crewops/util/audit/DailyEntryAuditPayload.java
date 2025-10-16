package pl.crewops.util.audit;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
import lombok.Builder;
import pl.crewops.enums.DailyEntryAuditType;

/**
 * Reprezentacja danych audytowych zapisywana jako JSON (payload).
 * Zawiera wersję struktury, typ operacji oraz poprzednie i nowe wartości zmienionych pól.
 *
 * Dzięki polom oldValues i newValues można łatwo odtworzyć historię zmian
 * lub zaprezentować użytkownikowi różnice w rekordzie.
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DailyEntryAuditPayload(

        /** Wersja struktury payloadu — umożliwia ewolucję formatu w przyszłości. */
        int version,

        /** Typ operacji (np. WORK_TIME_MODIFIED, STATUS_CHANGED, ATTENDANCE_UPDATED). */
        DailyEntryAuditType operationType,

        /** Wartości pól PRZED zmianą (np. startTime: 08:00). */
        Map<String, Object> oldValues,

        /** Wartości pól PO zmianie (np. startTime: 09:00). */
        Map<String, Object> newValues) {}
