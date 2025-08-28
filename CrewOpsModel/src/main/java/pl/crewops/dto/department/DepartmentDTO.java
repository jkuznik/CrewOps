package pl.crewops.dto.department;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.Builder;

@Builder
public record DepartmentDTO(UUID id, String name) implements Serializable {

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DepartmentDTO that)) return false;
        return Objects.equals(name(), that.name());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name());
    }
}
