package pl.crewops.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.crewops.domain.tenant.TenantAPI;
import pl.crewops.enums.ControllerURL;
import pl.crewops.infrastructure.multitenancy.TenantContext;
import pl.crewops.model.publicSchema.Tenant;
import pl.crewops.security.custom.CustomAuthentication;
import pl.crewops.security.custom.CustomAuthenticationManager;
import pl.crewops.security.custom.UserPrincipal;
import pl.crewops.security.jwt.JwtExceptionResolver;
import pl.crewops.security.jwt.JwtService;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final JwtExceptionResolver jwtExceptionResolver;
    private final UserDetailsService userDetailsService;
    private final CustomAuthenticationManager authenticationManager;
    private final TenantAPI tenantAPI;

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        TenantContext.clear();
        if (isPublicUrl(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            log.info("Jwt filter authentication starting");
            final String token = jwtService.extractTokenFromRequest(request);
            final String tenantCompanyId = jwtService.extractTenantCompanyId(token);
            Tenant tenant = tenantAPI.getByCompanyId(UUID.fromString(tenantCompanyId));
            TenantContext.setCurrentTenant(tenant.getSchemaName());
            final String username = jwtService.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserPrincipal userPrincipal = (UserPrincipal) userDetailsService.loadUserByUsername(username);
                if (jwtService.validateToken(token, userPrincipal)) {
                    CustomAuthentication customAuthentication = new CustomAuthentication(userPrincipal);
                    Authentication authenticate = authenticationManager.authenticate(customAuthentication);
                    SecurityContextHolder.getContext().setAuthentication(authenticate);
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
