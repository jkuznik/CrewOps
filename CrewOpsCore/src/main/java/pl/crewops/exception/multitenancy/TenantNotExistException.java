package pl.crewops.exception.multitenancy;

import java.util.UUID;

public class TenantNotExistException extends RuntimeException {
    public TenantNotExistException(UUID id) {
        super("Tenant with id: " + id.toString() + " does not exist");
    }
}
