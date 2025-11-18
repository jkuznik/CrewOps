package pl.crewops.enums;

import java.util.Arrays;
import org.springframework.util.AntPathMatcher;

public interface ControllerURL {

    AntPathMatcher pathMatcher = new AntPathMatcher();

    String LOGIN = "/login";
    String REGISTER = "/register";
    String VERIFY_EMAIL = "/verify-email";
    String LOGOUT = "/logout";
    String VALIDATE = "/validate";
    String HEALTH = "/health";

    String UPDATE_USER_ROLES = "/update-user-roles";
    String UPDATE_USER_CREDENTIALS = "/update-user-credentials";

    String EMPLOYEES = "/employees";
    String EMPLOYEE_ID = "employee-id";
    String QUALIFICATIONS = "/qualifications";
    String QUALIFICATION_ID = "qualification-id";
    String MACHINES = "/machines";
    String MACHINE_ID = "machine-id";
    String MACHINE_TYPES = "/machine-types";
    String MACHINE_TYPE_ID = "machine-type-id";
    String BREAKDOWNS = "/breakdowns";
    String BREAKDOWN_ID = "breakdown-id";
    String COMPANIES = "/companies";
    String COMPANY_ID = "company-id";
    String DEPARTMENTS = "/departments";
    String DEPARTMENT_ID = "department-id";
    String DAILY_ENTRIES = "/daily-entries";
    String JOB_POSITIONS = "/job-positions";
    String JOB_POSITIONS_ID = "job-position-id";
    String MESSAGES = "/messages";
    String MESSAGE_ID = "message-id";
    String NOTES = "/daily-notes";
    String SHIFTS = "/shifts";
    String SHIFT_ID = "shift-id";

    String EMPLOYEES_EID = EMPLOYEES + "/{" + EMPLOYEE_ID + "}";
    String BREAKDOWNS_BID = BREAKDOWNS + "/{" + BREAKDOWN_ID + "}";
    String COMPANIES_CID = COMPANIES + "/{" + COMPANY_ID + "}";
    String DEPARTMENTS_DID = DEPARTMENTS + "/{" + DEPARTMENT_ID + "}";
    String JOB_POSITIONS_JID = JOB_POSITIONS + "/{" + JOB_POSITIONS_ID + "}";

    String DAILY_ENTRIES_APPROVE = DAILY_ENTRIES + "/approve";

    String QUALIFICATIONS_QID = QUALIFICATIONS + "/{" + QUALIFICATION_ID + "}";
    String QUALIFICATIONS_QID_EMPLOYEES = QUALIFICATIONS + "/{" + QUALIFICATION_ID + "}" + EMPLOYEES;
    String QUALIFICATIONS_QIDS = QUALIFICATIONS + "/collection";

    String QUALIFICATIONS_EID_EXPIRED = QUALIFICATIONS + "/{" + EMPLOYEE_ID + "}" + "/expired";

    String MACHINES_VID = MACHINES + "/{" + MACHINE_ID + "}";
    String MACHINES_VID_EMPLOYEES = MACHINES + "/{" + MACHINE_ID + "}" + EMPLOYEES;
    String MACHINES_RN = MACHINES + "/{registrationNumber}";
    String MACHINES_VIDS = MACHINES + "/collection";

    String MESSAGES_EID = MESSAGES + "/{" + EMPLOYEE_ID + "}";
    String MESSAGES_MID = MESSAGES + "/{" + MESSAGE_ID + "}";

    String NOTES_DATE = NOTES + "/{date}";

    String DEPARTMENTS_DIDS = DEPARTMENTS + "/collection";

    String SHIFTS_SID = SHIFTS + "/{" + SHIFT_ID + "}";

    String EMPLOYEES_EID_PHONE = EMPLOYEES_EID + "/phone";
    String EMPLOYEE_EID_OPTIONS = EMPLOYEES_EID + "/options";
    String EMPLOYEES_EID_QUALIFICATIONS_EXPIRED = EMPLOYEES_EID + QUALIFICATIONS + "/expired";
    String EMPLOYEES_EID_QUALIFICATIONS_QID = EMPLOYEES_EID + QUALIFICATIONS_QID;
    String EMPLOYEES_EID_QUALIFICATIONS_QID_EXPIRED = EMPLOYEES_EID_QUALIFICATIONS_QID + "/expired";

    String EMPLOYEES_EID_MACHINES_VID = EMPLOYEES_EID + MACHINES_VID;

    String EMPLOYEES_EID_DEPARTMENTS_DID = EMPLOYEES_EID + DEPARTMENTS_DID;

    static String[] publicUrl() {
        return new String[] {
            REGISTER,
            VERIFY_EMAIL,
            LOGIN,
            LOGOUT,
            VALIDATE,
            HEALTH,
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/v3/api-docs/**",
            "/swagger.yaml",
            "/api-docs",
            "/testMail"
        };
    }

    static boolean isPublicUrl(String requestURI) {
        return Arrays.stream(ControllerURL.publicUrl()).anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }
}
