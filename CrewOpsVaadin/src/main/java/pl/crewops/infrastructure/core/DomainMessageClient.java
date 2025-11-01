package pl.crewops.infrastructure.core;

import static pl.crewops.enums.ControllerURL.*;

import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClientException;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.model.dto.message.MessageDTO;
import pl.crewops.model.dto.message.SendMessageCommand;

@Slf4j
class DomainMessageClient extends DomainAbstractClient {

    public DomainMessageClient(AuthorizationProvider authorizationProvider) {
        super(authorizationProvider);
    }

    public void sendMessage(SendMessageCommand sendMessageCommand) throws NotAuthenticatedException {
        try {
            authorizedClient()
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(MESSAGES).build())
                    .body(sendMessageCommand)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Update qualification expired at error");
        }
    }

    public List<MessageDTO> getMessagesByRecipientEmployeeId(UUID employeeId) throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(MESSAGES_EID).build(employeeId))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Error getting messages by recipient employee");
            return List.of();
        }
    }

    public MessageDTO setMessageReadStatus(UUID messageId, boolean status) throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .patch()
                    .uri(uriBuilder -> uriBuilder.path(MESSAGES_MID).build(messageId))
                    .body(status)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Update message read status error");
            return null;
        }
    }
}
