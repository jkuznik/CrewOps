package pl.kuznik.employee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kuznik.employee.dto.CreateEmployeeDTO;
import pl.kuznik.entity.Employee;
import pl.kuznik.entity.Qualification;
import pl.kuznik.entity.Vehicle;

@RestController
@RequestMapping("employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PatchMapping("/create")
    public ResponseEntity<Employee> createEmployee(
            @RequestParam @NotNull @NotBlank String firstName,
            @RequestParam @NotNull @NotBlank String lastName,
            @RequestParam @NotNull LocalDate birthDate,
            @RequestParam String phoneNumber,
            @RequestParam @NotNull @NotBlank String department,
            @RequestBody Set<Qualification> qualifications,
            @RequestBody Set<Vehicle> vehicles) {
        var createEmployeeDTO = CreateEmployeeDTO.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birthDate(birthDate)
                .phoneNumber(phoneNumber)
                .department(department)
                .qualifications(qualifications)
                .vehicles(vehicles)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.createEmployee(createEmployeeDTO));
    }
}
