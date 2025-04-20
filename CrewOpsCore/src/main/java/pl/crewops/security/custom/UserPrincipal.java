package pl.crewops.security.custom;

import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.crewops.dto.employee.EmployeeDTO;

@RequiredArgsConstructor
public class UserPrincipal implements UserDetails {

    private final EmployeeDTO employeeDTO;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    // TODO: change this implementation after this PoC. To do that Employee entity have to be updated to store some
    // credentials
    @Override
    public String getUsername() {
        return employeeDTO.firstName();
    }

    @Override
    public String getPassword() {
        return employeeDTO.lastName();
    }
}
