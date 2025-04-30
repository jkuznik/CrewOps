package pl.crewops.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.crewops.enums.ControllerURL;
import pl.crewops.security.custom.CustomAuthentication;
import pl.crewops.security.custom.CustomAuthenticationManager;
import pl.crewops.security.custom.UserPrincipal;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final JwtExceptionResolver jwtExceptionResolver;
    private final UserDetailsService userDetailsService;
    private final CustomAuthenticationManager authenticationManager;

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        log.info("Request URI: {} - jwt Auth Filter", requestURI);
        if (isPublicUrl(requestURI)) {
            log.info("Skipping JWT authentication for: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        try {
            log.info("Jwt Auth Filter - starting authentication");
            final String token = jwtService.extractTokenFromRequest(request);
            final String username = jwtService.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserPrincipal userPrincipal = (UserPrincipal) userDetailsService.loadUserByUsername(username);
                if (jwtService.validateToken(token, userPrincipal)) {
                    CustomAuthentication customAuthentication = new CustomAuthentication(userPrincipal);
                    Authentication authenticate = authenticationManager.authenticate(customAuthentication);

                    SecurityContextHolder.getContext().setAuthentication(authenticate);
                    log.info("Jwt Auth Filter - authentication successful");
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Jwt Auth Filter - error", e);
            jwtExceptionResolver.resolveException(request, response, null, e);
        }
    }

    private boolean isPublicUrl(String requestURI) {
        return Arrays.stream(ControllerURL.publicUrl()).anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }
}
