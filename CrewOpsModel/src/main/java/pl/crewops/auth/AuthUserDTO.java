package pl.crewops.auth;

import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import pl.crewops.dto.tenant.TenantDTO;

@Builder
public record AuthUserDTO(
        UUID id, String username, String password, UUID employeeId, Set<RoleDTO> roles, TenantDTO tenant) {}
