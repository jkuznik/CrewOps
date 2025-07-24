package pl.crewops.exception.domain.registration;

public class RegisterCustomerException extends RuntimeException {
    public RegisterCustomerException(String message) {
        super(message);
    }
}
