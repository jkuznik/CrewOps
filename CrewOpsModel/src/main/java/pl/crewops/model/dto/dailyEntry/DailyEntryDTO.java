package pl.crewops.model.dto.dailyEntry;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import pl.crewops.enums.DailyAttendanceStatus;
import pl.crewops.enums.DailyEntryStatus;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;

@Builder
public record DailyEntryDTO(
        UUID id,
        UUID employeeId,
        LocalDate entryDate,
        Instant startTime,
        Instant endTime,
        BigDecimal overTime,
        JobPositionDTO jobPosition,
        Set<DailyNoteDTO> dailyNotes,
        Set<DailyEntryAuditDTO> auditEvents,
        DailyAttendanceStatus attendance,
        DailyEntryStatus status)
        implements Serializable {

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DailyEntryDTO that)) return false;
        return Objects.equals(id(), that.id());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id());
    }
}
