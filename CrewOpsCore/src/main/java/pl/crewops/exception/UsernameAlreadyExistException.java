package pl.crewops.exception;

public class UsernameAlreadyExistException extends RuntimeException {
    public UsernameAlreadyExistException(String username) {
        super("Username " + username + " already exist");
    }
}
