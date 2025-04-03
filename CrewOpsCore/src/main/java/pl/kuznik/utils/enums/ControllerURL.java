package pl.kuznik.utils.enums;

public class ControllerURL {
    public static final String EMPLOYEES = "employees";
    public static final String EMPLOYEE_ID = "employeeId";
    public static final String EMPLOYEES_EID = EMPLOYEES + "/{" + EMPLOYEE_ID + "}";
    public static final String EMPLOYEES_EID_PHONE = EMPLOYEES_EID + "/phone";

    public static final String QUALIFICATIONS = "qualifications";
    public static final String QUALIFICATION_ID = "qualificationId";
    public static final String QUALIFICATIONS_QID = QUALIFICATIONS + "/{" + QUALIFICATION_ID + "}";

    public static final String EMPLOYEES_QID = EMPLOYEES + "/{" + QUALIFICATION_ID + "}";

    public static final String EMPLOYEES_EID_QUALIFICATIONS_QID = EMPLOYEES_EID + QUALIFICATIONS_QID;

    public static final String EMPLOYEES_EID_QUALIFICATIONS_QID_EXPIRED = EMPLOYEES_EID_QUALIFICATIONS_QID + "/expired";
}
