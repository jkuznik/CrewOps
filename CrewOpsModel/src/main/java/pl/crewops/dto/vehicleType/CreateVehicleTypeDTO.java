package pl.crewops.dto.vehicleType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateVehicleTypeDTO(@NotNull @NotBlank String name) {}
