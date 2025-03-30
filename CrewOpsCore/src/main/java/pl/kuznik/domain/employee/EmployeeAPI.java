package pl.kuznik.domain.employee;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.kuznik.domain.employee.dto.CreateEmployeeDTO;
import pl.kuznik.entity.Employee;

@Component
@RequiredArgsConstructor
public class EmployeeAPI {

    private final EmployeeService employeeService;

    public Employee createEmployee(CreateEmployeeDTO createEmployeeDTO) {
        return employeeService.createEmployee(createEmployeeDTO);
    }
}
