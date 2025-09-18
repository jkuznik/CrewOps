package pl.crewops.domain.message;

import static pl.crewops.domain.message.MessageMapper.mapToDTO;
import static pl.crewops.domain.message.MessageMapper.mapToEntity;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.domain.employee.EmployeeAPI;
import pl.crewops.model.Message;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.model.dto.message.CreateMessageDTO;
import pl.crewops.model.dto.message.MessageDTO;
import pl.crewops.model.dto.message.SendMessageCommand;
import pl.crewops.util.pagination.PageRequestFactory;

@Slf4j
@Service
@RequiredArgsConstructor
class MessageService implements MessageAPI {

    private final MessageRepository messageRepository;
    private final EmployeeAPI employeeAPI;

    @Override
    @Transactional
    public MessageDTO createMessage(CreateMessageDTO createMessageDTO) {
        Message message = messageRepository.save(mapToEntity(createMessageDTO));
        return mapToDTO(message);
    }

    @Override
    @Transactional
    public void sendMessage(SendMessageCommand sendMessageCommand) {

        switch (sendMessageCommand.recipientSelection().type()) {
            case ALL -> sendToAllEmployeesAsync(sendMessageCommand, employeeAPI.getAllActiveEmployees());
            case DEPARTMENT -> sendToAllEmployeesAsync(
                    sendMessageCommand,
                    employeeAPI.getAllActiveEmployeesByDepartment(UUID.fromString(
                            sendMessageCommand.recipientSelection().value())));
            case MACHINE -> sendToAllByMachine(
                    sendMessageCommand,
                    employeeAPI.getEmployeesByMachines(
                            UUID.fromString(
                                    sendMessageCommand.recipientSelection().value()),
                            0,
                            // TODO: implement non pagination sensitive solution
                            1000));
            case EMPLOYEE -> sendToRecipientEmployee(sendMessageCommand);
        }
    }

    // todo: i do afraid if async wont break multitenancy policy based on ThreadLocal, consider to modify this
    //  implementation and in case of async method call fetch employee list earlier and handle in-app message sending in
    // current
    //  thread and then just sending mails or sms in async method which will contains all required info like
    //  email address, phone numbers, etc before call async
    @Async
    void sendToAllEmployeesAsync(SendMessageCommand sendMessageCommand, List<EmployeeDTO> allActiveEmployees) {
        List<Message> messages = allActiveEmployees.stream()
                .map(employeeDTO -> mapToEntity(CreateMessageDTO.builder()
                        .title(sendMessageCommand.subject())
                        .description(sendMessageCommand.description())
                        .recipientEmployeeId(employeeDTO.id())
                        .senderEmployeeId(sendMessageCommand.senderEmployeeId())
                        .build()))
                .toList();

        messageRepository.saveAll(messages);
    }

    @Async
    void sendToAllByMachine(SendMessageCommand sendMessageCommand, List<EmployeeDTO> employeesByMachine) {
        List<Message> messages = employeesByMachine.stream()
                .map(employeeDTO -> mapToEntity(CreateMessageDTO.builder()
                        .title(sendMessageCommand.subject())
                        .description(sendMessageCommand.description())
                        .recipientEmployeeId(employeeDTO.id())
                        .senderEmployeeId(sendMessageCommand.senderEmployeeId())
                        .build()))
                .toList();

        messageRepository.saveAll(messages);
    }

    void sendToRecipientEmployee(SendMessageCommand sendMessageCommand) {
        var createMessageDTO = CreateMessageDTO.builder()
                .title(sendMessageCommand.subject())
                .description(sendMessageCommand.description())
                .recipientEmployeeId(
                        UUID.fromString(sendMessageCommand.recipientSelection().value()))
                .senderEmployeeId(sendMessageCommand.senderEmployeeId())
                .build();

        createMessage(createMessageDTO);
    }

    @Override
    @Transactional
    public List<MessageDTO> getAllMessagesByRecipientEmployeeIdAndReadIsFalse(
            UUID recipientEmployeeId, int page, int size) {
        Page<Message> allByRecipientEmployeeIdAndRead = messageRepository.findAllByRecipientEmployeeIdAndReadIsFalse(
                recipientEmployeeId, getPageRequest(page, size));
        return allByRecipientEmployeeIdAndRead.stream()
                .map(MessageMapper::mapToDTO)
                .toList();
    }

    @Override
    @Transactional
    public List<MessageDTO> getAllMessagesByRecipientEmployeeId(UUID recipientEmployeeId, int page, int size) {
        Page<Message> allByRecipientEmployeeIdAndRead =
                messageRepository.findAllByRecipientEmployeeId(recipientEmployeeId, getPageRequest(page, size));
        return allByRecipientEmployeeIdAndRead.stream()
                .map(MessageMapper::mapToDTO)
                .toList();
    }

    @Override
    @Transactional
    public MessageDTO setMessageReadStatus(UUID messageId, boolean read) {
        // TODO: custom exception
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new NoSuchElementException());
        message.setRead(read);
        return mapToDTO(messageRepository.save(message));
    }

    private static PageRequest getPageRequest(int page, int size) {
        return PageRequestFactory.createPageRequest(page, size, Sort.by(Sort.Order.asc("createdAt")));
    }
}
