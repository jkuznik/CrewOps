package pl.crewops.domain.shift;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.crewops.domain.jobPosition.JobPositionMapperStruct;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;
import pl.crewops.model.dto.shift.CreateShiftDTO;
import pl.crewops.model.dto.shift.ShiftDTO;
import pl.crewops.model.tenantSchema.Shift;

// Uwaga: Usun\u0105\u0142em JobPositionAPI z konfiguracji, poniewa\u017C nie jest u\u017Cywane w nowej logice DTO.
@SpringJUnitConfig(
        classes = {ShiftService.class, ShiftRepository.class, ShiftMapper.class, JobPositionMapperStruct.class})
class ShiftServiceTest {

    @Autowired
    ShiftService shiftService;

    @MockitoBean
    ShiftRepository shiftRepository;

    @MockitoBean
    ShiftMapper shiftMapper;

    @MockitoBean
    JobPositionMapperStruct jobPositionMapperStruct;

    private final UUID expectedId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        // Mockowanie zapisu: symuluje zwr\u00f3cenie encji z nadanym ID po zapisie
        when(shiftRepository.save(any(Shift.class))).thenAnswer(invocation -> {
            Shift entry = invocation.getArgument(0);
            entry.setId(expectedId);
            return entry;
        });
    }

    @Test
    void shouldCreateShift_andSave_withJobPositionDetails() {
        // GIVEN

        // 1. Definicja stanowisk w DTO
        Set<JobPositionDTO> jobPositionDtos = Set.of(
                JobPositionDTO.builder().id(UUID.randomUUID()).name("Operator").build(),
                JobPositionDTO.builder()
                        .id(UUID.randomUUID())
                        .name("Lider Zmiany")
                        .build());

        // 2. DTO wej\u015Bciowe
        CreateShiftDTO inputDto = CreateShiftDTO.builder()
                .name("Popo\u0142udniowa Zmiana")
                .jobPositions(jobPositionDtos)
                .build();

        // 3. Encje (MapStruct przejmie konwersj\u0119 JobPositionDTO -> JobPosition)
        // W tym przypadku mockujemy ten krok
        Shift unsavedShift = Shift.builder().name(inputDto.name()).build(); // Puste JobPositions
        Shift savedShift = Shift.builder().name(inputDto.name()).build();
        savedShift.setId(expectedId); // Puste JobPositions

        // 4. DTO wynikowe
        ShiftDTO expectedDto = ShiftDTO.builder()
                .id(expectedId)
                .name(inputDto.name())
                .jobPositions(jobPositionDtos) // U\u017Cywamy tych samych DTO
                .build();

        // MOCKING ZACHOWANIA:
        // Mockujemy konwersj\u0119 DTO -> Entity (metoda toEntity z jednym argumentem)
        // Zak\u0142adamy, \u017Ce MapStruct jest w stanie sam obs\u0142u\u017Cy\u0107 JobPositionDTO do
        // ShiftJobPosition
        when(shiftMapper.toEntity(inputDto)).thenReturn(unsavedShift);

        // Mockowanie mapowania ko\u0144cowego
        when(shiftMapper.toDTO(savedShift)).thenReturn(expectedDto);

        // WHEN
        ShiftDTO result = shiftService.createShift(inputDto);

        // THEN
        // 1. Asercje
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(expectedId);
        assertThat(result.name()).isEqualTo("Popołudniowa Zmiana");
        assertThat(result.jobPositions()).hasSize(2);

        // 2. Weryfikacja interakcji
        // Sprawdzamy, czy u\u017Cyto konwersji DTO -> Entity (MapStruct)
        verify(shiftMapper, times(1)).toEntity(inputDto);

        // Sprawdzamy, czy zapisano encj\u0119
        verify(shiftRepository, times(1)).save(unsavedShift);

        // Sprawdzamy, czy zwr\u00f3cono DTO
        verify(shiftMapper, times(1)).toDTO(savedShift);

        // Weryfikacja, \u017Ce nie u\u017Cyto przestarza\u0142ej metody mapowania z dwoma argumentami
        verify(shiftMapper, never()).toEntity(any(CreateShiftDTO.class), any());
    }
}
