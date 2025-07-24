package pl.crewops.registration;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.tenant.CreateTenantDTO;

@Builder
public record CreateCustomerCommand(
        @NotNull CreateTenantDTO createTenantDTO, @NotNull CreateEmployeeDTO createEmployeeDTO) {}
