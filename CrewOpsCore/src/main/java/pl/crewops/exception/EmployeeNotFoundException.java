package pl.crewops.exception;

import java.util.UUID;

public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(UUID uuid) {
        super("Employee with id " + uuid + " not found");
    }

    public EmployeeNotFoundException(String username) {
        super("Employee with username " + username + " not found");
    }
}
