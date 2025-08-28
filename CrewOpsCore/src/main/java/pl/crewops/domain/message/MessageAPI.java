package pl.crewops.domain.message;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;
import pl.crewops.dto.message.CreateMessageDTO;
import pl.crewops.dto.message.MessageDTO;
import pl.crewops.dto.message.SendMessageCommand;

@Validated
public interface MessageAPI {

    MessageDTO createMessage(@NotNull @Valid CreateMessageDTO createMessageDTO);

    void sendMessage(@NotNull @Valid SendMessageCommand sendMessageCommand);
    // todo: implement logic to separately fetch read and unread messages, for now fetch all
    List<MessageDTO> getAllMessagesByRecipientEmployeeIdAndReadIsFalse(UUID recipientEmployeeId, int page, int size);

    List<MessageDTO> getAllMessagesByRecipientEmployeeId(UUID recipientEmployeeId, int page, int size);

    MessageDTO setMessageReadStatus(@NotNull UUID messageId, boolean read);
}
