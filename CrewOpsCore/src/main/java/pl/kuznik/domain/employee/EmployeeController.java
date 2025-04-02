package pl.kuznik.domain.employee;

import static pl.kuznik.utils.enums.ControllerURL.*;

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

@RestController
@RequiredArgsConstructor
class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping(EMPLOYEES)
    public ResponseEntity<EmployeeDTO> createEmployee(@Valid @RequestBody CreateEmployeeDTO createEmployeeDTO) {

        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.createEmployee(createEmployeeDTO));
    }

    @GetMapping(EMPLOYEES)
    public ResponseEntity<List<EmployeeDTO>> getEmployees(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.getAllEmployees(page, size));
    }

    @PatchMapping(EMPLOYEES_EID)
    public ResponseEntity<EmployeeDTO> updateEmployee(
            @PathVariable(EMPLOYEE_ID) UUID employeeId,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String department) {
        var updateEmployeeDTO = new UpdateEmployeeDTO(employeeId, phoneNumber, department);

        return ResponseEntity.status(HttpStatus.OK).body(employeeService.updateEmployee(updateEmployeeDTO));
    }

    @PatchMapping(EMPLOYEES_EID_QUALIFICATIONS_QID)
    public ResponseEntity<EmployeeDTO> addEmployeeQualification(
            @PathVariable(EMPLOYEE_ID) UUID employeeId, @PathVariable(QUALIFICATION_ID) UUID qualificationId) {
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.addQualification(employeeId, qualificationId));
    }

    @DeleteMapping(EMPLOYEES_EID_QUALIFICATIONS_QID)
    public ResponseEntity<Void> removeEmployeeQualification(
            @PathVariable(EMPLOYEE_ID) UUID employeeId, @PathVariable(QUALIFICATION_ID) UUID qualificationId) {
        employeeService.removeQualification(employeeId, qualificationId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping(EMPLOYEES_EID_QUALIFICATIONS_QID_EXPIRED)
    public ResponseEntity<EmployeeDTO> updateEmployeeQualification(
            @PathVariable(EMPLOYEE_ID) UUID employeeId,
            @PathVariable(QUALIFICATION_ID) UUID qualificationId,
            @RequestBody Instant expireAt) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(employeeService.updateQualificationExpiredAt(employeeId, qualificationId, expireAt));
    }
}
