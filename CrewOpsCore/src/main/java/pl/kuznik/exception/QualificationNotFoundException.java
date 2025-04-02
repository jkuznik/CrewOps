package pl.kuznik.exception;

import java.util.UUID;

public class QualificationNotFoundException extends RuntimeException {
    public QualificationNotFoundException(UUID qulificationId) {
        super("Qualification with id " + qulificationId + " not found");
    }
}
