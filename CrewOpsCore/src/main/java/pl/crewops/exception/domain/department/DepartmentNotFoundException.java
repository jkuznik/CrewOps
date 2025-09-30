package pl.crewops.exception.domain.department;

import java.util.UUID;

public class DepartmentNotFoundException extends RuntimeException {
    public DepartmentNotFoundException(String message) {
        super(message);
    }

    public DepartmentNotFoundException(UUID id) {
        super("Department with id " + id.toString() + " not found");
    }
}
