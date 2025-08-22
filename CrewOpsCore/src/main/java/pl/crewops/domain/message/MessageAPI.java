package pl.crewops.domain.message;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;
import pl.crewops.dto.message.CreateMessageDTO;
import pl.crewops.dto.message.MessageDTO;

@Validated
public interface MessageAPI {

    MessageDTO createMessage(@NotNull @Valid CreateMessageDTO createMessageDTO);

    List<MessageDTO> getAllMessagesByRecipientEmployeeIdAndReadIsFalse(UUID recipientEmployeeId, int page, int size);
}
