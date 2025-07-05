package pl.crewops.domain.tenant;

import pl.crewops.dto.tenant.CreateTenantDTO;
import pl.crewops.dto.tenant.TenantDTO;
import pl.crewops.enums.TenantStatus;
import pl.crewops.model.publicSchema.Tenant;

class TenantMapper {

    public static Tenant mapToEntity(CreateTenantDTO createTenantDTO) {
        return Tenant.builder()
                .name(createTenantDTO.name())
                .active(true)
                .status(TenantStatus.TRIAL)
                .build();
    }

    public static TenantDTO mapToDTO(Tenant tenant) {
        return TenantDTO.builder()
                .id(tenant.getId())
                .name(tenant.getName())
                .active(tenant.isActive())
                .status(tenant.getStatus())
                .build();
    }
}
