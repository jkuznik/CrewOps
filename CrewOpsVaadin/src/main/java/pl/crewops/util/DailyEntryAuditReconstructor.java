package pl.crewops.util;

import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import pl.crewops.enums.DailyAttendanceStatus;
import pl.crewops.enums.DailyEntryStatus;
import pl.crewops.model.dto.dailyEntry.DailyEntryAuditDTO;
import pl.crewops.model.dto.dailyEntry.DailyEntryDTO;

/**
 * Odtwarza stan DailyEntry (startTime, endTime, statusy) w momencie wyst\u0105pienia danego audytu.
 * U\u017Cywa DailyEntryDTO jako kontenera stanu.
 */
public class DailyEntryAuditReconstructor {

    /**
     * Odtwarza stan DailyEntry na podstawie danego wiersza audytu.
     * @param targetAuditDTO Wiersz audytu, dla kt\u00f3rego chcemy pozna\u0107 stan po zmianie.
     * @param allAuditsHistory Pe\u0142na historia audyt\u00f3w dla danego wpisu.
     * @return Stan wpisu dziennego po wykonaniu targetAuditDTO w postaci DailyEntryDTO.
     */
    public DailyEntryDTO reconstructState(
            DailyEntryAuditDTO targetAuditDTO, List<DailyEntryAuditDTO> allAuditsHistory) {

        // 1. Sortuj histori\u0119 chronologicznie (od najstarszego do najnowszego)
        List<DailyEntryAuditDTO> eventsToApply = allAuditsHistory.stream()
                .sorted(Comparator.comparing(DailyEntryAuditDTO::createdAt))
                .filter(audit -> !audit.createdAt().isAfter(targetAuditDTO.createdAt()))
                .toList();

        // 2. Inicjalizacja stanu bazowego (u\u017Cywamy budowniczego DailyEntryDTO)
        DailyEntryDTO.DailyEntryDTOBuilder stateBuilder = DailyEntryDTO.builder()
                .id(UUID.randomUUID()) // Wymagane pole, mo\u017Ce by\u0107 placeholder
                // POPRAWIONE: U\u017Cycie ZoneId.systemDefault() do konwersji Instant na LocalDate
                .entryDate(targetAuditDTO
                        .createdAt()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate())
                .overTime(BigDecimal.ZERO);

        // 3. Iteruj i aplikuj zmiany
        for (DailyEntryAuditDTO audit : eventsToApply) {
            applyChanges(stateBuilder, audit.payload());
        }

        return stateBuilder.build();
    }

    private void applyChanges(DailyEntryDTO.DailyEntryDTOBuilder stateBuilder, JsonNode payload) {
        if (payload == null || !payload.has("newValues")) {
            return;
        }

        JsonNode newValues = payload.get("newValues");

        // Aplikuj zmiany z newValues
        if (newValues.has("startTime")) {
            stateBuilder.startTime(parseInstant(newValues.get("startTime")));
        }
        if (newValues.has("endTime")) {
            stateBuilder.endTime(parseInstant(newValues.get("endTime")));
        }
        if (newValues.has("overtime")) {
            stateBuilder.overTime(parseBigDecimal(newValues.get("overtime")));
        }
        if (newValues.has("attendance")) {
            stateBuilder.attendance(parseAttendanceStatus(newValues.get("attendance")));
        }
        if (newValues.has("status")) {
            stateBuilder.status(parseEntryStatus(newValues.get("status")));
        }
    }

    // --- Metody pomocnicze ---

    private Instant parseInstant(JsonNode node) {
        if (node.isTextual()) {
            try {
                return Instant.parse(node.asText());
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private BigDecimal parseBigDecimal(JsonNode node) {
        if (node.isNumber()) {
            return node.decimalValue();
        }
        if (node.isTextual()) {
            try {
                return new BigDecimal(node.asText());
            } catch (NumberFormatException e) {
                return BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    private DailyAttendanceStatus parseAttendanceStatus(JsonNode node) {
        if (node.isTextual()) {
            try {
                return DailyAttendanceStatus.valueOf(node.asText().toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

    private DailyEntryStatus parseEntryStatus(JsonNode node) {
        if (node.isTextual()) {
            try {
                return DailyEntryStatus.valueOf(node.asText().toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }
}
