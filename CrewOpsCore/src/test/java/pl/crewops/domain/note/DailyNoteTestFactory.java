package pl.crewops.domain.note;

import java.time.Instant;
import java.util.UUID;
import pl.crewops.enums.NoteType;
import pl.crewops.model.dto.note.CreateNoteDTO;
import pl.crewops.model.dto.note.NoteDTO;
import pl.crewops.model.tenantSchema.DailyEntry;
import pl.crewops.model.tenantSchema.Employee;
import pl.crewops.model.tenantSchema.Note;

public class DailyNoteTestFactory {

    // --- ENCJIE ---

    public static Note dailyNote(UUID id) {
        Note note =
                Note.builder().type(NoteType.PRIVATE).content("Test content").build();
        note.setId(id);
        note.setCreatedAt(Instant.now());
        note.setUpdatedAt(Instant.now());
        return note;
    }

    public static Note dailyNote(UUID id, DailyEntry dailyEntry, Employee reportedBy) {
        Note note = Note.builder()
                .type(NoteType.PRIVATE)
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

    public static CreateNoteDTO createDailyNoteDTO(UUID dailyEntryId, UUID reportedByEmployeeId) {
        return CreateNoteDTO.builder()
                .dailyEntryId(dailyEntryId)
                .reportedByEmployeeId(reportedByEmployeeId)
                .type(NoteType.PRIVATE)
                .content("Test content for creation")
                .build();
    }

    // --- DTO WYJŚCIOWE ---

    public static NoteDTO dailyNoteDTO(UUID id, UUID dailyEntryId, UUID reportedByEmployeeId) {
        return NoteDTO.builder()
                .id(id)
                .dailyEntryId(dailyEntryId)
                .reportedByEmployeeId(reportedByEmployeeId)
                .type(NoteType.PRIVATE)
                .content("Test content for creation")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
}
