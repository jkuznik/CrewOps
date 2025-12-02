package pl.crewops.model.dto.shift;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ShiftDTO(UUID id, String name, Set<ShiftConfig> shiftConfigs, String color) {

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ShiftDTO shiftDTO)) return false;
        return Objects.equals(id(), shiftDTO.id());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id());
    }
}
