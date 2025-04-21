package pl.crewops.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import pl.crewops.domain.employee.EmployeeAPI;
import pl.crewops.security.custom.UserPrincipal;

@Configuration
@RequiredArgsConstructor
public class UserDetailsServiceConfig {

    private final EmployeeAPI employeeAPI;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> new UserPrincipal(employeeAPI.getEmployeeByUsername(username));
    }
}
