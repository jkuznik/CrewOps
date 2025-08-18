package pl.crewops.domain.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static pl.crewops.domain.message.MessageTestFactory.createMessageDTO;
import static pl.crewops.domain.message.MessageTestFactory.message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.crewops.dto.message.CreateMessageDTO;
import pl.crewops.model.Message;

@SpringJUnitConfig(classes = {MessageService.class, MessageRepository.class})
class MessageServiceTest {

    @Autowired
    private MessageService messageService;

    @MockitoBean
    private MessageRepository messageRepository;

    private CreateMessageDTO createMessageDTO;
    private Message message;

    @BeforeEach
    void setUp() {
        createMessageDTO = createMessageDTO();
        message = message();
    }

    @Test
    void createMessage_shouldReturnMessageDTO_whenCreateMessageDTOIsValid() {
        // when
        Mockito.when(messageRepository.save(any())).thenReturn(message);

        // then
        var result = messageService.createMessage(createMessageDTO);

        assertThat(result).isNotNull();
        assertThat(result.description()).isEqualTo(message.getDescription());
    }
}
