package pl.crewops.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.Builder;

@Builder
public record CreateAuthUserDTO(
        @Size(max = 50) @NotNull @NotBlank String username,
        @Size(max = 50) @NotNull @NotBlank String password,
        @NotNull Set<RoleDTO> roles) {}
