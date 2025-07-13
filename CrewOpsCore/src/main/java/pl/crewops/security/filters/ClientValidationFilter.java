package pl.crewops.security.filters;

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
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (isPublicUrl(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("Client authentication starting");
        String clientId = request.getHeader("Client-Id");
        if (passwordEncoder.matches(clientId, securityConfigProperties.getClientId())) {
            log.info("Client validation succesful");
            filterChain.doFilter(request, response);
        } else {
            log.error("Client authentication failed");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Client ID");
        }
    }

    private boolean isPublicUrl(String requestURI) {
        return Arrays.stream(ControllerURL.publicUrl()).anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }
}
