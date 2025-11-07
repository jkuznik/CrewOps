package pl.crewops.domain.note;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pl.crewops.domain.note.DailyNoteTestFactory.createDailyNoteDTO;
import static pl.crewops.domain.note.DailyNoteTestFactory.dailyEntry;
import static pl.crewops.domain.note.DailyNoteTestFactory.dailyNote;
import static pl.crewops.domain.note.DailyNoteTestFactory.dailyNoteDTO;
import static pl.crewops.domain.note.DailyNoteTestFactory.employee;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.crewops.domain.dailyEntry.DailyEntryAPI;
import pl.crewops.domain.employee.EmployeeAPI;
import pl.crewops.model.dto.note.CreateNoteDTO;
import pl.crewops.model.dto.note.NoteDTO;
import pl.crewops.model.tenantSchema.DailyEntry;
import pl.crewops.model.tenantSchema.Employee;
import pl.crewops.model.tenantSchema.Note;

@SpringJUnitConfig(classes = {NoteService.class})
class NoteServiceTest {

    @Autowired
    NoteService dailyNoteService;

    // Mocki
    @MockitoBean
    NoteRepository noteRepository;

    @MockitoBean
    NoteMapper noteMapper;

    @MockitoBean
    DailyEntryAPI dailyEntryAPI;

    @MockitoBean
    EmployeeAPI employeeAPI;

    // Stałe ID
    private final UUID DAILY_ENTRY_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final UUID EMPLOYEE_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private final UUID DAILY_NOTE_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");

    // Obiekty testowe z fabryki
    private CreateNoteDTO createDtoWithEntry;
    private CreateNoteDTO createDtoWithoutEntry;
    private DailyEntry dailyEntry;
    private Employee employee;
    private Note noteEntity;
    private Note noteEntityWithoutEntry;
    private NoteDTO dailyNoteResultDTO;

    @BeforeEach
    void setUp() {
        // Inicjalizacja obiektów za pomocą DailyNoteTestFactory
        dailyEntry = dailyEntry(DAILY_ENTRY_ID);
        employee = employee(EMPLOYEE_ID);

        createDtoWithEntry = createDailyNoteDTO(DAILY_ENTRY_ID, EMPLOYEE_ID);
        createDtoWithoutEntry = createDailyNoteDTO(null, EMPLOYEE_ID);

        // Encje DailyNote (różne dla przypadków)
        noteEntity = dailyNote(DAILY_NOTE_ID, dailyEntry, employee);
        noteEntityWithoutEntry = dailyNote(DAILY_NOTE_ID, null, employee);

        dailyNoteResultDTO = dailyNoteDTO(DAILY_NOTE_ID, DAILY_ENTRY_ID, EMPLOYEE_ID);
    }

    @Test
    void createDailyNote_ShouldReturnDailyNoteDTO_whenDailyEntryIdIsPresent() {
        // Given
        // 1. Ustawienie zachowania API
        when(employeeAPI.getEmployeeById(EMPLOYEE_ID)).thenReturn(employee);

        // 2. Mockowanie mapowania (używamy eq(null) dla mapowania, bo nie interesuje nas tymczasowa encja)
        when(noteMapper.toEntity(eq(createDtoWithEntry), eq(employee))).thenReturn(noteEntity);

        // 3. Mockowanie repozytorium
        when(noteRepository.save(eq(noteEntity))).thenReturn(noteEntity);

        // 4. Mockowanie mapowania wynikowego DTO
        when(noteMapper.toDTO(eq(noteEntity))).thenReturn(dailyNoteResultDTO);

        // When
        NoteDTO result = dailyNoteService.createDailyNote(createDtoWithEntry);

        // Then
        assertThat(result).isNotNull();

        // Weryfikacja interakcji
        verify(employeeAPI, times(1)).getEmployeeById(EMPLOYEE_ID);
        verify(noteRepository, times(1)).save(noteEntity);
        // Weryfikacja, czy mapper został wywołany z poprawnymi, resolvowanymi encjami
        verify(noteMapper, times(1)).toEntity(createDtoWithEntry, employee);
        verify(noteMapper, times(1)).toDTO(noteEntity);
    }

    @Test
    void createDailyNote_ShouldReturnDailyNoteDTO_whenDailyEntryIdIsNull() {
        // Given
        // Ustawienie DTO wynikowego na null DailyEntryId dla asercji
        NoteDTO expectedDtoWithoutEntry = dailyNoteDTO(DAILY_NOTE_ID, null, EMPLOYEE_ID);

        // 1. Ustawienie zachowania API (DailyEntryAPI NIE jest wywoływany)
        when(employeeAPI.getEmployeeById(EMPLOYEE_ID)).thenReturn(employee);

        // 2. Mockowanie mapowania z DailyEntry = null
        when(noteMapper.toEntity(eq(createDtoWithoutEntry), eq(employee))).thenReturn(noteEntityWithoutEntry);

        // 3. Mockowanie repozytorium
        when(noteRepository.save(eq(noteEntityWithoutEntry))).thenReturn(noteEntityWithoutEntry);

        // 4. Mockowanie mapowania wynikowego DTO
        when(noteMapper.toDTO(eq(noteEntityWithoutEntry))).thenReturn(expectedDtoWithoutEntry);

        // When
        NoteDTO result = dailyNoteService.createDailyNote(createDtoWithoutEntry);

        // Then
        assertThat(result).isNotNull();

        // Weryfikacja interakcji
        verify(dailyEntryAPI, never()).getById(any()); // Sprawdzenie, że nie wywołano
        verify(employeeAPI, times(1)).getEmployeeById(EMPLOYEE_ID);
        verify(noteRepository, times(1)).save(noteEntityWithoutEntry);
        // Weryfikacja, czy mapper został wywołany z 'null' dla DailyEntry
        verify(noteMapper, times(1)).toEntity(eq(createDtoWithoutEntry), eq(employee));
        verify(noteMapper, times(1)).toDTO(noteEntityWithoutEntry);
    }
}
