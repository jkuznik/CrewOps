package pl.crewops.dto.tenant;

import java.util.UUID;
import lombok.Builder;
import pl.crewops.enums.TenantStatus;

@Builder
public record TenantDTO(UUID id, String name, boolean active, TenantStatus status) {}
