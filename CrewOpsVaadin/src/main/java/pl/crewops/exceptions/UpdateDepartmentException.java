package pl.crewops.exceptions;

public class UpdateDepartmentException extends RuntimeException {
    private final ExceptionMessageTranslator translator;

    public UpdateDepartmentException() {
        super();
        translator = new ExceptionMessageTranslator(ExceptionMessageTranslator.UPDATE_DEPARTMENT_ERROR);
    }

    @Override
    public String getMessage() {
        return translator.getText();
    }
}
