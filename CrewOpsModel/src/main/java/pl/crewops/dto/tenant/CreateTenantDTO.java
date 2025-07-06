package pl.crewops.dto.tenant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateTenantDTO(@Size(max = 50) @NotNull @NotBlank String name) {}
