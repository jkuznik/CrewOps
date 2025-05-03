package pl.crewops.exceptions;

public class NotAuthenticatedException extends Exception {
    public NotAuthenticatedException() {
        super("Current session is not authenticated");
    }
}
