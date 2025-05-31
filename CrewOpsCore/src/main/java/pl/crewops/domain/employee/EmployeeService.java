package pl.crewops.domain.employee;

import static pl.crewops.domain.employee.EmployeeMapper.mapToDTO;
import static pl.crewops.domain.employee.EmployeeMapper.mapToEntity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.auth.CreateAuthUserDTO;
import pl.crewops.domain.auth.AuthAPI;
import pl.crewops.domain.qualification.QualificationAPI;
import pl.crewops.domain.vehicle.VehicleAPI;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.employee.UpdateEmployeeDTO;
import pl.crewops.exception.*;
import pl.crewops.model.Employee;
import pl.crewops.model.Qualification;
import pl.crewops.model.Vehicle;
import pl.crewops.model.joinTable.EmployeeQualification;
import pl.crewops.utils.pagination.PageRequestFactory;

@Slf4j
@Service
@RequiredArgsConstructor
class EmployeeService implements EmployeeAPI {

    private final EmployeeRepository employeeRepository;
    private final EmployeeQualificationRepository employeeQualificationRepository;
    private final QualificationAPI qualificationAPI;
    private final VehicleAPI vehicleAPI;
    private final AuthAPI authAPI;

    @Transactional
    public EmployeeDTO createEmployee(CreateEmployeeDTO createEmployeeDTO) {
        if (authAPI.getByUsername(createEmployeeDTO.username()).isPresent()) {
            throw new UsernameAlreadyExistException(createEmployeeDTO.username());
        }

        try {
            Employee employee = employeeRepository.save(mapToEntity(createEmployeeDTO));
            var createAuthUser = CreateAuthUserDTO.builder()
                    .username(createEmployeeDTO.username())
                    .password(createEmployeeDTO.password())
                    .roles(createEmployeeDTO.roles())
                    .build();
            authAPI.createAuthUser(createAuthUser, employee);
            log.info("Create employee {}", createEmployeeDTO);
            return mapToDTO(employee);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public List<EmployeeDTO> getAllEmployees(int page, int size) {
        log.info("Get all employees with pagination. Page: {}, size: {}", page, size);
        return employeeRepository.findAll(getPageRequest(page, size)).stream()
                .map(EmployeeMapper::mapToDTO)
                .toList();
    }

    public List<EmployeeDTO> getAllActiveEmployees(int page, int size) {
        log.info("Get all active employees. Page: {}, size: {}", page, size);
        return employeeRepository.findAllByActiveIsTrue(getPageRequest(page, size)).stream()
                .map(EmployeeMapper::mapToDTO)
                .toList();
    }

    public Employee getEmployee(UUID id) {
        return employeeRepository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    public List<EmployeeDTO> getEmployeesByQualification(UUID qualificationId, int page, int size) {
        log.info("Get employees by qualification");
        return employeeRepository.findByQualificationId(qualificationId, getPageRequest(page, size)).stream()
                .map(EmployeeMapper::mapToDTO)
                .toList();
    }

    public List<EmployeeDTO> getEmployeesByVehicles(UUID vehicleId, int page, int size) {
        log.info("Get employees by vehicles");
        return employeeRepository.findByVehiclesId(vehicleId, getPageRequest(page, size)).stream()
                .map(EmployeeMapper::mapToDTO)
                .toList();
    }

    @Transactional
    public EmployeeDTO updateEmployee(UpdateEmployeeDTO updateEmployeeDTO) {
        Employee employee = employeeRepository
                .findById(updateEmployeeDTO.employeeId())
                .orElseThrow(() -> new EmployeeNotFoundException(updateEmployeeDTO.employeeId()));

        if (updateEmployeeDTO.phoneNumber() != null) {
            employee.setPhoneNumber(updateEmployeeDTO.phoneNumber());
        }
        if (updateEmployeeDTO.department() != null) {
            employee.setDepartment(updateEmployeeDTO.department());
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

        var authUser = authAPI.getByEmployee(employee).orElseThrow(() -> new AuthUserNotFoundException(employee));

        employee.setActive(false);
        log.info("Delete authUser {}", authUser.getUsername());
        authAPI.deleteById(authUser.getId());
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
    public EmployeeDTO updateQualificationExpiredAt(UUID employeeId, UUID qualificationId, Instant expiredAt) {
        EmployeeQualification employeeQualification = employeeQualificationRepository
                .findByEmployeeQualificationId(employeeId, qualificationId)
                .orElseThrow(() -> new EmployeeQualificationNotFoundException(employeeId, qualificationId));

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
    public EmployeeDTO addVehicle(UUID employeeId, UUID vehicleId) {
        Employee employee =
                employeeRepository.findById(employeeId).orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        Vehicle vehicle = vehicleAPI.getVehicle(vehicleId);

        employee.getVehicles().add(vehicle);

        log.info("Add vehicle {} to employee {}", vehicleId, employeeId);
        return mapToDTO(employee);
    }

    @Transactional
    public void removeVehicle(UUID employeeId, UUID vehicleId) {
        Employee employee =
                employeeRepository.findById(employeeId).orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        Vehicle vehicle = vehicleAPI.getVehicle(vehicleId);

        log.info("Remove vehicle {} from employee {}", vehicleId, employeeId);
        employee.getVehicles().remove(vehicle);
    }

    private void expireDateValidator(Instant expiredAt) {
        if (expiredAt == null) {
            throw new ExpireAtException("Expire date is null");
        }
        if (expiredAt.isBefore(Instant.now())) {
            throw new ExpireAtException("Expire date can't be in the past");
        }
    }

    private static PageRequest getPageRequest(int page, int size) {
        return PageRequestFactory.createPageRequest(
                page, size, Sort.by(Sort.Order.asc("lastName"), Sort.Order.asc("firstName")));
    }
}
