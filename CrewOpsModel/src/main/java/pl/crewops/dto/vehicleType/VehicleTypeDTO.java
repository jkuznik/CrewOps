package pl.crewops.dto.vehicleType;

import java.util.Objects;
import java.util.UUID;
import lombok.Builder;

@Builder
public record VehicleTypeDTO(UUID id, String name) {

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof VehicleTypeDTO that)) return false;
        return Objects.equals(name(), that.name());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name());
    }
}
