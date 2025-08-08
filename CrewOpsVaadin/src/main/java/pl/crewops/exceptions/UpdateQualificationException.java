package pl.crewops.exceptions;

public class UpdateQualificationException extends RuntimeException {

    private final ExceptionMessageTranslator translator;

    public UpdateQualificationException() {
        super();
        translator = new ExceptionMessageTranslator(ExceptionMessageTranslator.UPDATE_QUALIFICATION_ERROR);
    }

    @Override
    public String getMessage() {
        return translator.getText();
    }
}
