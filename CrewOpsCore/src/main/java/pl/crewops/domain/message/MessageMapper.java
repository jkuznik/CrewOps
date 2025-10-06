package pl.crewops.domain.message;

import pl.crewops.model.dto.message.CreateMessageDTO;
import pl.crewops.model.dto.message.MessageDTO;
import pl.crewops.model.tenantSchema.Message;

class MessageMapper {

    public static Message mapToEntity(CreateMessageDTO createMessageDTO) {
        return Message.builder()
                .title(createMessageDTO.title())
                .description(createMessageDTO.description())
                .recipientEmployeeId(createMessageDTO.recipientEmployeeId())
                .senderEmployeeId(createMessageDTO.senderEmployeeId())
                .build();
    }

    public static MessageDTO mapToDTO(Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .title(message.getTitle())
                .description(message.getDescription())
                .recipientEmployeeId(message.getRecipientEmployeeId())
                .senderEmployeeId(message.getSenderEmployeeId())
                .isRead(message.isRead())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
