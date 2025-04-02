package pl.kuznik.exception;

public class ExpireAtException extends RuntimeException {
    public ExpireAtException() {
        super("Expire date can't be in the past");
    }
}
