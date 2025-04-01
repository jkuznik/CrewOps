package pl.kuznik.domain.employee;

import jakarta.validation.Valid;
import java.util.List;
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

    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getEmployees() {
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.getAllEmployees());
    }

    @PatchMapping("update/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(
            @PathVariable("id") UUID id,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String department) {
        var updateEmployeeDTO = new UpdateEmployeeDTO(id, phoneNumber, department);

        return ResponseEntity.status(HttpStatus.OK).body(employeeService.updateEmployee(updateEmployeeDTO));
    }
}
