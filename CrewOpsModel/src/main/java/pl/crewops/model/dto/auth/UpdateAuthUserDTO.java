package pl.crewops.model.dto.auth;

import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import pl.crewops.model.dto.option.AuthUserOptionDTO;

@Builder
public record UpdateAuthUserDTO(
        @NotNull UUID employeeId,
        String username,
        String password,
        Set<RoleDTO> roles,
        Set<AuthUserOptionDTO> options,
        String currentPassword) {}
