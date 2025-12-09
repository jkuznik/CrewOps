package pl.crewops.domain.breakdown;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static pl.crewops.domain.breakdown.BreakdownTestFactory.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.crewops.domain.employee.EmployeeAPI;
import pl.crewops.domain.machine.MachineAPI;
import pl.crewops.model.dto.breakdown.BreakdownDTO;
import pl.crewops.model.dto.breakdown.CreateBreakdownDTO;
import pl.crewops.model.dto.breakdown.UpdateBreakdownDTO;
import pl.crewops.model.tenantSchema.Breakdown;

@SpringJUnitConfig(classes = {BreakdownService.class})
class BreakdownServiceTest {

    @Autowired
    private BreakdownService breakdownService;

    @MockitoBean
    private BreakdownRepository breakdownRepository;

    @MockitoBean
    private MachineAPI machineAPI;

    @MockitoBean
    private EmployeeAPI employeeAPI;

    @MockitoBean
    private BreakdownMapper mapper;

    @Test
    void createBreakdown_shouldReturnBreakdownDTO_WhenCreateDTOIsValid() {
        CreateBreakdownDTO command = createBreakdownDTO();
        Breakdown entity = breakdown();

        // when
        when(machineAPI.getMachine(any(UUID.class))).thenReturn(machine());
        when(employeeAPI.getEmployeeById(any(UUID.class))).thenReturn(employee());

        when(breakdownRepository.save(any(Breakdown.class))).thenReturn(entity);

        // mapper mocks
        when(mapper.toDTO(any(Breakdown.class))).thenReturn(breakdownDTO());

        // then
        BreakdownDTO result = breakdownService.createBreakdown(command);

        assertThat(result).isNotNull();
        assertThat(result.description()).isEqualTo("description");
    }

    @Test
    void shouldReturnBreakdownEntity_WhenBreakdownExists() {
        Breakdown entity = breakdown();

        // when
        when(breakdownRepository.findById(any())).thenReturn(Optional.of(entity));

        // then
        Breakdown result = breakdownService.getBreakdown(UUID.randomUUID());

        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo("description");
    }

    @Test
    void shouldReturnListOfBreakdownsDTO_WhenAnyBreakdownExist() {
        Breakdown entity = breakdown();
        BreakdownDTO dto = breakdownDTO();

        // when
        when(breakdownRepository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDTO(any())).thenReturn(dto);

        // then
        List<BreakdownDTO> result = breakdownService.getAllBreakdowns();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).description()).isEqualTo("description");
    }

    @Test
    void shouldReturnBreakdownDTO_WhenUpdateDTOIsValid() {
        Breakdown entity = breakdown();
        BreakdownDTO dto = breakdownDTO();

        // when
        when(breakdownRepository.findById(any())).thenReturn(Optional.of(entity));
        when(employeeAPI.getEmployeeById(any(UUID.class))).thenReturn(employee());
        when(machineAPI.getMachine(any())).thenReturn(machine());
        when(breakdownRepository.save(any(Breakdown.class))).thenReturn(entity);

        when(mapper.toDTO(any())).thenReturn(dto);

        // then
        BreakdownDTO result = breakdownService.updateBreakdown(updateBreakdownDTO());

        assertThat(result).isNotNull();
        assertThat(result.description()).isEqualTo("description");
        assertThat(result.solved()).isTrue();
    }

    @Test
    void shouldThrowException_WhenUpdateDTOSolvedPropertyIsFalse() {
        var updateCommand = UpdateBreakdownDTO.builder().solved(false).build();

        // when
        when(breakdownRepository.findById(any())).thenReturn(Optional.of(breakdown()));
        Exception result = catchException(() -> breakdownService.updateBreakdown(updateCommand));

        // then
        assertThat(result).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldDeleteBreakdown_WhenBreakdownExists() {
        // when
        doNothing().when(breakdownRepository).deleteById(any());
        breakdownService.deleteBreakdown(UUID.randomUUID());

        // then
        verify(breakdownRepository, times(1)).deleteById(any(UUID.class));
    }
}
