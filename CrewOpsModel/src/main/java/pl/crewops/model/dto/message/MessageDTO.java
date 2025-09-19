package pl.crewops.model.dto.message;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record MessageDTO(
        UUID id,
        String title,
        @NotNull String description,
        @NotNull UUID recipientEmployeeId,
        UUID senderEmployeeId,
        Instant createdAt,
        boolean isRead) {}
