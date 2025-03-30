package pl.kuznik.employee;

import org.springframework.stereotype.Component;
import pl.kuznik.employee.dto.CreateEmployeeDTO;
import pl.kuznik.entity.Employee;

@Component
public interface EmployeeAPI {

    Employee createEmployee(CreateEmployeeDTO createEmployeeDTO);
}
