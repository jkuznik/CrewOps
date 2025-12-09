package pl.crewops.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Component
public class JwtExceptionResolver implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(
            HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try {
            if (ex instanceof ExpiredJwtException) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Token expired");
            } else if (ex instanceof MalformedJwtException) {
                response.sendError(HttpStatus.FORBIDDEN.value(), "Invalid JWT token");
            } else {
                response.sendError(HttpStatus.FORBIDDEN.value(), "Authentication error");
            }
        } catch (IOException e) {
            log.warn("Error while resolving jwt exception", e);
        }
        return new ModelAndView();
    }
}
