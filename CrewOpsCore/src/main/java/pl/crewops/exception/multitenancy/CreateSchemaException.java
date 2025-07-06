package pl.crewops.exception.multitenancy;

public class CreateSchemaException extends RuntimeException {
    public CreateSchemaException(String message) {
        super(message);
    }

    public CreateSchemaException(String message, Throwable cause) {
        super(message, cause);
    }
}
