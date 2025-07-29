package pl.crewops.exception.domain.company;

public class NoUniqueCompanyTaxIdException extends RuntimeException {
    public NoUniqueCompanyTaxIdException(String message) {
        super("Company with tax id: " + message + " already exists");
    }
}
