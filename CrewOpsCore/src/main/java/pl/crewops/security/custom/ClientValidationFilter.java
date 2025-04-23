package pl.crewops.security.custom;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        log.info("Request URI: {}", requestURI);
        if (isPublicUrl(requestURI)) {
            log.debug("Skipping JWT authentication for: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        String clientId = request.getHeader("Client-Id");

        if (!securityConfigProperties.getClientId().equals(clientId)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Client ID");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicUrl(String requestURI) {
        return Arrays.stream(ControllerURL.publicUrl()).anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }
}
