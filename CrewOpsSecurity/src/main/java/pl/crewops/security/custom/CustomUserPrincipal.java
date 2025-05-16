package pl.crewops.security.custom;

import org.springframework.security.core.userdetails.UserDetails;

public interface CustomUserPrincipal extends UserDetails {

    String getFirstName();

    String getLastName();
}
