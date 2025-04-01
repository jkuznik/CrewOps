package pl.kuznik.domain.employee;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.kuznik.domain.employee.dto.CreateEmployeeDTO;
import pl.kuznik.domain.employee.dto.EmployeeDTO;
import pl.kuznik.domain.employee.dto.UpdateEmployeeDTO;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmployeeAPI {

    private final EmployeeService employeeService;

    public EmployeeDTO createEmployee(CreateEmployeeDTO createEmployeeDTO) {
        return employeeService.createEmployee(createEmployeeDTO);
    }

    public List<EmployeeDTO> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    public EmployeeDTO updateEmployee(UpdateEmployeeDTO updateEmployeeDTO) {
        return employeeService.updateEmployee(updateEmployeeDTO);
    }
}
