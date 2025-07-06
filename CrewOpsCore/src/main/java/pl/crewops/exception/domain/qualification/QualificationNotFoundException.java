package pl.crewops.exception.domain.qualification;

import java.util.UUID;

public class QualificationNotFoundException extends RuntimeException {
    public QualificationNotFoundException(UUID qulificationId) {
        super("Qualification with id " + qulificationId + " not found");
    }
}
