package pl.crewops.security.custom;

import java.util.UUID;
import org.springframework.security.core.userdetails.UserDetails;

public interface CustomUserPrincipal extends UserDetails {

    String getFirstName();

    String getLastName();

    String getTenantName();

    UUID getEmployeeId();
}
