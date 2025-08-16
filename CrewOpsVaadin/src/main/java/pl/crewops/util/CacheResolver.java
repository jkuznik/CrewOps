package pl.crewops.util;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.crewops.security.custom.UserPrincipal;

@Slf4j
public class CacheResolver {

    public static final String GET_COMPANY_BY_ID = "getCompanyById";
    public static final String GET_EMPLOYEE_BY_ID = "getEmployeeById";
    public static final String GET_ALL_EMPLOYEES = "getAllEmployees";
    public static final String GET_ALL_BREAKDOWNS = "getAllBreakdowns";
    public static final String GET_ALL_MACHINES = "getAllMachines";
    public static final String GET_ALL_MACHINES_BY_IDS = "getAllMachinesByIds";
    public static final String GET_ALL_MACHINE_TYPES = "getAllMachinesTypes";
    public static final String GET_ALL_QUALIFICATIONS = "getAllQualifications";
    public static final String GET_ALL_QUALIFICATIONS_WITH_EXPIRATION_TIME = "getAllQualificationsWithExpirationTime";

    public static UUID getCurrentCompanyId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getCompanyId();
        }
        log.warn("SecurityContext missing or invalid; using fallback company ID.");
        return UUID.fromString("00000000-0000-0000-0000-000000000000");
    }
}
