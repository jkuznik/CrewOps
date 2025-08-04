package pl.crewops.dto.machine;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.Builder;
import pl.crewops.dto.machineType.MachineTypeDTO;

@Builder
public record MachineDTO(
        UUID id,
        String make,
        String model,
        MachineTypeDTO machineType,
        Integer year,
        String vin,
        String registerNumber,
        Boolean broken)
        implements Serializable {
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MachineDTO that)) return false;
        return Objects.equals(id(), that.id());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id());
    }
}
