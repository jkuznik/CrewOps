package pl.crewops.domain.employee;

import static pl.crewops.enums.ControllerURL.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.employee.UpdateEmployeeDTO;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
class EmployeeController {

    private final EmployeeAPI employeeAPI;

    @GetMapping(EMPLOYEES)
    public ResponseEntity<List<EmployeeDTO>> getEmployees(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "15") int size) {
        log.info("Get employees");
        return ResponseEntity.status(HttpStatus.OK).body(employeeAPI.getAllActiveEmployees(page, size));
    }

    @GetMapping(EMPLOYEES_QID)
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByQualification(
            @PathVariable(QUALIFICATION_ID) UUID qualificationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(employeeAPI.getEmployeesByQualification(qualificationId, page, size));
    }

    @GetMapping(EMPLOYEES_VID)
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByVehicleId(
            @PathVariable(VEHICLE_ID) UUID vehicleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(employeeAPI.getEmployeesByVehicles(vehicleId, page, size));
    }

    @PatchMapping(EMPLOYEES_EID)
    public ResponseEntity<EmployeeDTO> updateEmployee(
            @PathVariable(EMPLOYEE_ID) UUID employeeId, @NotNull @Valid @RequestBody UpdateEmployeeDTO updateRequest) {

        if (!updateRequest.employeeId().equals(employeeId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Path ID and body ID must match");
        }

        var updateEmployeeDTO = UpdateEmployeeDTO.builder()
                .employeeId(employeeId)
                .phoneNumber(updateRequest.phoneNumber())
                .department(updateRequest.department())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(employeeAPI.updateEmployee(updateEmployeeDTO));
    }

    @PatchMapping(EMPLOYEES_EID_PHONE)
    public ResponseEntity<EmployeeDTO> removePhoneNumber(@PathVariable(EMPLOYEE_ID) UUID employeeId) {
        return ResponseEntity.status(HttpStatus.OK).body(employeeAPI.removePhoneNumber(employeeId));
    }

    @PatchMapping(EMPLOYEES_EID_QUALIFICATIONS_QID)
    public ResponseEntity<EmployeeDTO> addEmployeeQualification(
            @PathVariable(EMPLOYEE_ID) UUID employeeId, @PathVariable(QUALIFICATION_ID) UUID qualificationId) {
        return ResponseEntity.status(HttpStatus.OK).body(employeeAPI.addQualification(employeeId, qualificationId));
    }

    @DeleteMapping(EMPLOYEES_EID_QUALIFICATIONS_QID)
    public ResponseEntity<Void> removeEmployeeQualification(
            @PathVariable(EMPLOYEE_ID) UUID employeeId, @PathVariable(QUALIFICATION_ID) UUID qualificationId) {
        employeeAPI.removeQualification(employeeId, qualificationId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping(EMPLOYEES_EID_QUALIFICATIONS_QID_EXPIRED)
    public ResponseEntity<EmployeeDTO> updateEmployeeQualification(
            @PathVariable(EMPLOYEE_ID) UUID employeeId,
            @PathVariable(QUALIFICATION_ID) UUID qualificationId,
            @NotNull @RequestBody Instant expireAt) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(employeeAPI.updateQualificationExpiredAt(employeeId, qualificationId, expireAt));
    }

    @PatchMapping(EMPLOYEES_EID_VEHICLES_VID)
    public ResponseEntity<EmployeeDTO> addEmployeeVehicles(
            @PathVariable(EMPLOYEE_ID) UUID employeeId, @PathVariable(VEHICLE_ID) UUID vehicleId) {
        return ResponseEntity.status(HttpStatus.OK).body(employeeAPI.addVehicle(employeeId, vehicleId));
    }

    @DeleteMapping(EMPLOYEES_EID_VEHICLES_VID)
    public ResponseEntity<Void> removeEmployeeVehicles(
            @PathVariable(EMPLOYEE_ID) UUID employeeId, @PathVariable(VEHICLE_ID) UUID vehicleId) {
        employeeAPI.removeVehicle(employeeId, vehicleId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
