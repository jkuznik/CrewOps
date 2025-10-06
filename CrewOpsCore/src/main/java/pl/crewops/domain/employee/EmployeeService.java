package pl.crewops.domain.employee;

import static pl.crewops.domain.employee.EmployeeMapper.mapToDTO;
import static pl.crewops.domain.employee.EmployeeMapper.mapToEntity;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.domain.department.DepartmentAPI;
import pl.crewops.domain.machine.MachineAPI;
import pl.crewops.domain.qualification.QualificationAPI;
import pl.crewops.exception.domain.employee.EmployeeNotFoundException;
import pl.crewops.exception.domain.employee.EmployeeQualificationNotFoundException;
import pl.crewops.exception.domain.employee.ExpireAtException;
import pl.crewops.infrastructure.multitenancy.TenantContext;
import pl.crewops.model.dto.department.DepartmentDTO;
import pl.crewops.model.dto.employee.CreateEmployeeDTO;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.model.dto.employee.EmployeeQualificationDTO;
import pl.crewops.model.dto.employee.UpdateEmployeeDTO;
import pl.crewops.model.dto.qualification.UpdateQualificationExpiredAtDTO;
import pl.crewops.model.joinTable.EmployeeQualification;
import pl.crewops.model.tenantSchema.Department;
import pl.crewops.model.tenantSchema.Employee;
import pl.crewops.model.tenantSchema.Machine;
import pl.crewops.model.tenantSchema.Qualification;
import pl.crewops.util.pagination.PageRequestFactory;

@Slf4j
@Service
@RequiredArgsConstructor
class EmployeeService implements EmployeeAPI {

    private final EmployeeRepository employeeRepository;
    private final EmployeeQualificationRepository employeeQualificationRepository;
    private final DepartmentAPI departmentAPI;
    private final QualificationAPI qualificationAPI;
    private final MachineAPI machineAPI;

    @Transactional
    public EmployeeDTO createEmployee(CreateEmployeeDTO createEmployeeDTO) {
        log.info(TenantContext.getCurrentTenant());
        try {
            Employee employee = employeeRepository.save(mapToEntity(createEmployeeDTO));
            log.info("Create employee {}", createEmployeeDTO);
            return mapToDTO(employee, true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = true)
    public List<EmployeeDTO> getAllEmployees(int page, int size) {
        log.info("Get all employees with pagination. Page: {}, size: {}", page, size);
        return employeeRepository.findAll(getPageRequest(page, size)).stream()
                .map(EmployeeMapper::mapToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EmployeeDTO> getAllActiveEmployees(int page, int size) {
        log.info("Get all active employees. Page: {}, size: {}", page, size);
        return employeeRepository.findAllByActiveIsTrue(getPageRequest(page, size)).stream()
                .map(EmployeeMapper::mapToDTO)
                .toList();
    }

    @Override
    public List<EmployeeDTO> getAllActiveEmployees() {
        return employeeRepository.findAllByActiveIsTrue().stream()
                .map(EmployeeMapper::mapToDTO)
                .toList();
    }

    @Override
    public List<EmployeeDTO> getAllActiveEmployeesByDepartment(UUID departmentId) {
        return employeeRepository.findAllByDepartmentIdAndActiveIsTrue(departmentId).stream()
                .map(EmployeeMapper::mapToDTO)
                .toList();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public Employee getEmployeeById(UUID id) {
        return employeeRepository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public EmployeeDTO getEmployeeDTOById(UUID id) {
        return mapToDTO(getEmployeeById(id));
    }

    @Transactional(readOnly = true)
    public List<EmployeeDTO> getEmployeesByQualification(UUID qualificationId, int page, int size) {
        log.info("Get employees by qualification");
        return employeeRepository
                .findByQualificationIdAndActiveIsTrue(qualificationId, getPageRequest(page, size))
                .stream()
                .map(EmployeeMapper::mapToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EmployeeDTO> getEmployeesByMachines(UUID machineId, int page, int size) {
        log.info("Get employees by machines");
        return employeeRepository.findByMachinesIdAndActiveIsTrue(machineId, getPageRequest(page, size)).stream()
                .map(EmployeeMapper::mapToDTO)
                .toList();
    }

    @Transactional
    public EmployeeDTO updateEmployee(UpdateEmployeeDTO updateEmployeeDTO) {
        Employee employee = employeeRepository
                .findById(updateEmployeeDTO.employeeId())
                .orElseThrow(() -> new EmployeeNotFoundException(updateEmployeeDTO.employeeId()));

        if (updateEmployeeDTO.phoneNumber() != null) {
            if (updateEmployeeDTO.phoneNumber().isEmpty()) {
                employee.setPhoneNumber(null);
            } else {
                employee.setPhoneNumber(updateEmployeeDTO.phoneNumber());
            }
        }

        if (updateEmployeeDTO.email() != null) {
            if (updateEmployeeDTO.email().isEmpty()) {
                employee.setEmail(null);
            } else {
                employee.setEmail(updateEmployeeDTO.email());
            }
        }

        if (updateEmployeeDTO.departments() != null) {
            Set<Department> departments = departmentAPI.getDepartmentsIn(updateEmployeeDTO.departments().stream()
                    .map(DepartmentDTO::id)
                    .collect(Collectors.toSet()));
            employee.setDepartments(departments);
        }

        if (updateEmployeeDTO.active() != null) {
            employee.setActive(updateEmployeeDTO.active());
        }

        log.info("Update employee {}", updateEmployeeDTO);
        return mapToDTO(employee);
    }

    @Transactional
    public EmployeeDTO removePhoneNumber(UUID employeeId) {
        Employee employee =
                employeeRepository.findById(employeeId).orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        employee.setPhoneNumber(null);

        log.info("Remove phone number from employee {}", employeeId);
        return mapToDTO(employee);
    }

    @Transactional
    public void deleteEmployee(UUID employeeId) {
        var employee =
                employeeRepository.findById(employeeId).orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        employee.setActive(false);
        log.info("Set 'active' column to 'false' for employee {}", employeeId);
        employeeRepository.save(employee);
    }

    @Transactional
    public EmployeeDTO addQualification(UUID employeeId, UUID qualificationId) {
        Employee employee =
                employeeRepository.findById(employeeId).orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        Qualification qualification = qualificationAPI.getQualification(qualificationId);

        employee.getQualifications().add(qualification);

        log.info("Add qualification {} to employee {}", qualificationId, employeeId);
        return mapToDTO(employee);
    }

    @Transactional
    @Override
    public List<EmployeeQualificationDTO> getAllEmployeeQualificationsWithExpirationTime(UUID employeeId) {
        return employeeQualificationRepository.findAllByEmployeeIdAndExpiredAtIsNotNull(employeeId).stream()
                .map(EmployeeMapper::mapToEQDTO)
                .toList();
    }

    @Transactional
    public EmployeeDTO updateQualificationExpiredAt(
            UUID employeeId, UUID qualificationId, UpdateQualificationExpiredAtDTO updateQualificationExpiredAtDTO) {
        EmployeeQualification employeeQualification = employeeQualificationRepository
                .findByEmployeeQualificationId(employeeId, qualificationId)
                .orElseThrow(() -> new EmployeeQualificationNotFoundException(employeeId, qualificationId));

        var expiredAt = updateQualificationExpiredAtDTO.expiredAt();
        expireDateValidator(expiredAt);

        employeeQualification.setExpiredAt(expiredAt);

        log.info("Update qualification {} expired at {}", qualificationId, expiredAt);
        return mapToDTO(
                employeeRepository.findById(employeeId).orElseThrow(() -> new EmployeeNotFoundException(employeeId)));
    }

    @Transactional
    public void removeQualification(UUID employeeId, UUID qualificationId) {
        Employee employee =
                employeeRepository.findById(employeeId).orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        Qualification qualification = qualificationAPI.getQualification(qualificationId);

        log.info("Remove qualification {} from employee {}", qualificationId, employeeId);
        employee.getQualifications().remove(qualification);
    }

    @Transactional
    // TODO: consider to refactor code and introduce object like AddMachineCommand, AddQualificationCommand, Remove..
    public EmployeeDTO addMachine(UUID employeeId, UUID machineId) {
        Employee employee =
                employeeRepository.findById(employeeId).orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        Machine machine = machineAPI.getMachine(machineId);

        employee.getMachines().add(machine);

        log.info("Add machine {} to employee {}", machineId, employeeId);
        return mapToDTO(employee);
    }

    @Transactional
    public EmployeeDTO addDepartment(UUID employeeId, UUID departmentId) {
        Employee employee =
                employeeRepository.findById(employeeId).orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        Department department = departmentAPI.getDepartment(departmentId);

        employee.getDepartments().add(department);

        log.info("Add department {} to employee {}", departmentId, employeeId);
        return mapToDTO(employee);
    }

    @Transactional
    public void removeDepartment(UUID employeeId, UUID departmentId) {
        Employee employee =
                employeeRepository.findById(employeeId).orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        Department department = departmentAPI.getDepartment(departmentId);

        log.info("Remove department {} from employee {}", department, employeeId);
        employee.getDepartments().remove(department);
    }

    @Transactional
    public void removeMachine(UUID employeeId, UUID machineId) {
        Employee employee =
                employeeRepository.findById(employeeId).orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        Machine machine = machineAPI.getMachine(machineId);

        log.info("Remove machine {} from employee {}", machineId, employeeId);
        employee.getMachines().remove(machine);
    }

    private void expireDateValidator(Instant expiredAt) {
        if (expiredAt != null && expiredAt.isBefore(Instant.now())) {
            throw new ExpireAtException("Expire date can't be in the past");
        }
    }

    private static PageRequest getPageRequest(int page, int size) {
        return PageRequestFactory.createPageRequest(
                page, size, Sort.by(Sort.Order.asc("lastName"), Sort.Order.asc("firstName")));
    }
}
