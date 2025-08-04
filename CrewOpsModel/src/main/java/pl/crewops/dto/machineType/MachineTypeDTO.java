package pl.crewops.dto.machineType;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.Builder;

@Builder
public record MachineTypeDTO(UUID id, String name) implements Serializable {

    @JsonCreator
    public static MachineTypeDTO fromString(String value) {
        MachineTypeDTO dto = MachineTypeDTO.builder().name(value).build();
        return dto;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MachineTypeDTO that)) return false;
        return Objects.equals(name(), that.name());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name());
    }
}
