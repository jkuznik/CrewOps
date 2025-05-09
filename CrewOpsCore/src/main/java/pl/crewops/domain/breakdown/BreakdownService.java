package pl.crewops.domain.breakdown;

import static pl.crewops.domain.breakdown.BreakdownMapper.toDTO;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.domain.employee.EmployeeAPI;
import pl.crewops.domain.vehicle.VehicleAPI;
import pl.crewops.dto.breakdown.BreakdownDTO;
import pl.crewops.dto.breakdown.CreateBreakdownDTO;
import pl.crewops.exception.BreakdownNotFoundException;
import pl.crewops.exception.EmployeeNotFoundException;
import pl.crewops.exception.VehicleNotFoundException;
import pl.crewops.model.Breakdown;
import pl.crewops.model.Employee;
import pl.crewops.model.Vehicle;

@Slf4j
@Service
@RequiredArgsConstructor
class BreakdownService implements BreakdownAPI {

    private final BreakdownRepository breakdownRepository;
    private final VehicleAPI vehicleAPI;
    private final EmployeeAPI employeeAPI;

    @Transactional
    public BreakdownDTO createBreakdown(CreateBreakdownDTO createBreakdownDTO) {
        Vehicle vehicle;
        try {
            vehicle = vehicleAPI.getVehicle(createBreakdownDTO.vehicleId());
        } catch (VehicleNotFoundException e) {
            log.error("Not found vehicle during create breakdown: {}", e.getMessage());
            // TODO: eventually add custom exception
            throw new IllegalArgumentException(e.getMessage());
        }
        Employee employee;
        try {
            employee = employeeAPI.getEmployee(createBreakdownDTO.reportedByEmployeeId());
        } catch (EmployeeNotFoundException e) {
            log.error("Not found employee during create breakdown: {}", e.getMessage());
            // TODO: eventually add custom exception
            throw new IllegalArgumentException(e.getMessage());
        }

        var breakdown = Breakdown.builder()
                .description(createBreakdownDTO.description())
                .vehicle(vehicle)
                .reportedBy(employee)
                .build();

        log.info("Created breakdown: {}", breakdown);
        return toDTO(breakdownRepository.save(breakdown));
    }

    public Breakdown getBreakdown(UUID id) {
        return breakdownRepository.findById(id).orElseThrow(() -> new BreakdownNotFoundException(id));
    }
}
