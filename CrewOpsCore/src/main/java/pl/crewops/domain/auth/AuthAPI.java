package pl.crewops.domain.auth;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;
import pl.crewops.model.dto.auth.*;
import pl.crewops.model.dto.auth.AuthRequest;
import pl.crewops.model.dto.auth.AuthResponse;
import pl.crewops.model.dto.employee.CreateEmployeeDTO;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.model.dto.option.AuthUserOptionDTO;
import pl.crewops.model.publicSchema.AuthUser;
import pl.crewops.security.ValidTokenRequest;
import pl.crewops.security.ValidTokenResponse;

@Validated
public interface AuthAPI {

    Optional<AuthUser> getByUsername(@NotNull String username);

    Optional<AuthUser> getByEmployeeId(@NotNull UUID employeeId);

    AuthUserDTO createAuthUser(
            @NotNull @Valid CreateAuthUserDTO createAuthUserDTO, @NotNull UUID employeeId, @NotNull UUID companyId);

    AuthResponse login(@NotNull @Valid AuthRequest authRequest, HttpServletResponse response);

    AuthUserDTO updateAuthUserProfile(@Valid @NotNull UpdateAuthUserDTO updateAuthUserDTO);

    AuthUserDTO updateAuthUserOptions(@Valid @NotNull UpdateAuthUserDTO updateAuthUserDTO);

    AuthUserDTO updateAuthUserRoles(@Valid @NotNull UpdateAuthUserDTO updateAuthUserDTO);

    Set<AuthUserOptionDTO> getOptionsByEmployeeId(@NotNull UUID employeeId);

    ValidTokenResponse validateToken(@NotNull @Valid ValidTokenRequest validTokenRequest);

    CreateAuthUserResult createAuthUserWithRelatedEmployee(@NotNull @Valid CreateEmployeeDTO createEmployeeDTO);

    CreateAuthUserResult createAuthUserWithRelatedEmployeeForRegisterCustomer(
            @NotNull @Valid CreateEmployeeDTO createEmployeeDTO);

    EmployeeDTO terminateEmployeeAuthUserAccount(UUID employeeId);
}
