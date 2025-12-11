package pl.crewops.domain.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pl.crewops.domain.message.MessageTestFactory.*;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.crewops.domain.employee.EmployeeAPI;
import pl.crewops.exception.domain.message.MessageNotFoundException;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.model.dto.message.CreateMessageDTO;
import pl.crewops.model.dto.message.MessageDTO;
import pl.crewops.model.tenantSchema.Message;

@SpringJUnitConfig(classes = {MessageService.class, MessageRepository.class, EmployeeAPI.class})
class MessageServiceTest {

    @Autowired
    private MessageService messageService;

    @MockitoBean
    private MessageRepository messageRepository;

    @MockitoBean
    private EmployeeAPI employeeAPI;

    @MockitoBean
    private MessageMapper messageMapper;

    private CreateMessageDTO createMessageDTO;
    private Message message;
    private MessageDTO messageDTO;
    private Page<Message> messageSet;

    @BeforeEach
    void setUp() {
        createMessageDTO = createMessageDTO();
        message = message();
        messageDTO = messageDTO();
        messageSet = messageSet();
    }

    @Test
    void createMessage_shouldReturnMessageDTO_whenCreateMessageDTOIsValid() {
        // mapper → entity
        when(messageMapper.toEntity(createMessageDTO)).thenReturn(message);

        // repo → save
        when(messageRepository.save(any())).thenReturn(message);

        // mapper → DTO
        when(messageMapper.toDTO(message)).thenReturn(messageDTO);

        // when
        var result = messageService.createMessage(createMessageDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.description()).isEqualTo(message.getDescription());
    }

    @Test
    void getAllMessagesByRecipientEmployeeIdAndReadIsFalse_shouldReturnCollectionOfUnreadMessagesDTO() {
        // repo
        when(messageRepository.findAllByRecipientEmployeeIdAndReadIsFalse(any(), any()))
                .thenReturn(messageSet);

        // mapper for each element
        when(messageMapper.toDTO(any())).thenReturn(messageDTO);

        // when
        List<MessageDTO> result =
                messageService.getAllMessagesByRecipientEmployeeIdAndReadIsFalse(recipientEmployeeId, 0, 15);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).title()).isEqualTo(messageDTO.title());
    }

    @Test
    void getAllMessagesByRecipientEmployeeId_shouldReturnCollectionOfMessagesDTO() {
        when(messageRepository.findAllByRecipientEmployeeId(any(), any())).thenReturn(messageSet);
        when(messageMapper.toDTO(any())).thenReturn(messageDTO);

        List<MessageDTO> result = messageService.getAllMessagesByRecipientEmployeeId(recipientEmployeeId, 0, 10);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).title()).isEqualTo(messageDTO.title());
    }

    @Test
    void setMessageReadStatus_shouldUpdateReadStatus_whenMessageExists() {
        when(messageRepository.findById(message.getId())).thenReturn(java.util.Optional.of(message));
        when(messageRepository.save(any())).thenReturn(message);

        Message changed = Message.builder()
                .title(message.getTitle())
                .description(message.getDescription())
                .recipientEmployeeId(message.getRecipientEmployeeId())
                .senderEmployeeId(message.getSenderEmployeeId())
                .read(true)
                .build();

        MessageDTO changedDTO = MessageDTO.builder()
                .id(changed.getId())
                .title(changed.getTitle())
                .description(changed.getDescription())
                .recipientEmployeeId(changed.getRecipientEmployeeId())
                .senderEmployeeId(changed.getSenderEmployeeId())
                .isRead(true)
                .createdAt(changed.getCreatedAt())
                .build();

        when(messageMapper.toDTO(any())).thenReturn(changedDTO);

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

        // Mapper must map CreateMessageDTO → Message for each employee
        when(messageMapper.toEntity(any())).thenReturn(message);

        when(messageRepository.saveAll(any())).thenReturn(List.of(message));

        var command = sendMessageCommandAll();

        messageService.sendMessage(command);

        verify(messageRepository).saveAll(any());
    }

    @Test
    void setMessageReadStatus_shouldThrowException_whenMessageDoesNotExist() {
        when(messageRepository.findById(any())).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> messageService.setMessageReadStatus(UUID.randomUUID(), true))
                .isInstanceOf(MessageNotFoundException.class);
    }

    @Test
    void sendMessage_shouldSendToEmployeesByMachine_whenRecipientSelectionIsMACHINE() {
        var employees = List.of(
                EmployeeDTO.builder().firstName("first").lastName("last").build());

        when(employeeAPI.getEmployeesByMachines(any(), anyInt(), anyInt())).thenReturn(employees);
        when(messageMapper.toEntity(any())).thenReturn(message);
        when(messageRepository.saveAll(any())).thenReturn(List.of(message));

        var command = sendMessageCommandMachine();

        messageService.sendMessage(command);

        verify(messageRepository).saveAll(any());
    }

    @Test
    void sendMessage_shouldSendToSingleEmployee_whenRecipientSelectionIsEMPLOYEE() {
        when(messageMapper.toEntity(any())).thenReturn(message);
        when(messageRepository.save(any())).thenReturn(message);

        var command = sendMessageCommandEmployee();

        messageService.sendMessage(command);

        verify(messageRepository).save(any());
    }
}
