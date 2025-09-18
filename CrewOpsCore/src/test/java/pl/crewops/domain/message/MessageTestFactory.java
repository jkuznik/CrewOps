package pl.crewops.domain.message;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import pl.crewops.model.Message;
import pl.crewops.model.dto.message.CreateMessageDTO;
import pl.crewops.model.dto.message.RecipientSelection;
import pl.crewops.model.dto.message.SendMessageCommand;

class MessageTestFactory {

    public static final UUID messageId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public static final UUID recipientEmployeeId = UUID.fromString("21111111-1111-1111-1111-111111111111");
    public static final UUID senderEmployeeId = UUID.fromString("31111111-1111-1111-1111-111111111111");

    public static SendMessageCommand sendMessageCommandAll() {
        return new SendMessageCommand(
                "subject",
                "description",
                new RecipientSelection(RecipientSelection.RecipientOptionType.ALL, null),
                UUID.randomUUID());
    }

    public static SendMessageCommand sendMessageCommandMachine() {
        return new SendMessageCommand(
                "subject",
                "description",
                new RecipientSelection(
                        RecipientSelection.RecipientOptionType.MACHINE,
                        UUID.randomUUID().toString()),
                UUID.randomUUID());
    }

    public static SendMessageCommand sendMessageCommandEmployee() {
        return new SendMessageCommand(
                "subject",
                "description",
                new RecipientSelection(RecipientSelection.RecipientOptionType.EMPLOYEE, recipientEmployeeId.toString()),
                UUID.randomUUID());
    }

    public static CreateMessageDTO createMessageDTO() {
        return CreateMessageDTO.builder()
                .title("subject")
                .description("description")
                .recipientEmployeeId(recipientEmployeeId)
                .senderEmployeeId(senderEmployeeId)
                .build();
    }

    public static CreateMessageDTO notValidCreateMessageDTO() {
        return CreateMessageDTO.builder()
                .title("subject")
                .description(null) // there is an constraint that not allow to this value is null
                .recipientEmployeeId(recipientEmployeeId)
                .senderEmployeeId(senderEmployeeId)
                .build();
    }

    public static Message message() {
        var message = Message.builder()
                .title("subject")
                .description("description")
                .recipientEmployeeId(recipientEmployeeId)
                .senderEmployeeId(senderEmployeeId)
                .read(false)
                .build();

        message.setId(messageId);

        return message;
    }

    public static Page<Message> messageSet() {
        var messages = List.of(
                Message.builder()
                        .title("subject")
                        .description("description")
                        .recipientEmployeeId(recipientEmployeeId)
                        .senderEmployeeId(senderEmployeeId)
                        .read(false)
                        .build(),
                Message.builder()
                        .title("title2")
                        .description("description2")
                        .recipientEmployeeId(recipientEmployeeId)
                        .senderEmployeeId(senderEmployeeId)
                        .read(false)
                        .build());

        return new PageImpl<>(messages);
    }
}
