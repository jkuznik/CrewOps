package pl.kuznik.domain.employee.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UpdateEmployeeDTO(
        @NotNull UUID employeeId, @Size(max = 15) String phoneNumber, @Size(max = 50) String department) {}
