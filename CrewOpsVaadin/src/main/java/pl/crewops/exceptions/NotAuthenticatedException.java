package pl.crewops.exceptions;

public class NotAuthenticatedException extends Exception {

    private final ExceptionMessageTranslator translator;

    public NotAuthenticatedException() {
        super();
        this.translator = new ExceptionMessageTranslator(ExceptionMessageTranslator.NOT_AUTHENTICATED);
    }

    @Override
    public String getMessage() {
        return translator.getText();
    }
}
