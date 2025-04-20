package pl.crewops.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.crewops.security.custom.CustomAuthentication;
import pl.crewops.security.custom.CustomAuthenticationManager;
import pl.crewops.security.custom.UserPrincipal;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    private final CustomAuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String token = jwtService.extractTokenFromRequest(request);
        final String username = jwtService.extractUserFirstName(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserPrincipal userPrincipal = (UserPrincipal) userDetailsService.loadUserByUsername(username);
            if (jwtService.validateToken(token, userPrincipal)) {
                //                UsernamePasswordAuthenticationToken authentication = new
                // UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
                //
                //                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                //
                //                SecurityContextHolder.getContext().setAuthentication(authentication);

                CustomAuthentication customAuthentication = new CustomAuthentication(userPrincipal, request);
                Authentication authenticate = authenticationManager.authenticate(customAuthentication);

                SecurityContextHolder.getContext().setAuthentication(authenticate);
            }
        }

        filterChain.doFilter(request, response);
    }
}
