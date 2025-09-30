package pl.crewops.util.validators;

import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import pl.crewops.security.custom.UserPrincipal;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ValidatePasswordAspect {

    private final PasswordEncoder passwordEncoder;

    @Before("@annotation(pl.crewops.util.validators.ValidatePassword)")
    public void validatePassword(JoinPoint joinPoint) throws IllegalAccessException {
        var principal = (UserPrincipal)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        ValidatePassword annotation = method.getAnnotation(ValidatePassword.class);

        String passwordFieldName = annotation.passwordHolder();

        for (Object arg : joinPoint.getArgs()) {
            if (arg == null) continue;

            if (arg instanceof String && passwordFieldName.equals(method.getParameters()[0].getName())) {
                validatePasswordMatch((String) arg, principal.getPassword());
                return;
            }

            String passwordCandidate = extractPasswordField(arg, passwordFieldName);
            if (passwordCandidate != null) {
                validatePasswordMatch(passwordCandidate, principal.getPassword());
                return;
            }
        }

        log.warn("No parameter '{}' found in method {} for password validation", passwordFieldName, method.getName());
    }

    private String extractPasswordField(Object arg, String fieldName) throws IllegalAccessException {
        Class<?> clazz = arg.getClass();
        try {
            var field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(arg);
            return (value instanceof String) ? (String) value : null;
        } catch (NoSuchFieldException e) {
            return null; // not found, skip
        }
    }

    private void validatePasswordMatch(String providedPassword, String encodedPassword) {
        if (!passwordEncoder.matches(providedPassword, encodedPassword)) {
            log.warn(
                    "Password validation failed for user {}",
                    SecurityContextHolder.getContext().getAuthentication().getName());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }
}
