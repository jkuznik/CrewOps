package pl.crewops.model.dto.auth;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import pl.crewops.model.dto.option.AuthUserOptionDTO;
import pl.crewops.model.dto.tenant.TenantDTO;

@Builder
public record AuthUserDTO(
        UUID id,
        String username,
        String password,
        UUID employeeId,
        Set<RoleDTO> roles,
        Set<AuthUserOptionDTO> options,
        TenantDTO tenant) {
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AuthUserDTO that)) return false;
        return Objects.equals(username(), that.username());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username());
    }
}
