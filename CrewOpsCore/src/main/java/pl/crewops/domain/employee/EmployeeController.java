package pl.crewops.domain.employee;

import static pl.crewops.enums.ControllerURL.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.employee.EmployeeQualificationDTO;
import pl.crewops.dto.employee.UpdateEmployeeDTO;
import pl.crewops.dto.qualification.UpdateQualificationExpiredAtDTO;
import pl.crewops.security.custom.permissionAnnotation.ManagerPermission;

@ActiveProfiles("test")
@RestController
@RequiredArgsConstructor
@Validated
class EmployeeController {

    private final EmployeeAPI employeeAPI;

    @GetMapping(EMPLOYEES)
    public ResponseEntity<List<EmployeeDTO>> getEmployees(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(employeeAPI.getAllActiveEmployees(page, size));
    }

    @GetMapping(EMPLOYEES_EID)
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable(EMPLOYEE_ID) UUID employeeId) {
        return ResponseEntity.status(HttpStatus.OK).body(employeeAPI.getEmployeeDTOById(employeeId));
    }

    @GetMapping(QUALIFICATIONS_QID_EMPLOYEES)
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByQualification(
            @PathVariable(QUALIFICATION_ID) UUID qualificationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(employeeAPI.getEmployeesByQualification(qualificationId, page, size));
    }

    @GetMapping(MACHINES_VID_EMPLOYEES)
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByMachineId(
            @PathVariable(MACHINE_ID) UUID machineId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(employeeAPI.getEmployeesByMachines(machineId, page, size));
    }

    @PatchMapping(EMPLOYEES_EID)
    @ManagerPermission
    public ResponseEntity<EmployeeDTO> updateEmployee(
            @PathVariable(EMPLOYEE_ID) UUID employeeId, @NotNull @Valid @RequestBody UpdateEmployeeDTO updateRequest) {

        if (!updateRequest.employeeId().equals(employeeId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Path ID and body ID must match");
        }

        var updateEmployeeDTO = UpdateEmployeeDTO.builder()
                .employeeId(employeeId)
                .phoneNumber(updateRequest.phoneNumber())
                .departments(updateRequest.departments())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(employeeAPI.updateEmployee(updateEmployeeDTO));
    }

    @PatchMapping(EMPLOYEES_EID_PHONE)
    public ResponseEntity<EmployeeDTO> removePhoneNumber(@PathVariable(EMPLOYEE_ID) UUID employeeId) {
        return ResponseEntity.status(HttpStatus.OK).body(employeeAPI.removePhoneNumber(employeeId));
    }

    @GetMapping(EMPLOYEES_EID_QUALIFICATIONS_EXPIRED)
    @ManagerPermission
    public ResponseEntity<List<EmployeeQualificationDTO>> getEmployeeQualificationWithExpirationTime(
            @PathVariable(EMPLOYEE_ID) UUID employeeId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(employeeAPI.getAllEmployeeQualificationsWithExpirationTime(employeeId));
    }

    @PatchMapping(EMPLOYEES_EID_QUALIFICATIONS_QID)
    public ResponseEntity<EmployeeDTO> addEmployeeQualification(
            @PathVariable(EMPLOYEE_ID) UUID employeeId, @PathVariable(QUALIFICATION_ID) UUID qualificationId) {
        return ResponseEntity.status(HttpStatus.OK).body(employeeAPI.addQualification(employeeId, qualificationId));
    }

    @DeleteMapping(EMPLOYEES_EID_QUALIFICATIONS_QID)
    @ManagerPermission
    public ResponseEntity<Void> removeEmployeeQualification(
            @PathVariable(EMPLOYEE_ID) UUID employeeId, @PathVariable(QUALIFICATION_ID) UUID qualificationId) {
        employeeAPI.removeQualification(employeeId, qualificationId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping(EMPLOYEES_EID_QUALIFICATIONS_QID_EXPIRED)
    public ResponseEntity<EmployeeDTO> updateEmployeeQualification(
            @PathVariable(EMPLOYEE_ID) UUID employeeId,
            @PathVariable(QUALIFICATION_ID) UUID qualificationId,
            @RequestBody UpdateQualificationExpiredAtDTO updateQualificationExpiredAtDTO) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(employeeAPI.updateQualificationExpiredAt(
                        employeeId, qualificationId, updateQualificationExpiredAtDTO));
    }

    @PatchMapping(EMPLOYEES_EID_MACHINES_VID)
    public ResponseEntity<EmployeeDTO> addEmployeeMachines(
            @PathVariable(EMPLOYEE_ID) UUID employeeId, @PathVariable(MACHINE_ID) UUID machineId) {
        return ResponseEntity.status(HttpStatus.OK).body(employeeAPI.addMachine(employeeId, machineId));
    }

    @DeleteMapping(EMPLOYEES_EID_MACHINES_VID)
    public ResponseEntity<Void> removeEmployeeMachines(
            @PathVariable(EMPLOYEE_ID) UUID employeeId, @PathVariable(MACHINE_ID) UUID machineId) {
        employeeAPI.removeMachine(employeeId, machineId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping(EMPLOYEES_EID_DEPARTMENTS_DID)
    public ResponseEntity<EmployeeDTO> addEmployeeDepartments(
            @PathVariable(EMPLOYEE_ID) UUID employeeId, @PathVariable(DEPARTMENT_ID) UUID departmentId) {
        return ResponseEntity.status(HttpStatus.OK).body(employeeAPI.addDepartment(employeeId, departmentId));
    }

    @DeleteMapping(EMPLOYEES_EID_DEPARTMENTS_DID)
    public ResponseEntity<Void> removeEmployeeDepartments(
            @PathVariable(EMPLOYEE_ID) UUID employeeId, @PathVariable(DEPARTMENT_ID) UUID departmentId) {
        employeeAPI.removeDepartment(employeeId, departmentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
