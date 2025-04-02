package pl.kuznik.domain.employee;

import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kuznik.domain.employee.dto.CreateEmployeeDTO;
import pl.kuznik.domain.employee.dto.EmployeeDTO;
import pl.kuznik.domain.employee.dto.UpdateEmployeeDTO;
import pl.kuznik.utils.enums.ControllerURL;

@RestController
@RequestMapping(ControllerURL.EMPLOYEES)
@RequiredArgsConstructor
class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping()
    public ResponseEntity<EmployeeDTO> createEmployee(@Valid @RequestBody CreateEmployeeDTO createEmployeeDTO) {

        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.createEmployee(createEmployeeDTO));
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getEmployees() {
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.getAllEmployees());
    }

    @PatchMapping("{employeeId}")
    public ResponseEntity<EmployeeDTO> updateEmployee(
            @PathVariable("employeeId") UUID employeeId,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String department) {
        var updateEmployeeDTO = new UpdateEmployeeDTO(employeeId, phoneNumber, department);

        return ResponseEntity.status(HttpStatus.OK).body(employeeService.updateEmployee(updateEmployeeDTO));
    }

    @PatchMapping("{employeeId}/qaulifications/{qualificationId}")
    public ResponseEntity<EmployeeDTO> addEmployeeQaulification(
            @PathVariable("employeeId") UUID employeeId, @PathVariable("qualificationId") UUID qualificationId) {
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.addQualification(employeeId, qualificationId));
    }

    @PatchMapping("{employeeId}/qaulifications/{qualificationId}/expire")
    public ResponseEntity<EmployeeDTO> updateEmployeeQaulification(
            @PathVariable("employeeId") UUID employeeId,
            @PathVariable("qualificationId") UUID qualificationId,
            @RequestBody Instant expireAt) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(employeeService.updateQualificationExpiredAt(employeeId, qualificationId, expireAt));
    }
}
