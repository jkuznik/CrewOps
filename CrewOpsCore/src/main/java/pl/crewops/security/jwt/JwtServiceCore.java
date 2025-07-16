package pl.crewops.security.jwt;

import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.crewops.security.config.SecurityConfigProperties;
import pl.crewops.security.custom.UserPrincipal;

@Slf4j
@Service
public class JwtServiceCore extends JwtClaimsProvider {

    public JwtServiceCore(SecurityConfigProperties securityConfigProperties) {
        super(securityConfigProperties);
    }

    public boolean validToken(String token, UserDetails userDetails) {
        final UUID userName = extractEmployeeId(token);
        UserPrincipal principal = (UserPrincipal) userDetails;
        return (userName.equals(principal.getAuthUser().getEmployeeId()) && !isTokenExpired(token));
    }
}
