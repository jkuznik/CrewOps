package pl.kuznik.domain.employee;

import static pl.kuznik.domain.employee.EmployeeMapper.mapToDTO;
import static pl.kuznik.domain.employee.EmployeeMapper.mapToEntity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pl.kuznik.domain.employee.dto.CreateEmployeeDTO;
import pl.kuznik.domain.employee.dto.EmployeeDTO;
import pl.kuznik.domain.employee.dto.UpdateEmployeeDTO;
import pl.kuznik.domain.qualification.QualificationAPI;
import pl.kuznik.entity.Employee;
import pl.kuznik.entity.Qualification;
import pl.kuznik.entity.joinTable.EmployeeQualification;
import pl.kuznik.exception.EmployeeNotFoundException;
import pl.kuznik.exception.EmployeeQualificationNotFoundException;
import pl.kuznik.exception.ExpireAtException;

@Service
@RequiredArgsConstructor
@Validated
class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeQualificationRepository employeeQualificationRepository;
    private final QualificationAPI qualificationAPI;

    public EmployeeDTO createEmployee(@Valid @NotNull CreateEmployeeDTO createEmployeeDTO) {
        return mapToDTO(employeeRepository.save(mapToEntity(createEmployeeDTO)));
    }

    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
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

    private void expireDateValidator(Instant expiredAt) {
        if (expiredAt.isBefore(Instant.now())) {
            throw new ExpireAtException();
        }
    }

    // TODO: add qualifications, vehicles, delete phone number, find by qualifications, vehicles
}
