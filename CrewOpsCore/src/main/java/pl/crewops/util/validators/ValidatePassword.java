package pl.crewops.util.validators;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidatePassword {

    String passwordHolder() default "currentPassword";
}
