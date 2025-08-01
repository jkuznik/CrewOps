package pl.crewops.dto.vehicle;

import java.io.Serializable;
import java.util.Objects;
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
        Boolean broken)
        implements Serializable {
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof VehicleDTO that)) return false;
        return Objects.equals(id(), that.id());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id());
    }
}
