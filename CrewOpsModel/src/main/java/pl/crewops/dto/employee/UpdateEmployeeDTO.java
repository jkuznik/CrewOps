package pl.crewops.dto.employee;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import pl.crewops.dto.auth.RoleDTO;
import pl.crewops.dto.department.DepartmentDTO;

@Builder
public record UpdateEmployeeDTO(
        @NotNull UUID employeeId,
        @Size(max = 15) String phoneNumber,
        Set<DepartmentDTO> departments,
        Set<RoleDTO> roles,
        Boolean active) {}
