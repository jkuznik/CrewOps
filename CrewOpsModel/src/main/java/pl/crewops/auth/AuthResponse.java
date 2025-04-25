package pl.crewops.auth;

import jakarta.validation.constraints.NotNull;
import java.util.Date;
import pl.crewops.dto.employee.EmployeeDTO;

public record AuthResponse(
        @NotNull String token, @NotNull String username, @NotNull EmployeeDTO employeeDTO, @NotNull Date expiresAt) {}
