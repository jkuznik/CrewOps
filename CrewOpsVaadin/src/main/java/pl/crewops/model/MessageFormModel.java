package pl.crewops.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import pl.crewops.dto.message.MessageDTO;
import pl.crewops.dto.message.RecipientSelection;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageFormModel {
    private UUID id;
    private String title;
    private @NotNull @Size(max = 32767, message = "Message can not be longer than 32767 characters") String description;
    // TODO: create addition wrapper class to store recipient chose option (by department, by related machine, by
    // employeeId)
    private @NotNull RecipientSelection recipientSelection;
    private UUID senderEmployeeId;
    private @NotNull LocalDateTime createdAt;
    private boolean read;

    public static MessageFormModel toMessageFormModel(MessageDTO messageDTO) {
        return MessageFormModel.builder()
                .id(messageDTO.id())
                .title(messageDTO.title())
                .description(messageDTO.description())
                .recipientSelection(new RecipientSelection(
                        RecipientSelection.RecipientOptionType.EMPLOYEE,
                        messageDTO.recipientEmployeeId().toString()))
                .senderEmployeeId(messageDTO.senderEmployeeId())
                .createdAt(LocalDateTime.now())
                .read(messageDTO.isRead())
                .build();
    }

    public UUID getSenderEmployeeId() {
        return senderEmployeeId;
    }
}
