package pl.crewops.exceptions;

import com.vaadin.flow.component.html.H1;

public class ExceptionMessageTranslator extends H1 {
    public static final String NOT_AUTHENTICATED = "exceptionMessageTranslator.notAuthenticated";
    // todo i18n
    public static final String UPDATE_DEPARTMENT_ERROR = "exceptionMessageTranslator.updateDepartmentError";
    public static final String UPDATE_QUALIFICATION_ERROR = "exceptionMessageTranslator.updateQualificationError";
    public static final String UPDATE_MACHINE_ERROR = "exceptionMessageTranslator.updateMachineError";
    public static final String UPDATE_ROLES_ERROR = "exceptionMessageTranslator.updateMachineError";

    public ExceptionMessageTranslator(String key) {
        setText(getTranslation(key));
    }
}
