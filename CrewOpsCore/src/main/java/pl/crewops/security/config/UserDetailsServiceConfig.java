package pl.crewops.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.crewops.domain.auth.AuthAPI;
import pl.crewops.security.custom.UserPrincipal;

@Configuration
@RequiredArgsConstructor
public class UserDetailsServiceConfig {

    private final AuthAPI authAPI;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> new UserPrincipal(
                authAPI.getByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username)));
    }
}
