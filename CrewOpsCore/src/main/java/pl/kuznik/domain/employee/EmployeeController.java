package pl.kuznik.domain.employee;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kuznik.domain.employee.dto.CreateEmployeeDTO;
import pl.kuznik.domain.employee.dto.EmployeeDTO;
import pl.kuznik.domain.employee.dto.UpdateEmployeeDTO;

@RestController
@RequestMapping("employees")
@RequiredArgsConstructor
class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("create")
    public ResponseEntity<EmployeeDTO> createEmployee(@Valid @RequestBody CreateEmployeeDTO createEmployeeDTO) {

        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.createEmployee(createEmployeeDTO));
    }

    @PatchMapping("update/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(
            @PathVariable("id") UUID id, @RequestParam String phoneNumber, @RequestParam String department) {
        var updateEmployeeDTO = new UpdateEmployeeDTO(id, phoneNumber, department);

        return ResponseEntity.status(HttpStatus.OK).body(employeeService.updateEmployee(updateEmployeeDTO));
    }
}
