package pl.crewops.security.filters;

import static pl.crewops.enums.ControllerURL.isPublicUrl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.crewops.exception.domain.auth.AuthUserNotFoundException;
import pl.crewops.security.custom.CustomAuthentication;
import pl.crewops.security.custom.UserPrincipal;
import pl.crewops.security.jwt.JwtExceptionResolver;
import pl.crewops.security.jwt.JwtServiceCore;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtServiceCore jwtService;
    private final JwtExceptionResolver jwtExceptionResolver;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (isPublicUrl(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            log.info("Jwt filter authentication starting for {}", request.getRequestURI());
            final String token = jwtService.extractTokenFromRequest(request);
            final String employeeId = jwtService.extractEmployeeId(token).toString();
            if (employeeId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserPrincipal userPrincipal = (UserPrincipal) userDetailsService.loadUserByUsername(employeeId);
                if (jwtService.validToken(token, userPrincipal)) {
                    SecurityContextHolder.getContext().setAuthentication(new CustomAuthentication(userPrincipal));
                }
            } else {
                throw new AuthUserNotFoundException(employeeId);
            }
        } catch (Exception e) {
            log.error("Jwt Auth Filter - error", e);
            jwtExceptionResolver.resolveException(request, response, null, e);
        }
        filterChain.doFilter(request, response);
    }
}
