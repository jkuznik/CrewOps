package pl.crewops.domain.tenant;

import java.util.UUID;
import pl.crewops.dto.tenant.CreateTenantDTO;
import pl.crewops.model.publicSchema.Tenant;

class TenantTestFactory {

    public static CreateTenantDTO createTenantDTO() {
        return CreateTenantDTO.builder().name("test").build();
    }

    public static Tenant tenant() {
        var tenant = Tenant.builder().name("test").build();
        var tenantId = UUID.randomUUID();
        tenant.setId(tenantId);

        return tenant;
    }
}
