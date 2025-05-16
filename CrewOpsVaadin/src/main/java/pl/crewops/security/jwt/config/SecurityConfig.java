package pl.crewops.security.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // TODO: configure this as fast as possible

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .formLogin()
                .disable()
                .httpBasic()
                .disable(); // możesz zostawić włączone, jeśli chcesz testować z Postmanem

        return http.build();
    }
}
