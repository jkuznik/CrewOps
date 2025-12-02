package pl.crewops.domain.shift;

import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.domain.employee.EmployeeAPI;
import pl.crewops.domain.jobPosition.JobPositionAPI;
import pl.crewops.exception.domain.jobPosition.JobPositionNotFoundException;
import pl.crewops.exception.domain.shift.ShiftNotFoundException;
import pl.crewops.model.compositePK.SJPID;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;
import pl.crewops.model.dto.shift.CreateShiftDTO;
import pl.crewops.model.dto.shift.ShiftConfig;
import pl.crewops.model.dto.shift.ShiftDTO;
import pl.crewops.model.dto.shift.UpdateShiftDTO;
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

        if (createShiftDTO.configs() != null) {
            List<JobPosition> declaredJobPositions = createShiftDTO.configs().stream()
                    .map(shiftConfig ->
                            findJobPosition(shiftConfig.jopPosition().id()))
                    .toList();

            saved.setJobPositions(declaredJobPositions);

            shiftRepository.flush();

            List<ShiftJobPosition> configureShift = declaredJobPositions.stream()
                    .map(jobPosition -> sjpRepository
                            .findById(new SJPID(jobPosition.getId(), saved.getId()))
                            .orElseGet(() -> {
                                return new ShiftJobPosition(new SJPID(jobPosition.getId(), saved.getId()));
                            }))
                    .toList();

            configureShift.forEach(shiftJobPositionRecord -> {
                Optional<ShiftConfig> first = createShiftDTO.configs().stream()
                        .filter(config -> {
                            return config.jopPosition()
                                    .id()
                                    .equals(shiftJobPositionRecord
                                            .getJobPosition()
                                            .getId());
                        })
                        .findFirst();
                if (first.isPresent()) {
                    ShiftConfig jobPositionConfig = first.get();
                    shiftJobPositionRecord.setCritical(jobPositionConfig.critical());
                    if (jobPositionConfig.relatedEmployee() != null) {
                        Employee employeeById = employeeAPI.getEmployeeById(
                                jobPositionConfig.relatedEmployee().id());
                        shiftJobPositionRecord.setAssignedEmployee(employeeById);
                    }
                }
            });
        }

        return mapper.toDTO(saved);
    }

    @Override
    @Transactional
    public List<ShiftDTO> getAllShifts() {
        List<ShiftDTO> result = new ArrayList<>();
        List<Shift> shifts = shiftRepository.findAll();
        shifts.forEach(shift -> {
            List<ShiftJobPosition> shiftJobPositions = new ArrayList<>();
            shift.getJobPositions().forEach(jobPosition -> {
                Optional<ShiftJobPosition> byId = sjpRepository.findById(new SJPID(jobPosition.getId(), shift.getId()));
                byId.ifPresent(shiftJobPositions::add);
            });
            Set<ShiftConfig> shiftConfigs = new HashSet<>();
            shiftJobPositions.forEach(shiftJobPosition -> {
                var jobPositionDTO = JobPositionDTO.builder()
                        .id(shiftJobPosition.getJobPosition().getId())
                        .name(shiftJobPosition.getJobPosition().getName())
                        .build();

                EmployeeDTO employeeDTO = null;
                if (shiftJobPosition.getAssignedEmployee() != null) {
                    employeeDTO = EmployeeDTO.builder()
                            .id(shiftJobPosition.getAssignedEmployee().getId())
                            .firstName(shiftJobPosition.getAssignedEmployee().getFirstName())
                            .lastName(shiftJobPosition.getAssignedEmployee().getLastName())
                            .build();
                }

                shiftConfigs.add(new ShiftConfig(jobPositionDTO, employeeDTO, shiftJobPosition.isCritical()));
            });
            var shiftDTO = ShiftDTO.builder()
                    .id(shift.getId())
                    .name(shift.getName())
                    .shiftConfigs(shiftConfigs)
                    .color(shift.getColor())
                    .build();
            result.add(shiftDTO);
        });

        return result;
    }

    private JobPosition findJobPosition(UUID id) {
        return jobPositionAPI.findById(id).orElseThrow(() -> new JobPositionNotFoundException(id));
    }

    @Override
    @Transactional
    public ShiftDTO updateShift(UpdateShiftDTO updateShiftDTO) {
        Shift existedShift = shiftRepository
                .findById(updateShiftDTO.id())
                .orElseThrow(() -> new ShiftNotFoundException(updateShiftDTO.id()));

        existedShift.setName(updateShiftDTO.name());
        existedShift.setColor(updateShiftDTO.color());

        if (updateShiftDTO.configs() != null) {
            List<JobPosition> declaredJobPositions = updateShiftDTO.configs().stream()
                    .map(shiftConfig ->
                            findJobPosition(shiftConfig.jopPosition().id()))
                    .toList();

            existedShift.setJobPositions(declaredJobPositions);

            shiftRepository.flush();

            List<ShiftJobPosition> configureShift = declaredJobPositions.stream()
                    .map(jobPosition -> sjpRepository
                            .findById(new SJPID(jobPosition.getId(), existedShift.getId()))
                            .orElseGet(() -> {
                                return new ShiftJobPosition(new SJPID(jobPosition.getId(), existedShift.getId()));
                            }))
                    .toList();

            configureShift.forEach(shiftJobPositionRecord -> {
                Optional<ShiftConfig> first = updateShiftDTO.configs().stream()
                        .filter(config -> {
                            return config.jopPosition()
                                    .id()
                                    .equals(shiftJobPositionRecord
                                            .getJobPosition()
                                            .getId());
                        })
                        .findFirst();
                if (first.isPresent()) {
                    ShiftConfig jobPositionConfig = first.get();
                    shiftJobPositionRecord.setCritical(jobPositionConfig.critical());
                    if (jobPositionConfig.relatedEmployee() != null) {
                        Employee employeeById = employeeAPI.getEmployeeById(
                                jobPositionConfig.relatedEmployee().id());
                        shiftJobPositionRecord.setAssignedEmployee(employeeById);
                    }
                }
            });
        }

        return mapper.toDTO(existedShift);
    }

    @Override
    @Transactional
    public void deleteShift(UUID id) {
        shiftRepository.deleteById(id);
    }
}
