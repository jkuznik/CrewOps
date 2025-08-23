package pl.crewops.domain.employee;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.employee.EmployeeQualificationDTO;
import pl.crewops.dto.employee.UpdateEmployeeDTO;
import pl.crewops.dto.qualification.UpdateQualificationExpiredAtDTO;
import pl.crewops.exception.domain.employee.EmployeeNotFoundException;
import pl.crewops.model.Employee;

@Validated
public interface EmployeeAPI {

    EmployeeDTO createEmployee(@NotNull @Valid CreateEmployeeDTO createEmployeeDTO);

    Employee getEmployeeById(@NotNull UUID id) throws EmployeeNotFoundException;

    EmployeeDTO getEmployeeDTOById(@NotNull UUID id) throws EmployeeNotFoundException;

    List<EmployeeDTO> getAllEmployees(int page, int size);

    List<EmployeeDTO> getEmployeesByQualification(@NotNull UUID qualificationId, int page, int size);

    List<EmployeeDTO> getEmployeesByMachines(@NotNull UUID machineId, int page, int size);

    EmployeeDTO updateEmployee(@NotNull @Valid UpdateEmployeeDTO updateEmployeeDTO) throws EmployeeNotFoundException;

    EmployeeDTO removePhoneNumber(@NotNull UUID employeeId) throws EmployeeNotFoundException;

    void deleteEmployee(@NotNull UUID employeeId) throws EmployeeNotFoundException;

    EmployeeDTO addQualification(@NotNull UUID employeeId, @NotNull UUID qualificationId)
            throws EmployeeNotFoundException;

    List<EmployeeQualificationDTO> getAllEmployeeQualificationsWithExpirationTime(@NotNull UUID employeeId);

    EmployeeDTO updateQualificationExpiredAt(
            @NotNull UUID employeeId,
            @NotNull UUID qualificationId,
            UpdateQualificationExpiredAtDTO updateQualificationExpiredAtDTO)
            throws EmployeeNotFoundException;

    void removeQualification(@NotNull UUID employeeId, @NotNull UUID qualificationId) throws EmployeeNotFoundException;

    EmployeeDTO addMachine(@NotNull UUID employeeId, @NotNull UUID machineId) throws EmployeeNotFoundException;

    void removeMachine(@NotNull UUID employeeId, @NotNull UUID machineId) throws EmployeeNotFoundException;

    List<EmployeeDTO> getAllActiveEmployees(int page, int size);

    List<EmployeeDTO> getAllActiveEmployees();
}
