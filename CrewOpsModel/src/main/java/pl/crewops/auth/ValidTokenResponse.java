package pl.crewops.auth;

import java.util.Date;
import lombok.Builder;
import pl.crewops.dto.employee.EmployeeDTO;

@Builder
// TODO: update application to https
public record ValidTokenResponse(Boolean valid, Date expiration, EmployeeDTO employeeDTO) {}
