package pl.crewops.model.dto.employee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import pl.crewops.model.dto.auth.RoleDTO;
import pl.crewops.model.dto.department.DepartmentDTO;

/**
 * Property UUID creatorEmployeeId is required for sending notifications to those managers
 * who just add new employee. Include that information allow to avoid ask about current authentication
 * from SpringSecurityHolder what is kind of optimisation and simplify testing.
 * */
@Builder
public record CreateEmployeeDTO(
        UUID creatorEmployeeId,
        @Size(max = 50) @NotNull @NotBlank String firstName,
        @Size(max = 50) @NotNull @NotBlank String lastName,
        @NotNull LocalDate birthDate,
        @Size(max = 15) String phoneNumber,
        Set<DepartmentDTO> departments,
        @NotNull UUID companyId,
        Set<RoleDTO> roles) {}
