package pl.crewops.security.custom;

import java.util.Collection;
import java.util.Set;
import lombok.AllArgsConstructor;
import pl.crewops.model.auth.RoleGrantedAuthority;

@AllArgsConstructor
public class UserPrincipal implements CustomUserPrincipal {

    private final String username;
    private final String firstName;
    private final String lastName;
    private final Set<RoleGrantedAuthority> grantedAuthorities;

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
