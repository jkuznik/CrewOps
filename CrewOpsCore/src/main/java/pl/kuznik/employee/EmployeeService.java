package pl.kuznik.employee;

import static pl.kuznik.employee.EmployeeMapper.mapToEntity;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kuznik.employee.dto.CreateEmployeeDTO;
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
    public Employee updateEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }
}
