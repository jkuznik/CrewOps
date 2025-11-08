package pl.crewops.util;

import static pl.crewops.model.auth.RoleType.*;

import java.util.Date;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.crewops.model.auth.RoleGrantedAuthority;
import pl.crewops.security.custom.UserPrincipal;
import pl.crewops.security.jwt.JwtServiceVaadin;

@Component
@RequiredArgsConstructor
public class AuthenticationResolver {

    private final JwtServiceVaadin jwtService;

    public UUID extractCompanyIdFromToken(String token) {
        return jwtService.extractCompanyId(token);
    }

    public UUID extractEmployeeIdFromToken(String token) {
        return jwtService.extractEmployeeId(token);
    }

    public Date extractExpiresAtFromToken(String token) {
        return jwtService.extractExpiresAt(token);
    }

    public Set<RoleGrantedAuthority> extractAuthoritiesFromToken(String token) {
        return jwtService.extractAuthorities(token);
    }

    public boolean principalIsAuthenticated() {
        return getAuthenticationPrincipal() instanceof UserPrincipal;
    }

    public boolean principalHasOnlyEmployeePermission() {
        if (getAuthenticationPrincipal() instanceof UserPrincipal principal) {

            Set<RoleGrantedAuthority> roleGrantedAuthorities = jwtService.extractAuthorities(principal.getToken());

            return roleGrantedAuthorities.size() == 1
                    && roleGrantedAuthorities.contains(new RoleGrantedAuthority(EMPLOYEE));
        } else {
            return false;
        }
    }

    public boolean principalHasMechanicPermission() {
        if (getAuthenticationPrincipal() instanceof UserPrincipal principal) {

            Set<RoleGrantedAuthority> roleGrantedAuthorities = jwtService.extractAuthorities(principal.getToken());

            return roleGrantedAuthorities.contains(new RoleGrantedAuthority(MECHANIC))
                    || roleGrantedAuthorities.contains(new RoleGrantedAuthority(SHIFT_LEADER))
                    || roleGrantedAuthorities.contains(new RoleGrantedAuthority(MANAGER))
                    || roleGrantedAuthorities.contains(new RoleGrantedAuthority(COMPANY_ADMIN))
                    || roleGrantedAuthorities.contains(new RoleGrantedAuthority(SYSTEM_ADMIN));
        } else {
            return false;
        }
    }

    public boolean principalHasShiftLeaderPermission() {
        if (getAuthenticationPrincipal() instanceof UserPrincipal principal) {

            Set<RoleGrantedAuthority> roleGrantedAuthorities = jwtService.extractAuthorities(principal.getToken());

            return roleGrantedAuthorities.contains(new RoleGrantedAuthority(SHIFT_LEADER))
                    || roleGrantedAuthorities.contains(new RoleGrantedAuthority(MANAGER))
                    || roleGrantedAuthorities.contains(new RoleGrantedAuthority(COMPANY_ADMIN))
                    || roleGrantedAuthorities.contains(new RoleGrantedAuthority(SYSTEM_ADMIN));
        } else {
            return false;
        }
    }

    public boolean principalHasManagerPermission() {
        if (getAuthenticationPrincipal() instanceof UserPrincipal principal) {

            Set<RoleGrantedAuthority> roleGrantedAuthorities = jwtService.extractAuthorities(principal.getToken());

            return roleGrantedAuthorities.contains(new RoleGrantedAuthority(MANAGER))
                    || roleGrantedAuthorities.contains(new RoleGrantedAuthority(COMPANY_ADMIN))
                    || roleGrantedAuthorities.contains(new RoleGrantedAuthority(SYSTEM_ADMIN));
        } else {
            return false;
        }
    }

    public boolean principalHasCompanyAdminPermission() {
        if (getAuthenticationPrincipal() instanceof UserPrincipal principal) {
            Set<RoleGrantedAuthority> roleGrantedAuthorities = jwtService.extractAuthorities(principal.getToken());

            return roleGrantedAuthorities.contains(new RoleGrantedAuthority(COMPANY_ADMIN))
                    || roleGrantedAuthorities.contains(new RoleGrantedAuthority(SYSTEM_ADMIN));
        } else {
            return false;
        }
    }

    public boolean principalHasSystemAdminPermission() {
        if (getAuthenticationPrincipal() instanceof UserPrincipal principal) {

            Set<RoleGrantedAuthority> roleGrantedAuthorities = jwtService.extractAuthorities(principal.getToken());

            return roleGrantedAuthorities.contains(new RoleGrantedAuthority(SYSTEM_ADMIN));
        } else {
            return false;
        }
    }

    public UserPrincipal getPrincipal() {
        if (getAuthenticationPrincipal() instanceof UserPrincipal principal) {
            return principal;
        }
        return null;
    }

    public void unauthenticatePrincipal() {
        SecurityContextHolder.getContext().setAuthentication(null);
        SecurityContextHolder.clearContext();
    }

    private Object getAuthenticationPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? new Object() : authentication.getPrincipal();
    }
}
