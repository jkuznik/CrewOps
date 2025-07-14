package pl.crewops.security.config;

import static pl.crewops.enums.ControllerURL.*;
import static pl.crewops.model.auth.RoleType.*;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import pl.crewops.security.filters.ClientValidationFilter;
import pl.crewops.security.filters.JwtAuthFilter;
import pl.crewops.security.filters.TenantContextFilter;

@Configuration
@AllArgsConstructor
public class SecurityConfig {

    private final ClientValidationFilter clientValidationFilter;
    private final JwtAuthFilter jwtAuthFilter;
    private final TenantContextFilter tenantContextFilter;

    // TODO: configure current handling endpoint
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        // public access
                        .requestMatchers(publicUrl())
                        .permitAll()
                        // shift leader permission
                        .requestMatchers(HttpMethod.PATCH, shiftLeaderUrlPATCH())
                        .hasAnyRole(SHIFT_LEADER.name(), MANAGER.name(), SYSTEM_ADMIN.name())
                        // manager permission
                        .requestMatchers(HttpMethod.POST, managerUrlPOST())
                        .hasAnyRole(MANAGER.name(), SYSTEM_ADMIN.name())
                        .requestMatchers(HttpMethod.PATCH, managerUrlPATCH())
                        .hasAnyRole(MANAGER.name(), SYSTEM_ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE, managerUrlDELETE())
                        .hasAnyRole(MANAGER.name(), SYSTEM_ADMIN.name())
                        //
                        .anyRequest()
                        .authenticated())
                .addFilterBefore(clientValidationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(tenantContextFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers ->
                        headers.frameOptions(Customizer.withDefaults()).disable())
                .sessionManagement(sessionManagementConfigurer ->
                        sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8081"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
