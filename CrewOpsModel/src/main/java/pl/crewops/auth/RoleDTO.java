package pl.crewops.auth;

import java.util.UUID;
import lombok.Builder;

@Builder
public record RoleDTO(UUID id, String name) {}
