package pl.crewops.enums;

import java.util.Arrays;
import org.springframework.util.AntPathMatcher;

public interface ControllerURL {

    AntPathMatcher pathMatcher = new AntPathMatcher();

    String LOGIN = "/login";
    String REGISTER = "/register";
    String LOGOUT = "/logout";
    String VALIDATE = "/validate";
    String HEALTH = "/health";

    String UPDATE_ROLES = "/updateRoles";

    String EMPLOYEES = "/employees";
    String EMPLOYEE_ID = "employeeId";
    String QUALIFICATIONS = "/qualifications";
    String QUALIFICATION_ID = "qualificationId";
    String MACHINES = "/machines";
    String MACHINE_ID = "machineId";
    String MACHINE_TYPES = "/machineTypes";
    String MACHINE_TYPE_ID = "machineTypeId";
    String BREAKDOWNS = "/breakdowns";
    String BREAKDOWN_ID = "breakdownId";
    String COMPANIES = "/companies";
    String COMPANY_ID = "companyId";
    String DEPARTMENTS = "/departments";
    String DEPARTMENT_ID = "departmentId";
    String MESSAGES = "/messages";
    String MESSAGE_ID = "messageId";

    String EMPLOYEES_EID = EMPLOYEES + "/{" + EMPLOYEE_ID + "}";
    String BREAKDOWNS_BID = BREAKDOWNS + "/{" + BREAKDOWN_ID + "}";
    String COMPANIES_CID = COMPANIES + "/{" + COMPANY_ID + "}";

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

    String EMPLOYEES_EID_PHONE = EMPLOYEES_EID + "/phone";
    String EMPLOYEES_EID_QUALIFICATIONS_EXPIRED = EMPLOYEES_EID + QUALIFICATIONS + "/expired";
    String EMPLOYEES_EID_QUALIFICATIONS_QID = EMPLOYEES_EID + QUALIFICATIONS_QID;
    String EMPLOYEES_EID_QUALIFICATIONS_QID_EXPIRED = EMPLOYEES_EID_QUALIFICATIONS_QID + "/expired";

    String EMPLOYEES_EID_MACHINES_VID = EMPLOYEES_EID + MACHINES_VID;

    static String[] publicUrl() {
        return new String[] {
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
            "/api-docs"
        };
    }

    static boolean isPublicUrl(String requestURI) {
        return Arrays.stream(ControllerURL.publicUrl()).anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }
}
