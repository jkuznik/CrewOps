package pl.crewops.domain.message;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.IntegrationTest;
import pl.crewops.dto.message.CreateMessageDTO;
import pl.crewops.dto.message.MessageDTO;

@Transactional
class MessageAPITest extends IntegrationTest {

    @Test
    void createMessage_shouldReturnMessageDTO_whenInputDataIsValid() {
        // given
        var recipientEmployeeId = UUID.fromString("99999999-9999-9999-9999-999999999999");
        var createMessageDTO = CreateMessageDTO.builder()
                .title("title")
                .description("description")
                .recipientEmployeeId(recipientEmployeeId)
                .build();

        // when
        MessageDTO message = messageAPI.createMessage(createMessageDTO);

        // then

        assertThat(message).isNotNull();
        assertThat("description").isEqualTo(message.description());
    }

    @Test
    void getAllMessagesByRecipientEmployeeIdAndReadIsFalse() {}
}
