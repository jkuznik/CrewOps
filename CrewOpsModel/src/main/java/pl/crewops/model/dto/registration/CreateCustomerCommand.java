package pl.crewops.model.dto.registration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import pl.crewops.model.dto.employee.CreateEmployeeDTO;
import pl.crewops.model.dto.tenant.CreateTenantDTO;

@Builder
public record CreateCustomerCommand(
        @NotNull @Valid CreateTenantDTO createTenantDTO, @NotNull @Valid CreateEmployeeDTO createEmployeeDTO) {}
