package pl.crewops.model.dto.dailyEntry;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import pl.crewops.enums.DailyAttendanceStatus;
import pl.crewops.enums.DailyEntryStatus;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;

/**
 * Base interface for all update commands related to {@code DailyEntry}.
 * Each record represents a distinct type of modification.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type" // <-- klucz JSON, który wskaże typ komendy
        )
@JsonSubTypes({
    @JsonSubTypes.Type(value = UpdateDailyEntryCommand.UpdateAttendance.class, name = "UpdateAttendance"),
    @JsonSubTypes.Type(value = UpdateDailyEntryCommand.UpdateDailyEntryInformation.class, name = "UpdateWorkTime"),
    @JsonSubTypes.Type(value = UpdateDailyEntryCommand.ChangeEntryStatus.class, name = "ChangeEntryStatus"),
    @JsonSubTypes.Type(value = UpdateDailyEntryCommand.AddDailyNote.class, name = "AddDailyNote")
})
public sealed interface UpdateDailyEntryCommand
        permits UpdateDailyEntryCommand.UpdateAttendance,
                UpdateDailyEntryCommand.UpdateDailyEntryInformation,
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

    String comment();

    /**
     * Represents a command to update the work time (start and/or end time)
     * of a DailyEntry.
     */
    record UpdateDailyEntryInformation(
            @NotNull UUID employeeId,
            @NotNull LocalDate entryDate,
            @NotNull UUID actionByEmployeeId,
            Instant newStartTime,
            Instant newEndTime,
            BigDecimal newOvertime,
            JobPositionDTO jobPositionDTO,
            String comment)
            implements UpdateDailyEntryCommand {}

    /**
     * Represents a command to update the attendance status of a DailyEntry.
     */
    record UpdateAttendance(
            @NotNull UUID employeeId,
            @NotNull LocalDate entryDate,
            @NotNull UUID actionByEmployeeId,
            @NotNull DailyAttendanceStatus newAttendance,
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
