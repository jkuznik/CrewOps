package pl.crewops.dto.vehicleType;

import java.util.UUID;
import lombok.Builder;

@Builder
public record VehicleTypeDTO(UUID id, String name) {}
