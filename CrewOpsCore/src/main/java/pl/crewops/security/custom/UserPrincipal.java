package pl.crewops.security.custom;

import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.crewops.model.auth.AuthUser;
import pl.crewops.model.auth.RoleGrantedAuthority;

@RequiredArgsConstructor
public class UserPrincipal implements UserDetails {

    private final AuthUser authUser;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (authUser.getRoles() == null) {
            return List.of();
        }
        return authUser.getRoles().stream().map(RoleGrantedAuthority::new).toList();
    }

    // TODO: change this implementation after this PoC. To do that Employee entity have to be updated to store some
    // credentials
    @Override
    public String getUsername() {
        return authUser.getUsername();
    }

    @Override
    public String getPassword() {
        return authUser.getPassword();
    }
}
