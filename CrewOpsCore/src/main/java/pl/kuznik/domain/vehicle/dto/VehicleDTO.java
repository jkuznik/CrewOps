package pl.kuznik.domain.vehicle.dto;

import java.util.UUID;
import lombok.Builder;
import pl.kuznik.utils.enums.VehicleType;

@Builder
public record VehicleDTO(
        UUID id,
        String make,
        String model,
        VehicleType vehicleTyp,
        Integer year,
        String vin,
        String registerNumber,
        Boolean broken) {}
