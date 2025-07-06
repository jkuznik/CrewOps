package pl.crewops.exception.multitenancy;

public class TenantNotExistException extends RuntimeException {
    public TenantNotExistException(String name) {
        super("Tenant with name: " + name + " does not exist");
    }
}
