package pl.kuznik.domain.employee;

import static pl.kuznik.domain.employee.EmployeeMapper.mapToEntity;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kuznik.domain.employee.dto.CreateEmployeeDTO;
import pl.kuznik.domain.employee.dto.UpdateEmployeeDTO;
import pl.kuznik.entity.Employee;

@Service
@RequiredArgsConstructor
class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public Employee createEmployee(CreateEmployeeDTO createEmployeeDTO) {
        return employeeRepository.save(mapToEntity(createEmployeeDTO));
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Transactional
    public Employee updateEmployee(UpdateEmployeeDTO updateEmployeeDTO) {
        Employee employee = employeeRepository
                .findById(updateEmployeeDTO.employeeId())
                // TODO: custom exception
                .orElseThrow(RuntimeException::new);

        if (updateEmployeeDTO.phoneNumber() != null) {
            employee.setPhoneNumber(updateEmployeeDTO.phoneNumber());
        }
        if (updateEmployeeDTO.department() != null) {
            employee.setDepartment(updateEmployeeDTO.department());
        }

        return employee;
    }
}
