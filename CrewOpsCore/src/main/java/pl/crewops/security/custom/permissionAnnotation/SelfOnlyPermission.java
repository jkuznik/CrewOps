package pl.crewops.security.custom.permissionAnnotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SelfOnlyPermission {

    String identifier() default "employeeId";
}
