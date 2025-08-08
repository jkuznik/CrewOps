package pl.crewops.exceptions;

import com.vaadin.flow.component.html.H1;

public class ExceptionMessageTranslator extends H1 {
    public static final String NOT_AUTHENTICATED = "exceptionMessageTranslator.notAuthenticated";
    public static final String UPDATE_QUALIFICATION_ERROR = "exceptionMessageTranslator.updateQualificationError";

    private final String message;

    public ExceptionMessageTranslator(String message) {
        this.message = message;
        getTranslation(message);
    }

    String getMessage() {
        return getText();
    }
}
