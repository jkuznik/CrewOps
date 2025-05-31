package pl.crewops.domain.employee;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.employee.UpdateEmployeeDTO;
import pl.crewops.exception.EmployeeNotFoundException;
import pl.crewops.model.Employee;

@Component
@Validated
public interface EmployeeAPI {

    EmployeeDTO createEmployee(@NotNull @Valid CreateEmployeeDTO createEmployeeDTO);

    Employee getEmployee(@NotNull UUID id) throws EmployeeNotFoundException;

    List<EmployeeDTO> getAllEmployees(int page, int size);

    List<EmployeeDTO> getEmployeesByQualification(@NotNull UUID qualificationId, int page, int size);

    List<EmployeeDTO> getEmployeesByVehicles(@NotNull UUID vehicleId, int page, int size);

    EmployeeDTO updateEmployee(@NotNull @Valid UpdateEmployeeDTO updateEmployeeDTO) throws EmployeeNotFoundException;

    EmployeeDTO removePhoneNumber(@NotNull UUID employeeId) throws EmployeeNotFoundException;

    void deleteEmployee(@NotNull UUID employeeId) throws EmployeeNotFoundException;

    EmployeeDTO addQualification(@NotNull UUID employeeId, @NotNull UUID qualificationId)
            throws EmployeeNotFoundException;

    EmployeeDTO updateQualificationExpiredAt(
            @NotNull UUID employeeId, @NotNull UUID qualificationId, @NotNull Instant expireAt)
            throws EmployeeNotFoundException;

    void removeQualification(@NotNull UUID employeeId, @NotNull UUID qualificationId) throws EmployeeNotFoundException;

    EmployeeDTO addVehicle(@NotNull UUID employeeId, @NotNull UUID vehicleId) throws EmployeeNotFoundException;

    void removeVehicle(@NotNull UUID employeeId, @NotNull UUID vehicleId) throws EmployeeNotFoundException;

    List<EmployeeDTO> getAllActiveEmployees(int page, int size);
}
