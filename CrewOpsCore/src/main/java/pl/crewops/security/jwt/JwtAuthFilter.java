package pl.crewops.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.crewops.domain.employee.EmployeeAPI;
import pl.crewops.security.UserSecurity;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final EmployeeAPI employeeAPI;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String token = jwtService.extractTokenFromRequest(request);
        final String username = jwtService.extractUserFirstName(token);

        UserSecurity userSecurity = new UserSecurity(employeeAPI.getEmployeeByUsername(username));
    }
}
