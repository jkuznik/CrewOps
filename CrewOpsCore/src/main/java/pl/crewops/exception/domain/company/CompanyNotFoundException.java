package pl.crewops.exception.domain.company;

import java.util.UUID;

public class CompanyNotFoundException extends RuntimeException {
    public CompanyNotFoundException(UUID id) {
        super("Company with id " + id + " not found");
    }
}
