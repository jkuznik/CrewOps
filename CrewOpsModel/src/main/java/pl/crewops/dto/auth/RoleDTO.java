package pl.crewops.dto.auth;

import java.io.Serializable;
import java.util.Objects;
import lombok.Builder;

@Builder
public record RoleDTO(String name) implements Serializable {
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RoleDTO roleDTO)) return false;
        return Objects.equals(name(), roleDTO.name());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name());
    }
}
