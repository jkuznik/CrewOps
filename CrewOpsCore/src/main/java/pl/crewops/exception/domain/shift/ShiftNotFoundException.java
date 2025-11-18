package pl.crewops.exception.domain.shift;

import java.util.UUID;

public class ShiftNotFoundException extends RuntimeException {
    public ShiftNotFoundException(UUID id) {
        super("Shift with id " + id + " not found");
    }
}
