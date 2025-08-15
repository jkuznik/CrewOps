package pl.crewops.exceptions;

import com.vaadin.flow.component.html.H1;

public class ExceptionMessageTranslator extends H1 {
    public static final String NOT_AUTHENTICATED = "exceptionMessageTranslator.notAuthenticated";
    public static final String UPDATE_QUALIFICATION_ERROR = "exceptionMessageTranslator.updateQualificationError";
    public static final String UPDATE_MACHINE_ERROR = "exceptionMessageTranslator.updateMachineError";
    //    TODO: update this message in i18n
    public static final String UPDATE_ROLES_ERROR = "exceptionMessageTranslator.updateMachineError";

    public ExceptionMessageTranslator(String key) {
        setText(getTranslation(key));
    }
}
