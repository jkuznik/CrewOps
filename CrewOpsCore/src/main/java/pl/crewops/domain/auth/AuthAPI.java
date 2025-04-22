package pl.crewops.domain.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import pl.crewops.auth.CreateAuthUserDTO;
import pl.crewops.model.Employee;
import pl.crewops.model.auth.AuthUser;

@Component
@Validated
public interface AuthAPI {

    AuthUser getByUsername(@NotNull String username);

    AuthUser create(@NotNull @Valid CreateAuthUserDTO createAuthUserDTO, @NotNull @Valid Employee employee);
}
