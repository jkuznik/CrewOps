package pl.crewops.model.dto.dailyEntry;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import pl.crewops.enums.DailyAttendanceStatus;
import pl.crewops.enums.DailyEntryStatus;

/**
 * Base interface for all update commands related to {@code DailyEntry}.
 * <p>
 * Each record represents a distinct type of modification that can be applied to a DailyEntry
 * (e.g., updating attendance, work time, overtime, or adding a note).
 * <p>
 * Implementations are designed for strong type safety and to facilitate
 * handling different update scenarios in a clean and extensible way.
 */
public sealed interface UpdateDailyEntryCommand
        permits UpdateDailyEntryCommand.UpdateAttendance,
                UpdateDailyEntryCommand.UpdateWorkTime,
                UpdateDailyEntryCommand.UpdateOvertime,
                UpdateDailyEntryCommand.ChangeEntryStatus,
                UpdateDailyEntryCommand.AddDailyNote {

    /**
     * Unique identifier of the employee whose DailyEntry is being modified.
     */
    UUID employeeId();

    /**
     * Date of the DailyEntry record to be updated.
     */
    LocalDate entryDate();

    /**
     * Identifier of the employee or manager who performed the update.
     * <p>
     * Used for audit purposes to record who triggered the modification.
     */
    UUID actionByEmployeeId();

    /**
     * Represents a command to update the attendance status of a DailyEntry.
     */
    String comment();

    record UpdateAttendance(
            @NotNull UUID employeeId,
            @NotNull LocalDate entryDate,
            @NotNull UUID actionByEmployeeId,
            @NotNull DailyAttendanceStatus newAttendance,
            String comment)
            implements UpdateDailyEntryCommand {}

    /**
     * Represents a command to update the work time (start and/or end time)
     * of a DailyEntry.
     */
    record UpdateWorkTime(
            @NotNull UUID employeeId,
            @NotNull LocalDate entryDate,
            @NotNull UUID actionByEmployeeId,
            Instant newStartTime,
            Instant newEndTime,
            String comment)
            implements UpdateDailyEntryCommand {}

    /**
     * Represents a command to modify the overtime value of a DailyEntry.
     */
    record UpdateOvertime(
            @NotNull UUID employeeId,
            @NotNull LocalDate entryDate,
            @NotNull UUID actionByEmployeeId,
            @NotNull BigDecimal newOvertime,
            String comment)
            implements UpdateDailyEntryCommand {}

    /**
     * Represents a command to change the overall entry status of a DailyEntry.
     */
    record ChangeEntryStatus(
            @NotNull UUID employeeId,
            @NotNull LocalDate entryDate,
            @NotNull UUID actionByEmployeeId,
            @NotNull DailyEntryStatus newStatus,
            String comment)
            implements UpdateDailyEntryCommand {}

    /**
     * Represents a command to add a new daily note to a DailyEntry.
     */
    record AddDailyNote(
            @NotNull UUID employeeId,
            @NotNull LocalDate entryDate,
            @NotNull UUID actionByEmployeeId,
            @NotBlank String noteContent,
            String comment)
            implements UpdateDailyEntryCommand {}
}
