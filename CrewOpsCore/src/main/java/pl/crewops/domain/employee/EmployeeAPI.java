package pl.crewops.domain.employee;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.employee.UpdateEmployeeDTO;
import pl.crewops.exception.EmployeeNotFoundException;
import pl.crewops.model.Employee;

@Component
@RequiredArgsConstructor
public class EmployeeAPI {

    private final EmployeeService employeeService;

    public EmployeeDTO createEmployee(CreateEmployeeDTO createEmployeeDTO) {
        return employeeService.createEmployee(createEmployeeDTO);
    }

    public Employee getEmployee(UUID id) throws EmployeeNotFoundException {
        return employeeService.getEmployeeById(id);
    }

    public EmployeeDTO updateEmployee(UpdateEmployeeDTO updateEmployeeDTO) throws EmployeeNotFoundException {
        return employeeService.updateEmployee(updateEmployeeDTO);
    }
}
