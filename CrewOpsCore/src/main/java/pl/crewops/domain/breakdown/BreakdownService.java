package pl.crewops.domain.breakdown;

import static pl.crewops.domain.breakdown.BreakdownMapper.toDTO;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.domain.employee.EmployeeAPI;
import pl.crewops.domain.vehicle.VehicleAPI;
import pl.crewops.dto.breakdown.BreakdownDTO;
import pl.crewops.dto.breakdown.CreateBreakdownDTO;
import pl.crewops.dto.breakdown.UpdateBreakdownDTO;
import pl.crewops.dto.vehicle.UpdateVehicleDTO;
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
            throw new VehicleNotFoundException(createBreakdownDTO.vehicleId());
        }
        Employee employee;
        try {
            employee = employeeAPI.getEmployee(createBreakdownDTO.reportedByEmployeeId());
        } catch (EmployeeNotFoundException e) {
            log.error("Not found 'reportedBy' employee during create breakdown: {}", e.getMessage());
            throw new EmployeeNotFoundException(createBreakdownDTO.reportedByEmployeeId());
        }

        var breakdown = Breakdown.builder()
                .description(createBreakdownDTO.description())
                .vehicle(vehicle)
                .reportedBy(employee)
                .critical(createBreakdownDTO.critical())
                .build();

        log.info("Created breakdown: {}", breakdown);

        if (createBreakdownDTO.critical()) {
            var updateVehicle = UpdateVehicleDTO.builder()
                    .vehicleId(vehicle.getId())
                    .broken(true)
                    .build();

            vehicleAPI.updateVehicle(updateVehicle);
        }

        return toDTO(breakdownRepository.save(breakdown));
    }

    public Breakdown getBreakdown(UUID id) {
        return breakdownRepository.findById(id).orElseThrow(() -> new BreakdownNotFoundException(id));
    }

    public List<BreakdownDTO> getAllBreakdowns() {
        return breakdownRepository.findAll().stream()
                .map(BreakdownMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public BreakdownDTO updateBreakdown(UpdateBreakdownDTO updateBreakdownDTO) {
        log.info("Updating breakdown: {}", updateBreakdownDTO);
        Breakdown breakdown = breakdownRepository
                .findById(updateBreakdownDTO.breakdownId())
                .orElseThrow(() -> new BreakdownNotFoundException(updateBreakdownDTO.breakdownId()));

        if (updateBreakdownDTO.solved()) {
            Employee employee = employeeAPI.getEmployee(updateBreakdownDTO.repairedByEmployeeId());
            Vehicle vehicle = vehicleAPI.getVehicle(breakdown.getVehicle().getId());
            vehicle.setBroken(false);
            breakdown.setSolved(true);
            breakdown.setRepairedBy(employee);
            breakdown.setSolvedAt(Instant.now());
        } else {
            throw new IllegalArgumentException("Can't update breakdown if not solved");
        }

        log.info("Updated breakdown: {}", breakdown);
        return toDTO(breakdownRepository.save(breakdown));
    }

    @Transactional
    public void deleteBreakdown(UUID id) {
        breakdownRepository.deleteById(id);
    }
}
