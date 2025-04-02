package pl.kuznik.exception;

public class ExpireAtException extends RuntimeException {
    public ExpireAtException(String message) {
        super(message);
    }
}
