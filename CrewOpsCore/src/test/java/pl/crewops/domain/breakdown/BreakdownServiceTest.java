package pl.crewops.domain.breakdown;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static pl.crewops.domain.breakdown.BreakdownTestFactory.*;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.crewops.domain.employee.EmployeeAPI;
import pl.crewops.domain.vehicle.VehicleAPI;
import pl.crewops.dto.breakdown.BreakdownDTO;
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
    void shouldReturnBreakdownDTO_WhenCreateDTOIsValid() {
        // when
        when(vehicleAPI.getVehicle(any(UUID.class))).thenReturn(getVehicle());
        when(employeeAPI.getEmployee(any(UUID.class))).thenReturn(getEmployee());
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
}
