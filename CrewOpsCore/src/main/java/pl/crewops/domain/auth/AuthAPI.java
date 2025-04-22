package pl.crewops.domain.auth;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import pl.crewops.model.auth.AuthUser;

@Component
@Validated
public interface AuthAPI {

    AuthUser getByUsername(@NotNull String username);
}
