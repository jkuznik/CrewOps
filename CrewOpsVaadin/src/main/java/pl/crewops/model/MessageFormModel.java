package pl.crewops.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
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
    // TODO: create addition wrapper class to store recipient chose option (by departments, by related machine, by
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
                .createdAt(LocalDateTime.ofInstant(messageDTO.createdAt(), ZoneId.systemDefault()))
                .read(messageDTO.isRead())
                .build();
    }

    public UUID getSenderEmployeeId() {
        return senderEmployeeId;
    }

    public class MessageFormModelComparator implements Comparator<MessageFormModel> {

        @Override
        public int compare(MessageFormModel m1, MessageFormModel m2) {
            // First, compare read status: unread (false) should come before read (true)
            if (m1.isRead() != m2.isRead()) {
                return m1.isRead() ? 1 : -1; // unread first
            }

            // If both have the same read status, compare createdAt (newest first)
            return m2.getCreatedAt().compareTo(m1.getCreatedAt());
        }
    }
}
