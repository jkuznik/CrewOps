package pl.crewops.domain.tenant;

import pl.crewops.dto.tenant.TenantDTO;
import pl.crewops.model.publicSchema.Tenant;

class TenantMapper {

    public static TenantDTO mapToDTO(Tenant tenant) {
        return TenantDTO.builder()
                .id(tenant.getId())
                .companyId(tenant.getCompanyId())
                .status(tenant.getStatus())
                .build();
    }
}
