package pl.crewops.security.custom;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.crewops.enums.ControllerURL;
import pl.crewops.security.config.SecurityConfigProperties;

@Slf4j
@Component
@AllArgsConstructor
public class ClientValidationFilter extends OncePerRequestFilter {

    private final SecurityConfigProperties securityConfigProperties;
    private final PasswordEncoder passwordEncoder;

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        log.info("Request URI: {} - client validation filter", requestURI);
        if (isPublicUrl(requestURI)) {
            log.debug("Skipping client validation authentication for: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        String clientId = request.getHeader("Client-Id");

        if (passwordEncoder.matches(clientId, securityConfigProperties.getClientId())) {
            filterChain.doFilter(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Client ID");
        }
    }

    private boolean isPublicUrl(String requestURI) {
        return Arrays.stream(ControllerURL.publicUrl()).anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }
}
