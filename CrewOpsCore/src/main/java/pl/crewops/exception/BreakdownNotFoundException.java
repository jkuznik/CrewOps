package pl.crewops.exception;

import java.util.UUID;

public class BreakdownNotFoundException extends RuntimeException {
    public BreakdownNotFoundException(UUID id) {
        super("Could not find breakdown with id " + id);
    }
}
