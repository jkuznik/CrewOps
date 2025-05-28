package pl.crewops.dto.vehicle;

import java.util.UUID;
import lombok.Builder;
import pl.crewops.dto.vehicleType.VehicleTypeDTO;

@Builder
public record VehicleDTO(
        UUID id,
        String make,
        String model,
        VehicleTypeDTO vehicleType,
        Integer year,
        String vin,
        String registerNumber,
        Boolean broken) {}
