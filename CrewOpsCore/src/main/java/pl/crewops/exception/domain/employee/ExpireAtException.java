package pl.crewops.exception.domain.employee;

public class ExpireAtException extends RuntimeException {
    public ExpireAtException(String message) {
        super(message);
    }
}
