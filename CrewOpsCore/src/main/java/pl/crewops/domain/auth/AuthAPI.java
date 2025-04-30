package pl.crewops.domain.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import pl.crewops.auth.CreateAuthUserDTO;
import pl.crewops.model.Employee;
import pl.crewops.model.auth.AuthUser;

@Component
@Validated
public interface AuthAPI {

    Optional<AuthUser> getByUsername(@NotNull String username);

    AuthUser createAuthUser(@NotNull @Valid CreateAuthUserDTO createAuthUserDTO, @NotNull @Valid Employee employee);
}
