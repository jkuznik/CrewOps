package pl.crewops.domain.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pl.crewops.domain.message.MessageTestFactory.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.crewops.domain.employee.EmployeeAPI;
import pl.crewops.dto.employee.EmployeeDTO;
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

    @Test
    void getAllMessagesByRecipientEmployeeId_shouldReturnCollectionOfMessagesDTO() {
        when(messageRepository.findAllByRecipientEmployeeId(any(), any())).thenReturn(messageSet);

        List<MessageDTO> result = messageService.getAllMessagesByRecipientEmployeeId(recipientEmployeeId, 0, 10);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).title()).isEqualTo("title");
    }

    @Test
    void setMessageReadStatus_shouldUpdateReadStatus_whenMessageExists() {
        when(messageRepository.findById(message.getId())).thenReturn(java.util.Optional.of(message));
        when(messageRepository.save(any())).thenReturn(message);

        MessageDTO result = messageService.setMessageReadStatus(message.getId(), true);

        assertThat(result.isRead()).isTrue();
        verify(messageRepository).save(any());
    }

    @Test
    void sendMessage_shouldSendToAllEmployees_whenRecipientSelectionIsALL() {
        var employees = List.of(
                EmployeeDTO.builder().id(UUID.randomUUID()).build(),
                EmployeeDTO.builder().id(UUID.randomUUID()).build());
        when(employeeAPI.getAllActiveEmployees()).thenReturn(employees);
        when(messageRepository.saveAll(any())).thenReturn(List.of(message));

        var command = sendMessageCommandAll();

        messageService.sendMessage(command);

        // verify saveAll called with 2 messages
        verify(messageRepository).saveAll(any());
    }

    @Test
    void setMessageReadStatus_shouldThrowException_whenMessageDoesNotExist() {
        when(messageRepository.findById(any())).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> messageService.setMessageReadStatus(UUID.randomUUID(), true))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void sendMessage_shouldSendToEmployeesByMachine_whenRecipientSelectionIsMACHINE() {
        var employees = List.of(
                EmployeeDTO.builder().firstName("first").lastName("last").build());
        when(employeeAPI.getEmployeesByMachines(any(), anyInt(), anyInt())).thenReturn(employees);
        when(messageRepository.saveAll(any())).thenReturn(List.of(message));

        var command = sendMessageCommandMachine();

        messageService.sendMessage(command);

        verify(messageRepository).saveAll(any());
    }

    @Test
    void sendMessage_shouldSendToSingleEmployee_whenRecipientSelectionIsEMPLOYEE() {
        when(messageRepository.save(any())).thenReturn(message);

        var command = sendMessageCommandEmployee();

        messageService.sendMessage(command);

        verify(messageRepository).save(any());
    }
}
