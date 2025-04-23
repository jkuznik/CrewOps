package pl.crewops.auth;

import java.util.Set;
import lombok.Builder;

@Builder
public record CreateAuthUserDTO(String username, String password, Set<RoleDTO> roles) {}
