package pl.crewops.security.custom;

import java.util.Collection;
import javax.security.auth.Subject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
@Getter
@Setter
public class CustomAuthentication implements Authentication {
    private final UserPrincipal principal;

    boolean authenticated = false;
    Object details;

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return principal.getAuthorities();
    }

    @Override
    public Object getCredentials() {
        return principal.getPassword();
    }

    @Override
    public Object getDetails() {
        return details;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean implies(Subject subject) {
        return Authentication.super.implies(subject);
    }

    @Override
    public String getName() {
        return "";
    }
}
