package pl.crewops.dto.qualification;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.Builder;

@Builder
public record QualificationDTO(UUID id, String description, Instant expiredAt, Integer employeesAmount)
        implements Serializable {
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof QualificationDTO that)) return false;
        return Objects.equals(description(), that.description());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(description());
    }
}
