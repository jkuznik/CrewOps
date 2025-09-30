package pl.crewops.exception.domain.registration;

import java.util.UUID;

public class NotFoundPendingRegistrationException extends RuntimeException {
    public NotFoundPendingRegistrationException(String message) {
        super(message);
    }

    public NotFoundPendingRegistrationException(UUID id) {
        super("Could not find pending registration with id: " + id.toString());
    }
}
