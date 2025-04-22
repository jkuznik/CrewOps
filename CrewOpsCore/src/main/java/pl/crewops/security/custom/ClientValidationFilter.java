// package pl.crewops.security.custom;
//
// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import java.io.IOException;
// import lombok.AllArgsConstructor;
// import org.springframework.stereotype.Component;
// import org.springframework.web.filter.OncePerRequestFilter;
// import pl.crewops.security.config.SecurityConfigProperties;
//
// @Component
// @AllArgsConstructor
// public class ClientValidationFilter extends OncePerRequestFilter {
//
//    private final SecurityConfigProperties securityConfigProperties;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String clientId = request.getHeader("Client-Id");
//
//        if (!securityConfigProperties.getClientId().equals(clientId)) {
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Client ID");
//            return;
//        }
//
//        filterChain.doFilter(request, response);
//    }
// }
