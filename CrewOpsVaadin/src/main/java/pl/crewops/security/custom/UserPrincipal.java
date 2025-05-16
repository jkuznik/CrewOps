package pl.crewops.security.custom;

import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import pl.crewops.security.jwt.JwtService;

@AllArgsConstructor
public class UserPrincipal implements CustomUserPrincipal {

    private final String token;
    private final JwtService jwtService;

    //    private final String firstName;
    //    private final String lastName;

    @Override
    public String getFirstName() {
        return "";
    }

    @Override
    public String getLastName() {
        return "";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return "";
    }

    @Override
    public String getPassword() {
        return "";
    }
}
