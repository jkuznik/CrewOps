package pl.crewops.exception;

public class ExpireAtException extends RuntimeException {
    public ExpireAtException(String message) {
        super(message);
    }
}
