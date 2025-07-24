package pl.crewops.dto.tenant;

import java.util.UUID;
import lombok.Builder;

@Builder
public record TenantDTO(UUID id, String schemaName, UUID companyId, boolean active) {}
