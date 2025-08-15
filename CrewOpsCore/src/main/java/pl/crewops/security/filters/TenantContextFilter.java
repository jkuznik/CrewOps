package pl.crewops.security.filters;

import static pl.crewops.enums.ControllerURL.isPublicUrl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.crewops.domain.tenant.TenantAPI;
import pl.crewops.infrastructure.multitenancy.TenantContext;
import pl.crewops.model.publicSchema.Tenant;
import pl.crewops.security.custom.CustomAuthentication;
import pl.crewops.security.custom.CustomAuthenticationManager;
import pl.crewops.security.custom.UserPrincipal;
import pl.crewops.security.jwt.JwtServiceCore;

@Slf4j
@Component
@RequiredArgsConstructor
public class TenantContextFilter extends OncePerRequestFilter {

    private final CustomAuthenticationManager authenticationManager;
    private final JwtServiceCore jwtService;
    private final TenantAPI tenantAPI;

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
            Tenant tenant = tenantAPI.getByCompanyId(jwtService.extractCompanyId(token));
            if (tenant.getSchemaName().equals(schemaName)) {
                TenantContext.setCurrentTenant(schemaName);
                authenticationManager.authenticate(
                        authentication); // authentication set true only once in whole filter chain in this place
            } else {
                authentication.setAuthenticated(false);
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
