package pl.crewops.domain.employee;

import static pl.crewops.domain.employee.EmployeeMapper.mapToDTO;
import static pl.crewops.domain.employee.EmployeeMapper.mapToEntity;
import static pl.crewops.utils.pagination.PageRequestFactory.createPageRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pl.crewops.domain.qualification.QualificationAPI;
import pl.crewops.domain.vehicle.VehicleAPI;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.employee.UpdateEmployeeDTO;
import pl.crewops.exception.EmployeeNotFoundException;
import pl.crewops.exception.EmployeeQualificationNotFoundException;
import pl.crewops.exception.ExpireAtException;
import pl.crewops.model.Employee;
import pl.crewops.model.Qualification;
import pl.crewops.model.Vehicle;
import pl.crewops.model.joinTable.EmployeeQualification;

@Service
@RequiredArgsConstructor
@Validated
class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeQualificationRepository employeeQualificationRepository;
    private final QualificationAPI qualificationAPI;
    private final VehicleAPI vehicleAPI;

    public EmployeeDTO createEmployee(@Valid @NotNull CreateEmployeeDTO createEmployeeDTO) {
        return mapToDTO(employeeRepository.save(mapToEntity(createEmployeeDTO)));
    }

    public List<EmployeeDTO> getAllEmployees(int page, int size) {
        return employeeRepository.findAll(getPageRequest(page, size)).stream()
                .map(EmployeeMapper::mapToDTO)
                .toList();
    }

    public List<EmployeeDTO> getEmployeesByQualification(@NotNull UUID qualificationId, int page, int size) {
        return employeeRepository.findByQualificationId(qualificationId, getPageRequest(page, size)).stream()
                .map(EmployeeMapper::mapToDTO)
                .toList();
    }

    public List<EmployeeDTO> getEmployeesByVehicles(@NotNull UUID vehicleId, int page, int size) {
        return employeeRepository.findByVehiclesId(vehicleId, getPageRequest(page, size)).stream()
                .map(EmployeeMapper::mapToDTO)
                .toList();
    }

    @Transactional
    public EmployeeDTO updateEmployee(@Valid @NotNull UpdateEmployeeDTO updateEmployeeDTO) {
        Employee employee = employeeRepository
                .findById(updateEmployeeDTO.employeeId())
                .orElseThrow(() -> new EmployeeNotFoundException(updateEmployeeDTO.employeeId()));

        if (updateEmployeeDTO.phoneNumber() != null) {
            employee.setPhoneNumber(updateEmployeeDTO.phoneNumber());
        }
        if (updateEmployeeDTO.department() != null) {
            employee.setDepartment(updateEmployeeDTO.department());
        }

        return mapToDTO(employee);
    }

    @Transactional
    public EmployeeDTO removePhoneNumber(@NotNull UUID employeeId) {
        Employee employee =
                employeeRepository.findById(employeeId).orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        employee.setPhoneNumber(null);

        return mapToDTO(employee);
    }

    @Transactional
    public void deleteEmployee(@NotNull UUID employeeId) {
        employeeRepository.deleteById(employeeId);
    }

    @Transactional
    public EmployeeDTO addQualification(@NotNull UUID employeeId, @NotNull UUID qualificationId) {
        Employee employee =
                employeeRepository.findById(employeeId).orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        Qualification qualification = qualificationAPI.getQualification(qualificationId);

        employee.getQualifications().add(qualification);

        return mapToDTO(employee);
    }

    @Transactional
    public EmployeeDTO updateQualificationExpiredAt(
            @NotNull UUID employeeId, @NotNull UUID qualificationId, Instant expiredAt) {
        EmployeeQualification employeeQualification = employeeQualificationRepository
                .findByEmployeeQualificationId(employeeId, qualificationId)
                .orElseThrow(() -> new EmployeeQualificationNotFoundException(employeeId, qualificationId));

        expireDateValidator(expiredAt);

        employeeQualification.setExpiredAt(expiredAt);

        return mapToDTO(
                employeeRepository.findById(employeeId).orElseThrow(() -> new EmployeeNotFoundException(employeeId)));
    }

    @Transactional
    public void removeQualification(@NotNull UUID employeeId, @NotNull UUID qualificationId) {
        Employee employee =
                employeeRepository.findById(employeeId).orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        Qualification qualification = qualificationAPI.getQualification(qualificationId);

        employee.getQualifications().remove(qualification);
    }

    @Transactional
    public EmployeeDTO addVehicle(@NotNull UUID employeeId, @NotNull UUID vehicleId) {
        Employee employee =
                employeeRepository.findById(employeeId).orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        Vehicle vehicle = vehicleAPI.getVehicle(vehicleId);

        employee.getVehicles().add(vehicle);

        return mapToDTO(employee);
    }

    @Transactional
    public void removeVehicle(@NotNull UUID employeeId, @NotNull UUID vehicleId) {
        Employee employee =
                employeeRepository.findById(employeeId).orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        Vehicle vehicle = vehicleAPI.getVehicle(vehicleId);

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
        return createPageRequest(page, size, Sort.by(Sort.Order.asc("lastName"), Sort.Order.asc("firstName")));
    }
}
