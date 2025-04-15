package pl.crewops.dto.vehicle;

import java.util.UUID;
import lombok.Builder;
import pl.crewops.enums.VehicleType;

@Builder
public record VehicleDTO(
        UUID id,
        String make,
        String model,
        VehicleType vehicleType,
        Integer year,
        String vin,
        String registerNumber,
        Boolean broken) {}
