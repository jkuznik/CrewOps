package pl.crewops.exception.domain.vehicle;

import java.util.UUID;

public class VehicleTypeNotFoundException extends RuntimeException {
    public VehicleTypeNotFoundException(UUID id) {
        super("Vehicle type with id " + id + " not found");
    }
}
