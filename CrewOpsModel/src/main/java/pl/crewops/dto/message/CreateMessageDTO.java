package pl.crewops.dto.message;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CreateMessageDTO(
        @Size(max = 255) String title,
        @NotNull @Size(min = 2, max = 32767) String description,
        @NotNull UUID recipientEmployeeId,
        UUID senderEmployeeId) {}
