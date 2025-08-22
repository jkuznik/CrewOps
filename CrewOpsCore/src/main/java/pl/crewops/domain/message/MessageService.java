package pl.crewops.domain.message;

import static pl.crewops.domain.message.MessageMapper.mapToDTO;
import static pl.crewops.domain.message.MessageMapper.mapToEntity;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.dto.message.CreateMessageDTO;
import pl.crewops.dto.message.MessageDTO;
import pl.crewops.model.Message;
import pl.crewops.util.pagination.PageRequestFactory;

@Slf4j
@Service
@RequiredArgsConstructor
class MessageService implements MessageAPI {

    private final MessageRepository messageRepository;

    @Override
    @Transactional
    public MessageDTO createMessage(CreateMessageDTO createMessageDTO) {
        Message message = messageRepository.save(mapToEntity(createMessageDTO));
        return mapToDTO(message);
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

    private static PageRequest getPageRequest(int page, int size) {
        return PageRequestFactory.createPageRequest(page, size, Sort.by(Sort.Order.asc("createdAt")));
    }
}
