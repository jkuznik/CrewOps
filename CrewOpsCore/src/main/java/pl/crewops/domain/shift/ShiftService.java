package pl.crewops.domain.shift;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.domain.employee.EmployeeAPI;
import pl.crewops.domain.jobPosition.JobPositionAPI;
import pl.crewops.exception.domain.jobPosition.JobPositionNotFoundException;
import pl.crewops.model.compositePK.SJPID;
import pl.crewops.model.dto.shift.CreateShiftDTO;
import pl.crewops.model.dto.shift.ShiftConfig;
import pl.crewops.model.dto.shift.ShiftDTO;
import pl.crewops.model.joinTable.ShiftJobPosition;
import pl.crewops.model.tenantSchema.Employee;
import pl.crewops.model.tenantSchema.JobPosition;
import pl.crewops.model.tenantSchema.Shift;

@Slf4j
@Service
@RequiredArgsConstructor
class ShiftService implements ShiftAPI {

    private final ShiftRepository shiftRepository;
    private final SJPRepository sjpRepository;
    private final ShiftMapper mapper;

    private final JobPositionAPI jobPositionAPI;
    private final EmployeeAPI employeeAPI;

    @Override
    @Transactional
    public ShiftDTO createShift(CreateShiftDTO createShiftDTO) {

        Shift saved = shiftRepository.save(mapper.toEntity(createShiftDTO));

        if (createShiftDTO.jobPositions() != null) {
            Set<JobPosition> declaredJobPositions = createShiftDTO.jobPositions().stream()
                    .map(jobPositionDTO -> findJobPosition(jobPositionDTO.id()))
                    .collect(Collectors.toSet());

            saved.setJobPositions(declaredJobPositions);

            shiftRepository.flush();

            Set<ShiftJobPosition> configureShift = declaredJobPositions.stream()
                    .map(jobPosition -> sjpRepository
                            .findById(new SJPID(jobPosition.getId(), saved.getId()))
                            .orElseGet(() -> {
                                return new ShiftJobPosition(new SJPID(jobPosition.getId(), saved.getId()));
                            }))
                    .collect(Collectors.toSet());

            configureShift.forEach(shiftJobPositionRecord -> {
                Optional<ShiftConfig> first = createShiftDTO.configs().stream()
                        .filter(config -> {
                            return config.jopPositionId()
                                    .equals(shiftJobPositionRecord
                                            .getJobPosition()
                                            .getId());
                        })
                        .findFirst();
                if (first.isPresent()) {
                    ShiftConfig jobPositionConfig = first.get();
                    shiftJobPositionRecord.setCritical(jobPositionConfig.critical());
                    if (jobPositionConfig.relatedEmployeeId() != null) {
                        Employee employeeById = employeeAPI.getEmployeeById(jobPositionConfig.relatedEmployeeId());
                        shiftJobPositionRecord.setAssignedEmployee(employeeById);
                    }
                }
            });
        }

        return mapper.toDTO(saved);
    }

    private JobPosition findJobPosition(UUID id) {
        return jobPositionAPI.findById(id).orElseThrow(() -> new JobPositionNotFoundException(id));
    }
}
