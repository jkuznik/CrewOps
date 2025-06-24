package pl.crewops.domain.auth;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;
import pl.crewops.auth.*;
import pl.crewops.model.auth.AuthUser;

@Validated
public interface AuthAPI {

    Optional<AuthUser> getByUsername(@NotNull String username);

    Optional<AuthUser> getByEmployeeId(@NotNull UUID employeeId);

    AuthUser createAuthUser(@NotNull @Valid CreateAuthUserDTO createAuthUserDTO, @NotNull @Valid UUID employeeId);

    void deleteById(@NotNull UUID uuid);

    AuthResponse login(@NotNull @Valid AuthRequest authRequest, HttpServletResponse response);

    ValidTokenResponse validateToken(@NotNull @Valid ValidTokenRequest validTokenRequest);
}
