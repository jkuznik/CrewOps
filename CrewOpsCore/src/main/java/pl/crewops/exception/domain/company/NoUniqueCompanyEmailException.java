package pl.crewops.exception.domain.company;

public class NoUniqueCompanyEmailException extends RuntimeException {
    public NoUniqueCompanyEmailException(String message) {
        super("Company with email: " + message + " already exists");
    }
}
