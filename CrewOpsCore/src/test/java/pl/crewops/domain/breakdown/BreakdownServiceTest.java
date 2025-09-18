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
import pl.crewops.model.Breakdown;
import pl.crewops.model.dto.breakdown.BreakdownDTO;
import pl.crewops.model.dto.breakdown.UpdateBreakdownDTO;

@SpringJUnitConfig(classes = {BreakdownService.class, BreakdownRepository.class, MachineAPI.class, EmployeeAPI.class})
class BreakdownServiceTest {

    @Autowired
    private BreakdownService breakdownService;

    @MockitoBean
    private BreakdownRepository breakdownRepository;

    @MockitoBean
    private MachineAPI machineAPI;

    @MockitoBean
    private EmployeeAPI employeeAPI;

    private BreakdownDTO breakdownDTO;

    @Test
    void createBreakdown_shouldReturnBreakdownDTO_WhenCreateDTOIsValid() {
        // when
        when(machineAPI.getMachine(any(UUID.class))).thenReturn(machine());
        when(employeeAPI.getEmployeeById(any(UUID.class))).thenReturn(employee());
        when(breakdownRepository.save(any(Breakdown.class))).thenReturn(breakdown());

        // then
        BreakdownDTO result = breakdownService.createBreakdown(createBreakdownDTO());

        assertThat(result).isNotNull();
        assertThat(result.description()).isEqualTo("description");
    }

    @Test
    void shouldReturnBreakdownEntity_WhenBreakdownExists() {
        // when
        when(breakdownRepository.findById(any())).thenReturn(Optional.of(breakdown()));

        // then
        Breakdown result = breakdownService.getBreakdown(UUID.randomUUID());

        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo("description");
    }

    @Test
    void shouldReturnListOfBreakdownsDTO_WhenAnyBreakdownExist() {
        // when
        when(breakdownRepository.findAll()).thenReturn(List.of(breakdown()));

        // then
        List<BreakdownDTO> result = breakdownService.getAllBreakdowns();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).description()).isEqualTo("description");
    }

    @Test
    void shouldReturnBreakdownDTO_WhenUpdateDTOIsValid() {
        // when
        when(breakdownRepository.findById(any())).thenReturn(Optional.of(breakdown()));
        when(employeeAPI.getEmployeeById(any(UUID.class))).thenReturn(employee());
        when(machineAPI.getMachine(any())).thenReturn(machine());
        when(breakdownRepository.save(any(Breakdown.class))).thenReturn(breakdown());

        // then
        BreakdownDTO result = breakdownService.updateBreakdown(updateBreakdownDTO());

        assertThat(result).isNotNull();
        assertThat(result.description()).isEqualTo("description");
        assertThat(result.solved()).isTrue();
    }

    @Test
    void shouldThrowException_WhenUpdateDTOSolvedPropertyIsFalse() {
        // given
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
