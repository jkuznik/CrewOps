package pl.crewops.model.dto.machineType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateMachineTypeDTO(@NotNull @NotBlank String name) {}
