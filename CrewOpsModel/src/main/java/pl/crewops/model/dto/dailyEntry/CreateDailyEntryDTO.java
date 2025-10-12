package pl.crewops.model.dto.dailyEntry;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;
import pl.crewops.enums.DailyAttendanceStatus;
import pl.crewops.enums.DailyEntryStatus;

@Builder
public record CreateDailyEntryDTO(
        @NotNull UUID employeeId,
        @NotNull LocalDate entryDate,
        UUID actionByEmployeeId,
        Instant startTime,
        Instant endTime,
        BigDecimal overTime,
        DailyAttendanceStatus attendance,
        DailyEntryStatus status) {}
