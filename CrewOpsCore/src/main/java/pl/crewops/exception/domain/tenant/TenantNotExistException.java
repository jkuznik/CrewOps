package pl.crewops.exception.domain.tenant;

import java.util.UUID;

public class TenantNotExistException extends RuntimeException {
    public TenantNotExistException(UUID id) {
        super("Tenant with id: " + id.toString() + " does not exist");
    }

    public TenantNotExistException(String taxId) {
        super("Tenant with taxId: " + taxId + " does not exist");
    }
}
