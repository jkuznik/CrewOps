package pl.crewops.security.custom;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import pl.crewops.domain.employee.EmployeeAPI;
import pl.crewops.model.auth.RoleGrantedAuthority;
import pl.crewops.model.publicSchema.AuthUser;
import pl.crewops.utils.spring.SpringContext;

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
        EmployeeAPI employeeAPI = SpringContext.getBean(EmployeeAPI.class);
        return employeeAPI.getEmployee(authUser.getEmployeeId()).getFirstName();
    }

    @Override
    public String getLastName() {
        EmployeeAPI employeeAPI = SpringContext.getBean(EmployeeAPI.class);
        return employeeAPI.getEmployee(authUser.getEmployeeId()).getLastName();
    }

    @Override
    public UUID getId() {
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
