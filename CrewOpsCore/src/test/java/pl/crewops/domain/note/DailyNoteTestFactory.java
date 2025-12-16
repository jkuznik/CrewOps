package pl.crewops.domain.note;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import pl.crewops.enums.NoteType;
import pl.crewops.model.dto.note.CreateNoteDTO;
import pl.crewops.model.dto.note.NoteDTO;
import pl.crewops.model.tenantSchema.DailyEntry;
import pl.crewops.model.tenantSchema.Employee;
import pl.crewops.model.tenantSchema.Note;

class DailyNoteTestFactory {

    static final LocalDate TEST_NOTE_DATE = LocalDate.parse("2025-01-15");

    public static Note dailyNote(UUID id) {
        Note note =
                Note.builder().type(NoteType.PRIVATE).content("Test content").build();
        note.setId(id);
        note.setCreatedAt(Instant.now());
        note.setUpdatedAt(Instant.now());
        return note;
    }

    public static Note publicDailyNote(UUID id) {
        Note note =
                Note.builder().type(NoteType.PRIVATE).content("Test content").build();
        note.setId(id);
        note.setCreatedAt(Instant.now());
        note.setUpdatedAt(Instant.now());
        return note;
    }

    public static Note privateDailyNote(UUID id, DailyEntry dailyEntry, Employee reportedBy) {
        Note note = Note.builder()
                .type(NoteType.PRIVATE)
                .content("Test content")
                .reportedByEmployeeId(reportedBy)
                .build();
        note.setId(id);
        note.setCreatedAt(Instant.now());
        note.setUpdatedAt(Instant.now());
        return note;
    }

    public static DailyEntry dailyEntry(UUID id) {
        DailyEntry dailyEntry = new DailyEntry();
        dailyEntry.setId(id);
        return dailyEntry;
    }

    public static Employee employee(UUID id) {
        Employee employee = new Employee();
        employee.setId(id);
        return employee;
    }

    public static CreateNoteDTO createDailyNoteDTO(UUID dailyEntryId, UUID reportedByEmployeeId) {
        return CreateNoteDTO.builder()
                .reportedByEmployeeId(reportedByEmployeeId)
                .type(NoteType.PRIVATE)
                .content("Test content for creation")
                .build();
    }

    public static NoteDTO dailyNoteDTO(UUID id, UUID dailyEntryId, UUID reportedByEmployeeId) {
        return NoteDTO.builder()
                .id(id)
                .reportedByEmployeeId(reportedByEmployeeId)
                .type(NoteType.PRIVATE)
                .content("Test content for creation")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
}
