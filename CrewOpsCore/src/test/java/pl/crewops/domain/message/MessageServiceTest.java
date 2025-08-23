package pl.crewops.domain.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static pl.crewops.domain.message.MessageTestFactory.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.crewops.domain.employee.EmployeeAPI;
import pl.crewops.dto.message.CreateMessageDTO;
import pl.crewops.dto.message.MessageDTO;
import pl.crewops.model.Message;

@SpringJUnitConfig(classes = {MessageService.class, MessageRepository.class, EmployeeAPI.class})
class MessageServiceTest {

    @Autowired
    private MessageService messageService;

    @MockitoBean
    private MessageRepository messageRepository;

    @MockitoBean
    private EmployeeAPI employeeAPI;

    private CreateMessageDTO createMessageDTO;
    private Message message;
    private Page<Message> messageSet;

    @BeforeEach
    void setUp() {
        createMessageDTO = createMessageDTO();
        message = message();
        messageSet = messageSet();
        ;
    }

    @Test
    void createMessage_shouldReturnMessageDTO_whenCreateMessageDTOIsValid() {
        // when
        when(messageRepository.save(any())).thenReturn(message);

        // then
        var result = messageService.createMessage(createMessageDTO);

        assertThat(result).isNotNull();
        assertThat(result.description()).isEqualTo(message.getDescription());
    }

    @Test
    void getAllMessagesByRecipientEmployeeIdAndReadIsFalse_shouldReturnCollectionOfUnreadMessagesDTO() {
        // when
        when(messageRepository.findAllByRecipientEmployeeIdAndReadIsFalse(any(), any()))
                .thenReturn(messageSet);

        // then
        List<MessageDTO> result =
                messageService.getAllMessagesByRecipientEmployeeIdAndReadIsFalse(recipientEmployeeId, 0, 15);

        assertThat(result).hasSize(2);
        assertThat("title").isEqualTo(result.getFirst().title());
    }
}
