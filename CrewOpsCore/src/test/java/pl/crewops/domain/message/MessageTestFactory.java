package pl.crewops.domain.message;

import java.util.UUID;
import pl.crewops.dto.message.CreateMessageDTO;
import pl.crewops.model.Message;

class MessageTestFactory {

    private static final UUID messageId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID recipientEmployeeId = UUID.fromString("21111111-1111-1111-1111-111111111111");
    private static final UUID senderEmployeeId = UUID.fromString("31111111-1111-1111-1111-111111111111");

    public static CreateMessageDTO createMessageDTO() {
        return CreateMessageDTO.builder()
                .title("title")
                .description("description")
                .recipientEmployeeId(recipientEmployeeId)
                .senderEmployeeId(senderEmployeeId)
                .build();
    }

    public static Message message() {
        var message = Message.builder()
                .title("title")
                .description("description")
                .recipientEmployeeId(recipientEmployeeId)
                .senderEmployeeId(senderEmployeeId)
                .read(false)
                .build();

        message.setId(messageId);

        return message;
    }
}
