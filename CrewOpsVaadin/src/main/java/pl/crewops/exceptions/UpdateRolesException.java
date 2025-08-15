package pl.crewops.exceptions;

public class UpdateRolesException extends RuntimeException {
    private final ExceptionMessageTranslator translator;

    public UpdateRolesException() {
        super();
        translator = new ExceptionMessageTranslator(ExceptionMessageTranslator.UPDATE_ROLES_ERROR);
    }

    @Override
    public String getMessage() {
        return translator.getText();
    }
}
