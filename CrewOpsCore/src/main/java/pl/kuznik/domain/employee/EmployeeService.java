package pl.kuznik.domain.employee;

import static pl.kuznik.domain.employee.EmployeeMapper.mapToDTO;
import static pl.kuznik.domain.employee.EmployeeMapper.mapToEntity;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pl.kuznik.domain.employee.dto.CreateEmployeeDTO;
import pl.kuznik.domain.employee.dto.EmployeeDTO;
import pl.kuznik.domain.employee.dto.UpdateEmployeeDTO;
import pl.kuznik.entity.Employee;
import pl.kuznik.exception.EmployeeNotFoundException;

@Service
@RequiredArgsConstructor
@Validated
class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeDTO createEmployee(@Valid CreateEmployeeDTO createEmployeeDTO) {
        return mapToDTO(employeeRepository.save(mapToEntity(createEmployeeDTO)));
    }

    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(EmployeeMapper::mapToDTO)
                .toList();
    }

    @Transactional
    public EmployeeDTO updateEmployee(@Valid UpdateEmployeeDTO updateEmployeeDTO) {
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
}
