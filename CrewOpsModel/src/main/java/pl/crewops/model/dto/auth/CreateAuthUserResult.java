package pl.crewops.model.dto.auth;

import lombok.Builder;
import pl.crewops.model.dto.employee.EmployeeDTO;

@Builder
public record CreateAuthUserResult(EmployeeDTO employeeDTO, AuthUserDTO authUserDTO, String plainPassword) {}
