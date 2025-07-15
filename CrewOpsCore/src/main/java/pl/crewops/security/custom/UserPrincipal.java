package pl.crewops.security.custom;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import pl.crewops.model.auth.RoleGrantedAuthority;
import pl.crewops.model.auth.RoleType;
import pl.crewops.model.publicSchema.AuthUser;

public class UserPrincipal implements CustomUserPrincipal {

    @Getter
    private final AuthUser authUser;

    private final Set<GrantedAuthority> grantedAuthorities;

    public UserPrincipal(AuthUser authUser) {
        this.authUser = authUser;
        Set<GrantedAuthority> grantedAuthoritiesSet = new HashSet<>();
        authUser.getRoles()
                .forEach(role -> grantedAuthoritiesSet.add(
                        new RoleGrantedAuthority(RoleType.valueOf(role.getName().replace("ROLE_", "")))));
        this.grantedAuthorities = grantedAuthoritiesSet;
    }

    @Override
    public UUID getCompanyId() {
        return authUser.getTenant().getCompanyId();
    }

    @Override
    public UUID getEmployeeId() {
        return authUser.getEmployeeId();
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
