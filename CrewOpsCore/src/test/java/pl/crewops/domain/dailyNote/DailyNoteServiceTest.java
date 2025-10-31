package pl.crewops.domain.dailyNote;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pl.crewops.domain.dailyNote.DailyNoteTestFactory.createDailyNoteDTO;
import static pl.crewops.domain.dailyNote.DailyNoteTestFactory.dailyEntry;
import static pl.crewops.domain.dailyNote.DailyNoteTestFactory.dailyNote;
import static pl.crewops.domain.dailyNote.DailyNoteTestFactory.dailyNoteDTO;
import static pl.crewops.domain.dailyNote.DailyNoteTestFactory.employee;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.crewops.domain.dailyEntry.DailyEntryAPI;
import pl.crewops.domain.employee.EmployeeAPI;
import pl.crewops.model.dto.dailyNote.CreateDailyNoteDTO;
import pl.crewops.model.dto.dailyNote.DailyNoteDTO;
import pl.crewops.model.tenantSchema.DailyEntry;
import pl.crewops.model.tenantSchema.DailyNote;
import pl.crewops.model.tenantSchema.Employee;

@SpringJUnitConfig(classes = {DailyNoteService.class})
class DailyNoteServiceTest {

    @Autowired
    DailyNoteService dailyNoteService;

    // Mocki
    @MockitoBean
    DailyNoteRepository dailyNoteRepository;

    @MockitoBean
    DailyNoteMapper dailyNoteMapper;

    @MockitoBean
    DailyEntryAPI dailyEntryAPI;

    @MockitoBean
    EmployeeAPI employeeAPI;

    // Stałe ID
    private final UUID DAILY_ENTRY_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final UUID EMPLOYEE_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private final UUID DAILY_NOTE_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");

    // Obiekty testowe z fabryki
    private CreateDailyNoteDTO createDtoWithEntry;
    private CreateDailyNoteDTO createDtoWithoutEntry;
    private DailyEntry dailyEntry;
    private Employee employee;
    private DailyNote dailyNoteEntity;
    private DailyNote dailyNoteEntityWithoutEntry;
    private DailyNoteDTO dailyNoteResultDTO;

    @BeforeEach
    void setUp() {
        // Inicjalizacja obiektów za pomocą DailyNoteTestFactory
        dailyEntry = dailyEntry(DAILY_ENTRY_ID);
        employee = employee(EMPLOYEE_ID);

        createDtoWithEntry = createDailyNoteDTO(DAILY_ENTRY_ID, EMPLOYEE_ID);
        createDtoWithoutEntry = createDailyNoteDTO(null, EMPLOYEE_ID);

        // Encje DailyNote (różne dla przypadków)
        dailyNoteEntity = dailyNote(DAILY_NOTE_ID, dailyEntry, employee);
        dailyNoteEntityWithoutEntry = dailyNote(DAILY_NOTE_ID, null, employee);

        dailyNoteResultDTO = dailyNoteDTO(DAILY_NOTE_ID, DAILY_ENTRY_ID, EMPLOYEE_ID);
    }

    @Test
    void createDailyNote_ShouldReturnDailyNoteDTO_whenDailyEntryIdIsPresent() {
        // Given
        // 1. Ustawienie zachowania API
        when(dailyEntryAPI.getById(DAILY_ENTRY_ID)).thenReturn(dailyEntry);
        when(employeeAPI.getEmployeeById(EMPLOYEE_ID)).thenReturn(employee);

        // 2. Mockowanie mapowania (używamy eq(null) dla mapowania, bo nie interesuje nas tymczasowa encja)
        when(dailyNoteMapper.toEntity(eq(createDtoWithEntry), eq(dailyEntry), eq(employee)))
                .thenReturn(dailyNoteEntity);

        // 3. Mockowanie repozytorium
        when(dailyNoteRepository.save(eq(dailyNoteEntity))).thenReturn(dailyNoteEntity);

        // 4. Mockowanie mapowania wynikowego DTO
        when(dailyNoteMapper.toDTO(eq(dailyNoteEntity))).thenReturn(dailyNoteResultDTO);

        // When
        DailyNoteDTO result = dailyNoteService.createDailyNote(createDtoWithEntry);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.dailyEntryId()).isEqualTo(DAILY_ENTRY_ID);

        // Weryfikacja interakcji
        verify(dailyEntryAPI, times(1)).getById(DAILY_ENTRY_ID);
        verify(employeeAPI, times(1)).getEmployeeById(EMPLOYEE_ID);
        verify(dailyNoteRepository, times(1)).save(dailyNoteEntity);
        // Weryfikacja, czy mapper został wywołany z poprawnymi, resolvowanymi encjami
        verify(dailyNoteMapper, times(1)).toEntity(createDtoWithEntry, dailyEntry, employee);
        verify(dailyNoteMapper, times(1)).toDTO(dailyNoteEntity);
    }

    @Test
    void createDailyNote_ShouldReturnDailyNoteDTO_whenDailyEntryIdIsNull() {
        // Given
        // Ustawienie DTO wynikowego na null DailyEntryId dla asercji
        DailyNoteDTO expectedDtoWithoutEntry = dailyNoteDTO(DAILY_NOTE_ID, null, EMPLOYEE_ID);

        // 1. Ustawienie zachowania API (DailyEntryAPI NIE jest wywoływany)
        when(employeeAPI.getEmployeeById(EMPLOYEE_ID)).thenReturn(employee);

        // 2. Mockowanie mapowania z DailyEntry = null
        when(dailyNoteMapper.toEntity(eq(createDtoWithoutEntry), isNull(), eq(employee)))
                .thenReturn(dailyNoteEntityWithoutEntry);

        // 3. Mockowanie repozytorium
        when(dailyNoteRepository.save(eq(dailyNoteEntityWithoutEntry))).thenReturn(dailyNoteEntityWithoutEntry);

        // 4. Mockowanie mapowania wynikowego DTO
        when(dailyNoteMapper.toDTO(eq(dailyNoteEntityWithoutEntry))).thenReturn(expectedDtoWithoutEntry);

        // When
        DailyNoteDTO result = dailyNoteService.createDailyNote(createDtoWithoutEntry);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.dailyEntryId()).isNull();

        // Weryfikacja interakcji
        verify(dailyEntryAPI, never()).getById(any()); // Sprawdzenie, że nie wywołano
        verify(employeeAPI, times(1)).getEmployeeById(EMPLOYEE_ID);
        verify(dailyNoteRepository, times(1)).save(dailyNoteEntityWithoutEntry);
        // Weryfikacja, czy mapper został wywołany z 'null' dla DailyEntry
        verify(dailyNoteMapper, times(1)).toEntity(eq(createDtoWithoutEntry), isNull(), eq(employee));
        verify(dailyNoteMapper, times(1)).toDTO(dailyNoteEntityWithoutEntry);
    }
}
