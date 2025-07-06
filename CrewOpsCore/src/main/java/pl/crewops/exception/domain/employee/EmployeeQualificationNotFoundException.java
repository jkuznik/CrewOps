package pl.crewops.exception.domain.employee;

import java.util.UUID;

public class EmployeeQualificationNotFoundException extends RuntimeException {
    public EmployeeQualificationNotFoundException(UUID employeId, UUID qualificationId) {
        super("Failed to find qualification with id " + qualificationId + " for employee with id " + employeId);
    }
}
