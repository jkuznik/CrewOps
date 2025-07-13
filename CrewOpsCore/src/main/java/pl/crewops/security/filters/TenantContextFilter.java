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
import pl.crewops.security.jwt.JwtService;

@Slf4j
@Component
@RequiredArgsConstructor
public class TenantContextFilter extends OncePerRequestFilter {

    private final CustomAuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TenantAPI tenantAPI;

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (isPublicUrl(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }
        log.info("TCF authorization started for {}", request.getRequestURI());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof CustomAuthentication) {
            var principal = (UserPrincipal) authentication.getPrincipal();
            String schemaName = principal.getAuthUser().getTenant().getSchemaName();
            String token = jwtService.extractTokenFromRequest(request);
            Tenant tenant = tenantAPI.getByCompanyId(UUID.fromString(jwtService.extractTenantCompanyId(token)));
            if (tenant.getSchemaName().equals(schemaName)) {
                TenantContext.setCurrentTenant(schemaName);
                authenticationManager.authenticate(
                        authentication); // authentication set true only one in filter chain in this place
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isPublicUrl(String requestURI) {
        return Arrays.stream(ControllerURL.publicUrl()).anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }
}
