package pl.crewops.dto.vehicle;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import pl.crewops.enums.VehicleType;

@Builder
public record CreateVehicleDTO(
        @Size(max = 31) @NotNull String make,
        @Size(max = 31) @NotNull String model,
        @NotNull VehicleType type,
        @NotNull Integer year,
        @Size(max = 50) String vin,
        @Size(max = 15) String registerNumber,
        @NotNull Boolean broken) {}
