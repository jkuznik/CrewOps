package pl.crewops.security.custom;

import java.util.Collection;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import pl.crewops.model.auth.RoleGrantedAuthority;

@RequiredArgsConstructor
public class UserPrincipal implements CustomUserPrincipal {

    private final String username;
    private final String firstName;
    private final String lastName;
    private final Set<RoleGrantedAuthority> grantedAuthorities;

    @Getter
    @Setter
    private String token;

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Collection<RoleGrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return "";
    }
}
