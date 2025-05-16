package pl.crewops.security.custom;

import java.util.Collection;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import pl.crewops.security.jwt.JwtService;

@AllArgsConstructor
public class UserPrincipal implements CustomUserPrincipal {

    private final String token;
    private final JwtService jwtService;

    private final Set<GrantedAuthority> grantedAuthorities;

    public UserPrincipal(JwtService jwtService, String token, Set<GrantedAuthority> grantedAuthorities) {
        this.jwtService = jwtService;
        this.token = token;
        this.grantedAuthorities = grantedAuthorities;
    }

    @Override
    public String getFirstName() {
        return jwtService.extractFirstName(token);
    }

    @Override
    public String getLastName() {
        return jwtService.extractLastName(token);
    }

    @Override
    public String getUsername() {
        return jwtService.extractUsername(token);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return jwtService.extractAuthorities(token);
    }

    @Override
    public String getPassword() {
        return "";
    }
}
