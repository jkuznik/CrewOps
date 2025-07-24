package pl.crewops.exception.domain.auth;

public class UsernameAlreadyExistException extends RuntimeException {
    public UsernameAlreadyExistException(String username) {
        super("Username " + username + " already exist");
    }
}
