package pl.crewops.exception.domain.message;

import java.util.UUID;

public class MessageNotFoundException extends RuntimeException {
    public MessageNotFoundException(String message) {
        super(message);
    }

    public MessageNotFoundException(UUID messageId) {
        super("Message with id " + messageId.toString() + " not found");
    }
}
