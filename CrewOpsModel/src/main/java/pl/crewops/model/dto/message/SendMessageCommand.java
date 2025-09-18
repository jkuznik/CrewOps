package pl.crewops.model.dto.message;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Builder;

@Builder
public record SendMessageCommand(
        @Size(max = 255) String title,
        @NotNull @Size(min = 2, max = 32767) String description,
        @NotNull RecipientSelection recipientSelection,
        UUID senderEmployeeId) {}
