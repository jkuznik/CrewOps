package pl.crewops.model.dto.note;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import lombok.Builder;
import pl.crewops.enums.NoteType;

@Builder
public record NoteDTO(
        UUID id,
        LocalDate date,
        UUID reportedByEmployeeId,
        NoteType type,
        String content,
        Instant createdAt,
        Instant updatedAt)
        implements Serializable {

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NoteDTO noteDTO)) return false;
        return Objects.equals(id(), noteDTO.id());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id());
    }
}
