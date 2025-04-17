package pl.crewops.security;

import static pl.crewops.enums.ControllerURL.*;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.crewops.security.jwt.JwtAuthFilter;

@Configuration
@AllArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final ClientValidationFilter clientValidationFilter;

    private static final String[] PUBLIC = getPublicUrl();
    private static final String[] ADMIN = getAdminUrl();

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.addFilterBefore(clientValidationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(PUBLIC)
                        .permitAll()
                        .requestMatchers(ADMIN)
                        .authenticated())
                .formLogin(Customizer.withDefaults())
                .build();
    }

    private static String[] getPublicUrl() {
        return new String[] {
            "/" + EMPLOYEES + "/**", "/" + VEHICLES + "/**",
        };
    }

    private static String[] getAdminUrl() {
        return new String[] {"/" + QUALIFICATIONS + "/**"};
    }
}
