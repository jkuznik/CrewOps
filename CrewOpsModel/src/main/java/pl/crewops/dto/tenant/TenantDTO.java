package pl.crewops.dto.tenant;

import java.util.UUID;
import lombok.Builder;

@Builder
public record TenantDTO(UUID id, UUID companyId, boolean active) {}
