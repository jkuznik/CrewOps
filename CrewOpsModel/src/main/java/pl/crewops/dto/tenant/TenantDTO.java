package pl.crewops.dto.tenant;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.Builder;

@Builder
public record TenantDTO(UUID id, String schemaName, UUID companyId, String taxId, boolean active)
        implements Serializable {
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TenantDTO tenantDTO)) return false;
        return Objects.equals(id(), tenantDTO.id());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id());
    }
}
