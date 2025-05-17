package pl.crewops.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.crewops.security.custom.CustomUserPrincipal;
import pl.crewops.security.custom.UserPrincipal;

public class JwtAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token;

        try {
            token = JwtReader.extractTokenFromRequest(request);
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var username = JwtReader.getUsername(token);
            var firstName = JwtReader.getFirstName(token);
            var lastName = JwtReader.getLastName(token);
            var grantedAuthorities = JwtReader.getAuthorities(token);

            CustomUserPrincipal principal = new UserPrincipal(username, firstName, lastName, grantedAuthorities);
            Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
