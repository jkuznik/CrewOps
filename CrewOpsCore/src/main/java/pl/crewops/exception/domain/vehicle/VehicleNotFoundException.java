package pl.crewops.exception.domain.vehicle;

import java.util.UUID;

public class VehicleNotFoundException extends RuntimeException {
    public VehicleNotFoundException(UUID uuid) {
        super("Vehicle with id " + uuid + " not found");
    }
}
