package pl.crewops.security.custom.permissionAnnotation.aspect;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import pl.crewops.security.custom.UserPrincipal;
import pl.crewops.security.custom.permissionAnnotation.SelfOnlyPermission;

@Slf4j
@Aspect
@Component
public class SelfOnlyAspect {

    @Before("@annotation(pl.crewops.security.custom.permissionAnnotation.SelfOnlyPermission)")
    public void validateSelfOnlyPermission(JoinPoint joinPoint) {
        var principal = (UserPrincipal)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        SelfOnlyPermission annotation = method.getAnnotation(SelfOnlyPermission.class);

        String paramName = annotation.identifier();
        Object[] args = joinPoint.getArgs();
        Parameter[] parameters = method.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            Object arg = args[i];
            if (arg == null) continue;

            if (parameters[i].getName().equals(paramName)) {
                if (!idsMatch(principal.getEmployeeId(), arg)) {
                    deny(principal.getEmployeeId(), method.getName(), arg);
                }
                return;
            }

            Object extracted = extractFieldOrGetter(arg, paramName);
            if (extracted != null) {
                if (!idsMatch(principal.getEmployeeId(), extracted)) {
                    deny(principal.getEmployeeId(), method.getName(), extracted);
                }
                return;
            }
        }

        log.error("Identifier '{}' not found in method {}", paramName, method.getName());
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
    }

    private void deny(UUID employeeId, String methodName, Object provided) {
        log.warn("Access denied for user {} in method {}. Provided identifier = {}", employeeId, methodName, provided);
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
    }

    private boolean idsMatch(UUID principalId, Object candidate) {
        if (candidate instanceof UUID uuid) {
            return principalId.equals(uuid);
        }
        if (candidate instanceof String str) {
            try {
                return principalId.equals(UUID.fromString(str));
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return false;
    }

    private Object extractFieldOrGetter(Object arg, String fieldName) {
        try {
            // first try getter
            Method getter = arg.getClass().getMethod("get" + capitalize(fieldName));
            return getter.invoke(arg);
        } catch (NoSuchMethodException ignored) {
            // fallback: direct field
            try {
                var field = arg.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(arg);
            } catch (NoSuchFieldException | IllegalAccessException ignored2) {
                return null;
            }
        } catch (Exception e) {
            log.error("Error extracting {} from {}", fieldName, arg.getClass(), e);
            return null;
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
