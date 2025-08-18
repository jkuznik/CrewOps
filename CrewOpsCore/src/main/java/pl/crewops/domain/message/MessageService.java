package pl.crewops.domain.message;

import static pl.crewops.domain.message.MessageMapper.mapToDTO;
import static pl.crewops.domain.message.MessageMapper.mapToEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.crewops.dto.message.CreateMessageDTO;
import pl.crewops.dto.message.MessageDTO;
import pl.crewops.model.Message;

@Slf4j
@Service
@RequiredArgsConstructor
class MessageService implements MessageAPI {

    private final MessageRepository messageRepository;

    @Override
    public MessageDTO createMessage(CreateMessageDTO createMessageDTO) {
        Message message = messageRepository.save(mapToEntity(createMessageDTO));
        return mapToDTO(message);
    }
}
