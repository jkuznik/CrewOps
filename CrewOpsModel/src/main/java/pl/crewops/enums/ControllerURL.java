package pl.crewops.enums;

import java.util.Arrays;
import org.springframework.util.AntPathMatcher;

public class ControllerURL {

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    public static final String LOGIN = "/login";
    public static final String REGISTER = "/register";
    public static final String LOGOUT = "/logout";
    public static final String VALIDATE = "/validate";

    public static final String EMPLOYEES = "/employees";
    public static final String EMPLOYEE_ID = "employeeId";
    public static final String QUALIFICATIONS = "/qualifications";
    public static final String QUALIFICATION_ID = "qualificationId";
    public static final String VEHICLES = "/vehicles";
    public static final String VEHICLE_ID = "vehicleId";
    public static final String VEHICLE_TYPES = "/vehicleTypes";
    public static final String VEHICLE_TYPE_ID = "vehicleTypeId";
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

    public static final String VEHICLES_VID = VEHICLES + "/{" + VEHICLE_ID + "}";
    public static final String VEHICLES_VID_EMPLOYEES = VEHICLES + "/{" + VEHICLE_ID + "}" + EMPLOYEES;
    public static final String VEHICLES_RN = VEHICLES + "/{registrationNumber}";
    public static final String VEHICLES_VIDS = VEHICLES + "/collection";

    public static final String EMPLOYEES_EID_PHONE = EMPLOYEES_EID + "/phone";
    public static final String EMPLOYEES_EID_QUALIFICATIONS_QID = EMPLOYEES_EID + QUALIFICATIONS_QID;
    public static final String EMPLOYEES_EID_QUALIFICATIONS_QID_EXPIRED = EMPLOYEES_EID_QUALIFICATIONS_QID + "/expired";

    public static final String EMPLOYEES_EID_VEHICLES_VID = EMPLOYEES_EID + VEHICLES_VID;

    public static String[] publicUrl() {
        return new String[] {
            LOGIN,
            REGISTER,
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

    public static String[] shiftLeaderUrlPATCH() {
        return new String[] {"/" + VEHICLES_VID, "/" + BREAKDOWNS_BID // TODO: implement this on FE side
        };
    }

    public static String[] managerUrlPOST() {
        return new String[] {"/" + EMPLOYEES, "/" + QUALIFICATIONS, "/" + VEHICLES};
    }

    public static String[] managerUrlPATCH() {
        return new String[] {
            "/" + EMPLOYEES_EID,
            "/" + EMPLOYEES_EID_PHONE,
            "/" + EMPLOYEES_EID_QUALIFICATIONS_QID,
            "/" + EMPLOYEES_EID_QUALIFICATIONS_QID_EXPIRED,
            "/" + EMPLOYEES_EID_VEHICLES_VID,
            "/" + QUALIFICATIONS_QID,
            "/" + VEHICLES_VID
        };
    }

    public static String[] managerUrlDELETE() {
        return new String[] {
            "/" + EMPLOYEES_EID, "/" + EMPLOYEES_EID_QUALIFICATIONS_QID, "/" + EMPLOYEES_EID_VEHICLES_VID
        };
    }
}
