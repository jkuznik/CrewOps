package pl.crewops.domain.shift;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.crewops.domain.employee.EmployeeAPI;
import pl.crewops.domain.jobPosition.JobPositionAPI;
import pl.crewops.domain.jobPosition.JobPositionMapperStruct;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;
import pl.crewops.model.dto.shift.CreateShiftDTO;
import pl.crewops.model.dto.shift.ShiftDTO;
import pl.crewops.model.tenantSchema.JobPosition;
import pl.crewops.model.tenantSchema.Shift;

@SpringJUnitConfig(
        classes = {
            ShiftService.class,
            ShiftRepository.class,
            SJPRepository.class,
            ShiftMapper.class,
            JobPositionAPI.class,
            EmployeeAPI.class,
            JobPositionMapperStruct.class
        })
class ShiftServiceTest {

    @Autowired
    ShiftService shiftService;

    @MockitoBean
    ShiftRepository shiftRepository;

    @MockitoBean
    SJPRepository sjpRepository;

    @MockitoBean
    ShiftMapper shiftMapper;

    @MockitoBean
    JobPositionAPI jobPositionAPI;

    @MockitoBean
    EmployeeAPI employeeAPI;

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

        var jobPosition = JobPosition.builder().name("Operator").build();

        Set<JobPositionDTO> jobPositionDtos = Set.of(
                JobPositionDTO.builder().id(UUID.randomUUID()).name("Operator").build(),
                JobPositionDTO.builder()
                        .id(UUID.randomUUID())
                        .name("Lider Zmiany")
                        .build());

        // 2. DTO wej\u015Bciowe
        CreateShiftDTO inputDto = CreateShiftDTO.builder()
                .name("Popo\u0142udniowa Zmiana")
                .configs(Set.of())
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
                .shiftConfigs(Set.of())
                .build();

        when(shiftMapper.toEntity(inputDto)).thenReturn(unsavedShift);

        // Mockowanie mapowania ko\u0144cowego
        when(shiftMapper.toDTO(savedShift)).thenReturn(expectedDto);

        when(jobPositionAPI.findById(any())).thenReturn(Optional.of(jobPosition));

        // WHEN
        ShiftDTO result = shiftService.createShift(inputDto);

        // THEN
        // 1. Asercje
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(expectedId);
        assertThat(result.name()).isEqualTo("Popołudniowa Zmiana");
        //        assertThat(result.shiftConfigs()).hasSize(2);

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
