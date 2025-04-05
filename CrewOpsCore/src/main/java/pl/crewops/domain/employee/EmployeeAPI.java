package pl.crewops.domain.employee;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.employee.UpdateEmployeeDTO;

@Component
@RequiredArgsConstructor
public class EmployeeAPI {

    private final EmployeeService employeeService;

    public EmployeeDTO createEmployee(CreateEmployeeDTO createEmployeeDTO) {
        return employeeService.createEmployee(createEmployeeDTO);
    }

    public EmployeeDTO updateEmployee(UpdateEmployeeDTO updateEmployeeDTO) {
        return employeeService.updateEmployee(updateEmployeeDTO);
    }
}
