package pl.crewops.exception.domain.machine;

import java.util.UUID;

public class MachineTypeNotFoundException extends RuntimeException {
    public MachineTypeNotFoundException(UUID id) {
        super("Machine type with id " + id + " not found");
    }
}
