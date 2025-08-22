package pl.crewops.util;

import static pl.crewops.model.auth.RoleType.*;

import java.util.Set;
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

    public boolean principalIsAuthenticated() {
        return getAuthenticationPrincipal() instanceof UserPrincipal;
    }

    public boolean principalHasOnlyEmployeePermission() {
        if (getAuthenticationPrincipal() instanceof UserPrincipal principal && tokenIsValid(principal)) {

            Set<RoleGrantedAuthority> roleGrantedAuthorities = jwtService.extractAuthorities(principal.getToken());

            return roleGrantedAuthorities.size() == 1
                    && roleGrantedAuthorities.contains(new RoleGrantedAuthority(EMPLOYEE));
        } else {
            return false;
        }
    }

    public boolean principalHasManagerPermission() {
        if (getAuthenticationPrincipal() instanceof UserPrincipal principal && tokenIsValid(principal)) {

            Set<RoleGrantedAuthority> roleGrantedAuthorities = jwtService.extractAuthorities(principal.getToken());

            return roleGrantedAuthorities.contains(new RoleGrantedAuthority(MANAGER))
                    || roleGrantedAuthorities.contains(new RoleGrantedAuthority(COMPANY_ADMIN))
                    || roleGrantedAuthorities.contains(new RoleGrantedAuthority(SYSTEM_ADMIN));
        } else {
            return false;
        }
    }

    public boolean principalHasCompanyAdminPermission() {
        if (getAuthenticationPrincipal() instanceof UserPrincipal principal && tokenIsValid(principal)) {
            Set<RoleGrantedAuthority> roleGrantedAuthorities = jwtService.extractAuthorities(principal.getToken());

            return roleGrantedAuthorities.contains(new RoleGrantedAuthority(COMPANY_ADMIN))
                    || roleGrantedAuthorities.contains(new RoleGrantedAuthority(SYSTEM_ADMIN));
        } else {
            return false;
        }
    }

    public boolean principalHasSystemAdminPermission() {
        if (getAuthenticationPrincipal() instanceof UserPrincipal principal && tokenIsValid(principal)) {

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

    private boolean tokenIsValid(UserPrincipal principal) {
        return jwtService.validToken(principal.getToken());
    }

    private Object getAuthenticationPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? new Object() : authentication.getPrincipal();
    }
}
