package pl.crewops.model.dto.qualification;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UpdateQualificationExpiredAtDTO(
        @NotNull UUID employeeId, @NotNull UUID qualificationId, Instant expiredAt) {}
