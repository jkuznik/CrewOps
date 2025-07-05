package pl.crewops.exception.auth;

public class UsernameAlreadyExistException extends RuntimeException {
    public UsernameAlreadyExistException(String username) {
        super("Username " + username + " already exist");
    }
}
