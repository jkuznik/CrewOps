package pl.crewops.domain.dailyNote;

import java.time.Instant;
import java.util.UUID;
import pl.crewops.enums.DailyNoteType;
import pl.crewops.model.dto.dailyNote.CreateDailyNoteDTO;
import pl.crewops.model.dto.dailyNote.DailyNoteDTO;
import pl.crewops.model.tenantSchema.DailyEntry;
import pl.crewops.model.tenantSchema.DailyNote;
import pl.crewops.model.tenantSchema.Employee;

public class DailyNoteTestFactory {

    // --- ENCJIE ---

    public static DailyNote dailyNote(UUID id) {
        DailyNote note = DailyNote.builder()
                .type(DailyNoteType.PRIVATE)
                .content("Test content")
                .build();
        note.setId(id);
        note.setCreatedAt(Instant.now());
        note.setUpdatedAt(Instant.now());
        return note;
    }

    public static DailyNote dailyNote(UUID id, DailyEntry dailyEntry, Employee reportedBy) {
        DailyNote note = DailyNote.builder()
                .type(DailyNoteType.PRIVATE)
                .content("Test content")
                .dailyEntry(dailyEntry)
                .reportedByEmployeeId(reportedBy)
                .build();
        note.setId(id);
        note.setCreatedAt(Instant.now());
        note.setUpdatedAt(Instant.now());
        return note;
    }

    public static DailyEntry dailyEntry(UUID id) {
        // Zakładamy, że DailyEntry ma setter dla ID
        DailyEntry dailyEntry = new DailyEntry();
        dailyEntry.setId(id);
        return dailyEntry;
    }

    public static Employee employee(UUID id) {
        // Zakładamy, że Employee ma setter dla ID
        Employee employee = new Employee();
        employee.setId(id);
        return employee;
    }

    // --- DTO WEJŚCIOWE ---

    public static CreateDailyNoteDTO createDailyNoteDTO(UUID dailyEntryId, UUID reportedByEmployeeId) {
        return CreateDailyNoteDTO.builder()
                .dailyEntryId(dailyEntryId)
                .reportedByEmployeeId(reportedByEmployeeId)
                .type(DailyNoteType.PRIVATE)
                .content("Test content for creation")
                .build();
    }

    // --- DTO WYJŚCIOWE ---

    public static DailyNoteDTO dailyNoteDTO(UUID id, UUID dailyEntryId, UUID reportedByEmployeeId) {
        return DailyNoteDTO.builder()
                .id(id)
                .dailyEntryId(dailyEntryId)
                .reportedByEmployeeId(reportedByEmployeeId)
                .type(DailyNoteType.PRIVATE)
                .content("Test content for creation")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
}
