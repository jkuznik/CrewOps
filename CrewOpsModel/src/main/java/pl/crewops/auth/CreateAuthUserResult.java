package pl.crewops.auth;

import lombok.Builder;
import pl.crewops.dto.employee.EmployeeDTO;

@Builder
public record CreateAuthUserResult(EmployeeDTO employeeDTO, AuthUserDTO authUserDTO) {}
