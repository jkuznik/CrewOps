package pl.crewops.domain.note;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.IntegrationTest;
import pl.crewops.enums.NoteType;
import pl.crewops.model.dto.note.CreateNoteDTO;
import pl.crewops.model.dto.note.FetchNotesRequest;
import pl.crewops.model.dto.note.NoteDTO;
import pl.crewops.model.tenantSchema.Employee;
import pl.crewops.model.tenantSchema.Note;

@Transactional
class NoteAPITest extends IntegrationTest {

    @Autowired
    private NoteRepository noteRepository;

    private final UUID employeeId = UUID.fromString("10101010-1010-1010-1010-101010101010");
    private final UUID employeeId2 = UUID.fromString("20202020-2020-2020-2020-202020202020");

    private UUID userAId;
    private Employee userA;
    private Employee userB;
    private LocalDate testDate;

    @BeforeEach
    void setUpIntegrationTestEnvironment() {
        userA = employeeAPI.getEmployeeById(employeeId);
        userB = employeeAPI.getEmployeeById(employeeId2);

        userAId = userA.getId();
        testDate = LocalDate.now();

        // Czasem wymagane, aby wyczyścić dane z @initTest, jeśli się nadpisują
        // noteRepository.deleteAll();
    }

    @Test
    void initTest() {
        List<Note> all = noteRepository.findAll();

        assertThat(all.size()).isEqualTo(2);
    }

    @Test
    void createDailyNote_shouldPersistNoteAndReturnDTO() {
        // given
        CreateNoteDTO createNoteDTO = new CreateNoteDTO(LocalDate.now(), NoteType.PRIVATE, employeeId, "Private note");

        // when
        NoteDTO result = noteAPI.createDailyNote(createNoteDTO);

        // then
        assertNotNull(result.id());
        assertEquals("Private note", result.content());
        assertEquals(NoteType.PRIVATE, result.type());
        noteRepository.flush();

        Note savedNote = noteRepository.findById(result.id()).orElseThrow();
        assertThat(savedNote.getCreatedAt()).isNotNull();
        assertEquals(employeeId, savedNote.getReportedByEmployeeId().getId());
    }

    @Test
    void getAllPublicAndUserPrivateNotesByDate_shouldFilterCombineAndSortCorrectly() {
        // given
        CreateNoteDTO dto1 = CreateNoteDTO.builder()
                .content("Public Note 1 (Latest)")
                .type(NoteType.PUBLIC)
                .date(testDate)
                .reportedByEmployeeId(userB.getId())
                .build();
        NoteDTO note1 = noteAPI.createDailyNote(dto1);

        CreateNoteDTO dto2 = CreateNoteDTO.builder()
                .content("Private Note 2 (Oldest)")
                .type(NoteType.PRIVATE)
                .date(testDate)
                .reportedByEmployeeId(userAId)
                .build();
        NoteDTO note2 = noteAPI.createDailyNote(dto2);

        CreateNoteDTO dto3 = CreateNoteDTO.builder()
                .content("Private Note 3 (Ignore)")
                .type(NoteType.PRIVATE)
                .date(testDate)
                .reportedByEmployeeId(userB.getId())
                .build();
        noteAPI.createDailyNote(dto3);

        CreateNoteDTO dto4 = CreateNoteDTO.builder()
                .content("Shared Note 4 (Duplicated)")
                .type(NoteType.PUBLIC)
                .date(testDate)
                .reportedByEmployeeId(userAId)
                .build();
        NoteDTO note4 = noteAPI.createDailyNote(dto4);

        FetchNotesRequest request = new FetchNotesRequest(userAId, testDate);

        entityManager.flush();

        // when
        List<NoteDTO> result = noteAPI.getAllPublicAndUserPrivateNotesByDate(request);

        // then
        assertThat(result).hasSize(3);
        assertThat(result)
                .extracting(NoteDTO::content)
                .contains("Public Note 1 (Latest)", "Private Note 2 (Oldest)", "Shared Note 4 (Duplicated)");

        assertThat(result).noneMatch(note -> note.content().contains("Private Note 3 (Ignore)"));
    }
}
