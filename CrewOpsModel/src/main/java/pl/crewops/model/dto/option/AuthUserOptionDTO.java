package pl.crewops.model.dto.option;

import java.util.Objects;
import java.util.UUID;
import lombok.Builder;

@Builder
public record AuthUserOptionDTO(UUID employeeId, UUID optionId, String name, boolean enabled) {

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AuthUserOptionDTO that)) return false;
        return Objects.equals(name(), that.name());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name());
    }
}
