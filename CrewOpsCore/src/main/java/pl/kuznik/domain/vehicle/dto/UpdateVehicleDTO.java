package pl.kuznik.domain.vehicle.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UpdateVehicleDTO(@NotNull UUID vehicleId, @Size(max = 15) String registerNumber, Boolean broken) {}
