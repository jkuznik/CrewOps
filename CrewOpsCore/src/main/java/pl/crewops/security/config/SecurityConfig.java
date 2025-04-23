package pl.crewops.security.config;

import static pl.crewops.auth.RoleType.*;
import static pl.crewops.enums.ControllerURL.*;

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
import pl.crewops.security.jwt.JwtAuthFilter;

@Configuration
@AllArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    //    private final ClientValidationFilter clientValidationFilter;

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
                        .hasAnyRole(SHIFT_LEADER.name(), MANAGER.name(), ADMIN.name())
                        // manager permission
                        .requestMatchers(HttpMethod.POST, managerUrlPOST())
                        .hasAnyRole(MANAGER.name(), ADMIN.name())
                        .requestMatchers(HttpMethod.PATCH, managerUrlPATCH())
                        .hasAnyRole(MANAGER.name(), ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE, managerUrlDELETE())
                        .hasAnyRole(MANAGER.name(), ADMIN.name())
                        //
                        .anyRequest()
                        .authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers ->
                        headers.frameOptions(Customizer.withDefaults()).disable())
                .sessionManagement(sessionManagementConfigurer ->
                        sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*"); // Allow all origins
        corsConfiguration.addAllowedMethod("*"); // Allow all methods
        corsConfiguration.addAllowedHeader("*"); // Allow all headers

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration); // Apply CORS to all URLs
        return source;
    }
}
