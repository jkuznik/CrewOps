package pl.crewops.security.config;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.crewops.domain.auth.AuthAPI;
import pl.crewops.model.publicSchema.AuthUser;
import pl.crewops.security.custom.UserPrincipal;

@Configuration
@RequiredArgsConstructor
public class UserDetailsServiceConfig {

    private final AuthAPI authAPI;

    @Bean
    public UserDetailsService userDetailsService() {
        return employeeId -> {
            AuthUser authUser = authAPI.getByEmployeeId(UUID.fromString(employeeId))
                    .orElseThrow(() -> new UsernameNotFoundException(employeeId));
            return new UserPrincipal(authUser);
        };
    }
}
