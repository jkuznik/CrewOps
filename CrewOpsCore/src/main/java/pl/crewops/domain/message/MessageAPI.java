package pl.crewops.domain.message;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import pl.crewops.dto.message.CreateMessageDTO;
import pl.crewops.dto.message.MessageDTO;

@Validated
public interface MessageAPI {

    MessageDTO createMessage(@NotNull @Valid CreateMessageDTO createMessageDTO);
}
