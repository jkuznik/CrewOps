package pl.crewops.domain.auth;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;
import pl.crewops.model.dto.auth.*;
import pl.crewops.model.dto.employee.CreateEmployeeDTO;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.model.publicSchema.AuthUser;
import pl.crewops.security.auth.AuthRequest;
import pl.crewops.security.auth.AuthResponse;
import pl.crewops.security.auth.ValidTokenRequest;
import pl.crewops.security.auth.ValidTokenResponse;

@Validated
public interface AuthAPI {

    Optional<AuthUser> getByUsername(@NotNull String username);

    Optional<AuthUser> getByEmployeeId(@NotNull UUID employeeId);

    AuthUserDTO createAuthUser(
            @NotNull @Valid CreateAuthUserDTO createAuthUserDTO, @NotNull UUID employeeId, @NotNull UUID companyId);

    AuthResponse login(@NotNull @Valid AuthRequest authRequest, HttpServletResponse response);

    AuthUserDTO updateAuthUser(@Valid @NotNull UpdateAuthUserDTO updateAuthUserDTO);

    ValidTokenResponse validateToken(@NotNull @Valid ValidTokenRequest validTokenRequest);

    CreateAuthUserResult createAuthUserWithRelatedEmployee(@NotNull @Valid CreateEmployeeDTO createEmployeeDTO);

    CreateAuthUserResult createAuthUserWithRelatedEmployeeForRegisterCustomer(
            @NotNull @Valid CreateEmployeeDTO createEmployeeDTO);

    EmployeeDTO terminateEmployeeAuthUserAccount(UUID employeeId);
}
