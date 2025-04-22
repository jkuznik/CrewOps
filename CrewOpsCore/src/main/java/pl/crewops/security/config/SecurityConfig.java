package pl.crewops.security.config;

import static pl.crewops.enums.ControllerURL.getPublicUrl;
// import pl.crewops.security.custom.ClientValidationFilter;
// import pl.crewops.security.jwt.JwtAuthFilter;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
                        .requestMatchers(getPublicUrl())
                        .permitAll()
                        .requestMatchers("/employees/**")
                        .hasRole("ADMIN")
                        .requestMatchers("/employees")
                        .hasRole("ADMIN")
                        .requestMatchers("/vehicles/**")
                        .hasRole("USER")
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
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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
