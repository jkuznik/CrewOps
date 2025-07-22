package pl.crewops.security.custom;

import java.util.UUID;
import org.springframework.security.core.userdetails.UserDetails;

public interface CustomUserPrincipal extends UserDetails {

    UUID getCompanyId();

    UUID getEmployeeId();
}
