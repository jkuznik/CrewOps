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
public class RoleResolver {

    private final JwtServiceVaadin jwtService;

    public boolean principalIsAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    public boolean principalHasOnlyEmployeeRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isAuthenticated(authentication)
                && authentication.getPrincipal() instanceof UserPrincipal principal
                && tokenIsValid(principal)) {

            Set<RoleGrantedAuthority> roleGrantedAuthorities = jwtService.extractAuthorities(principal.getToken());

            return roleGrantedAuthorities.size() == 1
                    && roleGrantedAuthorities.contains(new RoleGrantedAuthority(EMPLOYEE));
        } else {
            return false;
        }
    }

    public boolean principalHasManagerRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isAuthenticated(authentication)
                && authentication.getPrincipal() instanceof UserPrincipal principal
                && tokenIsValid(principal)) {

            Set<RoleGrantedAuthority> roleGrantedAuthorities = jwtService.extractAuthorities(principal.getToken());

            return roleGrantedAuthorities.contains(new RoleGrantedAuthority(MANAGER))
                    || roleGrantedAuthorities.contains(new RoleGrantedAuthority(COMPANY_ADMIN))
                    || roleGrantedAuthorities.contains(new RoleGrantedAuthority(SYSTEM_ADMIN));
        } else {
            return false;
        }
    }

    public boolean principalHasCompanyAdminRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isAuthenticated(authentication)
                && authentication.getPrincipal() instanceof UserPrincipal principal
                && tokenIsValid(principal)) {
            Set<RoleGrantedAuthority> roleGrantedAuthorities = jwtService.extractAuthorities(principal.getToken());

            return roleGrantedAuthorities.contains(new RoleGrantedAuthority(COMPANY_ADMIN))
                    || roleGrantedAuthorities.contains(new RoleGrantedAuthority(SYSTEM_ADMIN));
        } else {
            return false;
        }
    }

    public boolean principalHasSystemAdminRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isAuthenticated(authentication)
                && authentication.getPrincipal() instanceof UserPrincipal principal
                && tokenIsValid(principal)) {

            Set<RoleGrantedAuthority> roleGrantedAuthorities = jwtService.extractAuthorities(principal.getToken());

            return roleGrantedAuthorities.size() == 1
                    && roleGrantedAuthorities.contains(new RoleGrantedAuthority(SYSTEM_ADMIN));
        } else {
            return false;
        }
    }

    public UserPrincipal getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isAuthenticated(authentication) && authentication.getPrincipal() instanceof UserPrincipal principal) {
            return principal;
        }
        return null;
    }

    public void unauthenticatePrincipal() {
        SecurityContextHolder.clearContext();
    }

    private boolean tokenIsValid(UserPrincipal principal) {
        return jwtService.validToken(principal.getToken());
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated();
    }
}
