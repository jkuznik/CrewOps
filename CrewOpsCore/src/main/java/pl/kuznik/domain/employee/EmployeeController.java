package pl.kuznik.domain.employee;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kuznik.domain.employee.dto.CreateEmployeeDTO;
import pl.kuznik.entity.Employee;
import pl.kuznik.entity.Qualification;
import pl.kuznik.entity.Vehicle;

@RestController
@RequestMapping("employees")
@RequiredArgsConstructor
class EmployeeController {

    private final EmployeeService employeeService;

    @PatchMapping("/create")
    public ResponseEntity<Employee> createEmployee(
            @Valid @RequestBody CreateEmployeeDTO createEmployeeDTO) {

        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.createEmployee(createEmployeeDTO));
    }
}
