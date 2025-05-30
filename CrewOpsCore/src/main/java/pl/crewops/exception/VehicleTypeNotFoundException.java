package pl.crewops.exception;

import java.util.UUID;

public class VehicleTypeNotFoundException extends RuntimeException {
    public VehicleTypeNotFoundException(UUID id) {
        super("Vehicle type with id " + id + " not found");
    }
}
