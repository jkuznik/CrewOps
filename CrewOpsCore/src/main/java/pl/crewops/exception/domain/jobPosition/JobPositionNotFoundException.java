package pl.crewops.exception.domain.jobPosition;

import java.util.UUID;

public class JobPositionNotFoundException extends RuntimeException {
    public JobPositionNotFoundException(UUID id) {
        super("Job position with id: " + id + " not found");
    }
}
