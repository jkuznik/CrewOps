package pl.crewops.security.filters;

import static pl.crewops.enums.ControllerURL.isPublicUrl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.crewops.security.config.SecurityConfigProperties;

@Slf4j
@Component
@AllArgsConstructor
public class ClientValidationFilter extends OncePerRequestFilter {

    private final SecurityConfigProperties securityConfigProperties;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        SecurityContextHolder.getContext().setAuthentication(null);
        SecurityContextHolder.clearContext();

        if (isPublicUrl(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }
        String clientId = request.getHeader("Client-Id");
        if (passwordEncoder.matches(clientId, securityConfigProperties.getClientId())) {
            filterChain.doFilter(request, response);
        } else {
            log.error("Client authentication failed");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Client ID");
        }
    }
}
