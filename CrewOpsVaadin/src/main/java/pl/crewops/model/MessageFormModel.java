package pl.crewops.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import pl.crewops.dto.message.MessageDTO;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageFormModel {
    private UUID id;
    private String title;
    private @NotNull @Size(max = 32767, message = "Message can not be longer than 32767 characters") String description;
    private @NotNull UUID recipientEmployeeId;
    private UUID senderEmployeeId;
    private @NotNull LocalDateTime createdAt;
    private boolean read;

    public static MessageFormModel toMessageFormModel(MessageDTO messageDTO) {
        return MessageFormModel.builder()
                .id(messageDTO.id())
                .title(messageDTO.title())
                .description(messageDTO.description())
                .recipientEmployeeId(messageDTO.recipientEmployeeId())
                .senderEmployeeId(messageDTO.senderEmployeeId())
                .createdAt(LocalDateTime.now())
                .read(messageDTO.isRead())
                .build();
    }

    public UUID getSenderEmployeeId() {
        return senderEmployeeId;
    }
}
