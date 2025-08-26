package pl.crewops.dto.employee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import pl.crewops.dto.auth.RoleDTO;
import pl.crewops.dto.department.DepartmentDTO;

@Builder
public record CreateEmployeeDTO(
        @Size(max = 50) @NotNull @NotBlank String firstName,
        @Size(max = 50) @NotNull @NotBlank String lastName,
        @NotNull LocalDate birthDate,
        @Size(max = 15) String phoneNumber,
        Set<DepartmentDTO> departments,
        @NotNull UUID companyId,
        Set<RoleDTO> roles) {}
