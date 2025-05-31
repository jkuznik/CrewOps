package pl.crewops.security.custom;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import pl.crewops.model.auth.AuthUser;
import pl.crewops.model.auth.RoleGrantedAuthority;

public class UserPrincipal implements CustomUserPrincipal {

    @Getter
    private final AuthUser authUser;

    private final Set<GrantedAuthority> grantedAuthorities;

    public UserPrincipal(AuthUser authUser) {
        this.authUser = authUser;
        Set<GrantedAuthority> grantedAuthoritiesSet = new HashSet<>();
        authUser.getRoles()
                .forEach(role -> grantedAuthoritiesSet.add(new RoleGrantedAuthority("ROLE_" + role.getName())));
        this.grantedAuthorities = grantedAuthoritiesSet;
    }

    @Override
    public String getFirstName() {
        return authUser.getEmployee().getFirstName();
    }

    @Override
    public String getLastName() {
        return authUser.getEmployee().getLastName();
    }

    @Override
    public UUID getId() {
        return authUser.getEmployee().getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getUsername() {
        return authUser.getUsername();
    }

    @Override
    public String getPassword() {
        return authUser.getPassword();
    }
}
