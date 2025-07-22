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
import pl.crewops.domain.vehicle.VehicleAPI;
import pl.crewops.dto.breakdown.BreakdownDTO;
import pl.crewops.dto.breakdown.UpdateBreakdownDTO;
import pl.crewops.model.Breakdown;

@SpringJUnitConfig(classes = {BreakdownService.class, BreakdownRepository.class, VehicleAPI.class, EmployeeAPI.class})
class BreakdownServiceTest {

    @Autowired
    private BreakdownService breakdownService;

    @MockitoBean
    private BreakdownRepository breakdownRepository;

    @MockitoBean
    private VehicleAPI vehicleAPI;

    @MockitoBean
    private EmployeeAPI employeeAPI;

    private BreakdownDTO breakdownDTO;

    @Test
    void createBreakdown_shouldReturnBreakdownDTO_WhenCreateDTOIsValid() {
        // when
        when(vehicleAPI.getVehicle(any(UUID.class))).thenReturn(getVehicle());
        when(employeeAPI.getEmployeeById(any(UUID.class))).thenReturn(getEmployee());
        when(breakdownRepository.save(any(Breakdown.class))).thenReturn(getBreakdown());

        // then
        BreakdownDTO result = breakdownService.createBreakdown(createBreakdownDTO());

        assertThat(result).isNotNull();
        assertThat(result.description()).isEqualTo("description");
    }

    @Test
    void shouldReturnBreakdownEntity_WhenBreakdownExists() {
        // when
        when(breakdownRepository.findById(any())).thenReturn(Optional.of(getBreakdown()));

        // then
        Breakdown result = breakdownService.getBreakdown(UUID.randomUUID());

        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo("description");
    }

    @Test
    void shouldReturnListOfBreakdownsDTO_WhenAnyBreakdownExist() {
        // when
        when(breakdownRepository.findAll()).thenReturn(List.of(getBreakdown()));

        // then
        List<BreakdownDTO> result = breakdownService.getAllBreakdowns();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).description()).isEqualTo("description");
    }

    @Test
    void shouldReturnBreakdownDTO_WhenUpdateDTOIsValid() {
        // when
        when(breakdownRepository.findById(any())).thenReturn(Optional.of(getBreakdown()));
        when(employeeAPI.getEmployeeById(any(UUID.class))).thenReturn(getEmployee());
        when(vehicleAPI.getVehicle(any())).thenReturn(getVehicle());
        when(breakdownRepository.save(any(Breakdown.class))).thenReturn(getBreakdown());

        // then
        BreakdownDTO result = breakdownService.updateBreakdown(getUpdateBreakdownDTO());

        assertThat(result).isNotNull();
        assertThat(result.description()).isEqualTo("description");
        assertThat(result.solved()).isTrue();
    }

    @Test
    void shouldThrowException_WhenUpdateDTOSolvedPropertyIsFalse() {
        // given
        var updateCommand = UpdateBreakdownDTO.builder().solved(false).build();

        // when
        when(breakdownRepository.findById(any())).thenReturn(Optional.of(getBreakdown()));
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
