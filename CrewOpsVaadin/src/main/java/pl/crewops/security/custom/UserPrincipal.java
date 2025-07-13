package pl.crewops.security.custom;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import pl.crewops.model.auth.RoleGrantedAuthority;

@RequiredArgsConstructor
public class UserPrincipal implements CustomUserPrincipal {

    private final String username;
    private final UUID companyId;
    private final Set<RoleGrantedAuthority> grantedAuthorities;

    @Getter
    @Setter
    private UUID employeeId;

    @Getter
    @Setter
    private String token;

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public UUID getCompanyId() {
        return companyId;
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
