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
import pl.crewops.domain.machine.MachineAPI;
import pl.crewops.exception.domain.breakdown.BreakdownNotFoundException;
import pl.crewops.exception.domain.employee.EmployeeNotFoundException;
import pl.crewops.exception.domain.machine.MachineNotFoundException;
import pl.crewops.model.dto.breakdown.BreakdownDTO;
import pl.crewops.model.dto.breakdown.CreateBreakdownDTO;
import pl.crewops.model.dto.breakdown.UpdateBreakdownDTO;
import pl.crewops.model.dto.machine.UpdateMachineDTO;
import pl.crewops.model.tenantSchema.Breakdown;
import pl.crewops.model.tenantSchema.Employee;
import pl.crewops.model.tenantSchema.Machine;

@Slf4j
@Service
@RequiredArgsConstructor
class BreakdownService {

    private final BreakdownRepository breakdownRepository;
    private final MachineAPI machineAPI;
    private final EmployeeAPI employeeAPI;

    @Transactional
    public BreakdownDTO createBreakdown(CreateBreakdownDTO createBreakdownDTO) {
        Machine machine;
        try {
            machine = machineAPI.getMachine(createBreakdownDTO.machineId());
        } catch (MachineNotFoundException e) {
            log.error("Not found machine during create breakdown: {}", e.getMessage());
            throw new MachineNotFoundException(createBreakdownDTO.machineId());
        }
        Employee employee;
        try {
            employee = employeeAPI.getEmployeeById(createBreakdownDTO.reportedByEmployeeId());
        } catch (EmployeeNotFoundException e) {
            log.error("Failed to fetch info about 'reportedBy' employee during create breakdown: {}", e.getMessage());
            throw new EmployeeNotFoundException(createBreakdownDTO.reportedByEmployeeId());
        }

        var breakdown = Breakdown.builder()
                .description(createBreakdownDTO.description())
                .machine(machine)
                .reportedBy(employee)
                .critical(createBreakdownDTO.critical())
                .build();

        log.info("Created breakdown: {}", breakdown);

        if (createBreakdownDTO.critical()) {
            var updateMachine = UpdateMachineDTO.builder()
                    .machineId(machine.getId())
                    .broken(true)
                    .build();

            machineAPI.updateMachine(updateMachine);
        }

        return toDTO(breakdownRepository.save(breakdown));
    }

    @Transactional(readOnly = true)
    public Breakdown getBreakdown(UUID id) {
        return breakdownRepository.findById(id).orElseThrow(() -> new BreakdownNotFoundException(id));
    }

    @Transactional(readOnly = true)
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
            Employee employee = employeeAPI.getEmployeeById(updateBreakdownDTO.repairedByEmployeeId());
            Machine machine = machineAPI.getMachine(breakdown.getMachine().getId());
            breakdown.setSolved(true);
            breakdown.setRepairedBy(employee);
            breakdown.setSolvedAt(Instant.now());
            var updatedBreakdown = toDTO(breakdownRepository.save(breakdown));

            if (breakdownRepository
                    .findFirstByMachineAndCriticalIsTrueAndSolvedIsFalse(machine)
                    .isEmpty()) {
                machine.setBroken(false);
            }

            log.info("Updated breakdown: {}", breakdown);
            return updatedBreakdown;
        } else {
            throw new IllegalArgumentException("Can't update breakdown if not solved");
        }
    }

    @Transactional
    public void deleteBreakdown(UUID id) {
        breakdownRepository.deleteById(id);
    }
}
