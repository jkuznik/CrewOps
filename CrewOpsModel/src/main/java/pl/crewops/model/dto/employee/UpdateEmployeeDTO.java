package pl.crewops.model.dto.employee;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import pl.crewops.model.dto.auth.RoleDTO;
import pl.crewops.model.dto.department.DepartmentDTO;

@Builder
public record UpdateEmployeeDTO(
        @NotNull UUID employeeId,
        @Size(max = 15) String phoneNumber,
        Set<DepartmentDTO> departments,
        Set<RoleDTO> roles,
        Boolean active) {}
