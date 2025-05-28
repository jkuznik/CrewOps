package pl.crewops.exception;

import pl.crewops.model.Employee;

public class AuthUserNotFoundException extends RuntimeException {
    public AuthUserNotFoundException(String message) {
        super(message);
    }

    public AuthUserNotFoundException(Employee employee) {
        super("AuthUser related to employee with id " + employee.getId() + " not found");
    }
}
