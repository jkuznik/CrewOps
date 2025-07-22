package pl.crewops.exception.auth;

public class RegisterCustomerException extends RuntimeException {
    public RegisterCustomerException(String message) {
        super(message);
    }
}
