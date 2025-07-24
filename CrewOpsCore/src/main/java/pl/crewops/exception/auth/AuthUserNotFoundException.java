package pl.crewops.exception.auth;

import java.util.UUID;

public class AuthUserNotFoundException extends RuntimeException {
    public AuthUserNotFoundException(String message) {
        super(message);
    }

    public AuthUserNotFoundException(UUID employeeId) {
        super("AuthUser related to employee with id " + employeeId + " not found");
    }
}
