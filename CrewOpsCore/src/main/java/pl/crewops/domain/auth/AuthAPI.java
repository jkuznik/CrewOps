package pl.crewops.domain.auth;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import pl.crewops.auth.*;
import pl.crewops.model.Employee;
import pl.crewops.model.auth.AuthUser;

@Component
@Validated
public interface AuthAPI {

    Optional<AuthUser> getByUsername(@NotNull String username);

    Optional<AuthUser> getByEmployee(@NotNull Employee employee);

    AuthUser createAuthUser(@NotNull @Valid CreateAuthUserDTO createAuthUserDTO, @NotNull @Valid Employee employee);

    void deleteById(@NotNull UUID uuid);

    AuthResponse login(@NotNull @Valid AuthRequest authRequest, HttpServletResponse response);

    ValidTokenResponse validateToken(@NotNull @Valid ValidTokenRequest validTokenRequest);
}
