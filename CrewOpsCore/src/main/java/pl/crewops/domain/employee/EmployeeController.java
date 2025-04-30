package pl.crewops.domain.employee;

import static pl.crewops.enums.ControllerURL.*;

import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.employee.UpdateEmployeeDTO;

@RestController
@Slf4j
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
        log.info("Get employees");
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.getAllEmployees(page, size));
    }

    @GetMapping(EMPLOYEES_QID)
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByQualification(
            @PathVariable(QUALIFICATION_ID) UUID qualificationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(employeeService.getEmployeesByQualification(qualificationId, page, size));
    }

    @GetMapping(EMPLOYEES_VID)
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByVehicleId(
            @PathVariable(VEHICLE_ID) UUID vehicleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.getEmployeesByVehicles(vehicleId, page, size));
    }

    @PatchMapping(EMPLOYEES_EID)
    public ResponseEntity<EmployeeDTO> updateEmployee(
            @PathVariable(EMPLOYEE_ID) UUID employeeId,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String department) {
        var updateEmployeeDTO = new UpdateEmployeeDTO(employeeId, phoneNumber, department);

        return ResponseEntity.status(HttpStatus.OK).body(employeeService.updateEmployee(updateEmployeeDTO));
    }

    @PatchMapping(EMPLOYEES_EID_PHONE)
    public ResponseEntity<EmployeeDTO> removePhoneNumber(@PathVariable(EMPLOYEE_ID) UUID employeeId) {
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.removePhoneNumber(employeeId));
    }

    @DeleteMapping(EMPLOYEES_EID)
    public ResponseEntity<Void> deleteEmployee(@PathVariable(EMPLOYEE_ID) UUID employeeId) {
        employeeService.deleteEmployee(employeeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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

    @PatchMapping(EMPLOYEES_EID_VEHICLES_VID)
    public ResponseEntity<EmployeeDTO> addEmployeeVehicles(
            @PathVariable(EMPLOYEE_ID) UUID employeeId, @PathVariable(VEHICLE_ID) UUID vehicleId) {
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.addVehicle(employeeId, vehicleId));
    }

    @DeleteMapping(EMPLOYEES_EID_VEHICLES_VID)
    public ResponseEntity<Void> removeEmployeeVehicles(
            @PathVariable(EMPLOYEE_ID) UUID employeeId, @PathVariable(VEHICLE_ID) UUID vehicleId) {
        employeeService.removeVehicle(employeeId, vehicleId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
