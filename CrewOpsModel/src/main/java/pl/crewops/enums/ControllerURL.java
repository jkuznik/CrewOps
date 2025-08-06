package pl.crewops.enums;

import java.util.Arrays;
import org.springframework.util.AntPathMatcher;

public interface ControllerURL {

    static final AntPathMatcher pathMatcher = new AntPathMatcher();

    public static final String LOGIN = "/login";
    public static final String REGISTER = "/register";
    public static final String LOGOUT = "/logout";
    public static final String VALIDATE = "/validate";

    public static final String EMPLOYEES = "/employees";
    public static final String EMPLOYEE_ID = "employeeId";
    public static final String QUALIFICATIONS = "/qualifications";
    public static final String QUALIFICATION_ID = "qualificationId";
    public static final String MACHINES = "/machines";
    public static final String MACHINE_ID = "machineId";
    public static final String MACHINE_TYPES = "/machineTypes";
    public static final String MACHINE_TYPE_ID = "machineTypeId";
    public static final String BREAKDOWNS = "/breakdowns";
    public static final String BREAKDOWN_ID = "breakdownId";
    public static final String COMPANIES = "/companies";
    public static final String COMPANY_ID = "companyId";

    public static final String EMPLOYEES_EID = EMPLOYEES + "/{" + EMPLOYEE_ID + "}";
    public static final String BREAKDOWNS_BID = BREAKDOWNS + "/{" + BREAKDOWN_ID + "}";
    public static final String COMPANIES_CID = COMPANIES + "/{" + COMPANY_ID + "}";

    public static final String QUALIFICATIONS_QID = QUALIFICATIONS + "/{" + QUALIFICATION_ID + "}";
    public static final String QUALIFICATIONS_QID_EMPLOYEES =
            QUALIFICATIONS + "/{" + QUALIFICATION_ID + "}" + EMPLOYEES;
    public static final String QUALIFICATIONS_QIDS = QUALIFICATIONS + "/collection";

    public static final String MACHINES_VID = MACHINES + "/{" + MACHINE_ID + "}";
    public static final String MACHINES_VID_EMPLOYEES = MACHINES + "/{" + MACHINE_ID + "}" + EMPLOYEES;
    public static final String MACHINES_RN = MACHINES + "/{registrationNumber}";
    public static final String MACHINES_VIDS = MACHINES + "/collection";

    public static final String EMPLOYEES_EID_PHONE = EMPLOYEES_EID + "/phone";
    public static final String EMPLOYEES_EID_QUALIFICATIONS_QID = EMPLOYEES_EID + QUALIFICATIONS_QID;
    public static final String EMPLOYEES_EID_QUALIFICATIONS_QID_EXPIRED = EMPLOYEES_EID_QUALIFICATIONS_QID + "/expired";

    public static final String EMPLOYEES_EID_MACHINES_VID = EMPLOYEES_EID + MACHINES_VID;

    public static String[] publicUrl() {
        return new String[] {
            LOGIN,
            LOGOUT,
            VALIDATE,
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/v3/api-docs/**",
            "/swagger.yaml",
            "/api-docs"
        };
    }

    public static boolean isPublicUrl(String requestURI) {
        return Arrays.stream(ControllerURL.publicUrl()).anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }
}
