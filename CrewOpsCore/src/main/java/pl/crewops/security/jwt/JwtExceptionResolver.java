package pl.crewops.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

@Component
public class JwtExceptionResolver implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(
            HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try {
            if (ex instanceof ExpiredJwtException) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Token expired");
            } else if (ex instanceof SignatureException || ex instanceof MalformedJwtException) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid JWT token");
            } else {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Authentication error");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ModelAndView();
    }
}
