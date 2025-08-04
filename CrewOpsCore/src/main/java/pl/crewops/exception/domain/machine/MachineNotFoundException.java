package pl.crewops.exception.domain.machine;

import java.util.UUID;

public class MachineNotFoundException extends RuntimeException {
    public MachineNotFoundException(UUID uuid) {
        super("Machine with id " + uuid + " not found");
    }
}
