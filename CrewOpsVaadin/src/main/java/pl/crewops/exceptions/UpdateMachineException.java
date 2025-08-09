package pl.crewops.exceptions;

public class UpdateMachineException extends RuntimeException {
    private final ExceptionMessageTranslator translator;

    public UpdateMachineException() {
        super();
        translator = new ExceptionMessageTranslator(ExceptionMessageTranslator.UPDATE_MACHINE_ERROR);
    }

    @Override
    public String getMessage() {
        return translator.getText();
    }
}
