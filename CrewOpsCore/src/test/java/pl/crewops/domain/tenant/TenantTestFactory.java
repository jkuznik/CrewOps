package pl.crewops.domain.tenant;

import java.util.UUID;
import pl.crewops.dto.address.CreateAddressDTO;
import pl.crewops.dto.company.CreateCompanyDTO;
import pl.crewops.dto.tenant.CreateTenantDTO;
import pl.crewops.model.publicSchema.Tenant;

class TenantTestFactory {

    static UUID testCompanyId = UUID.randomUUID();
    static UUID tenantId = UUID.randomUUID();

    static CreateTenantDTO createTenantDTO() {
        return CreateTenantDTO.builder()
                .createAddressDTO(CreateAddressDTO.builder()
                        .postalCode("postalCode")
                        .city("city")
                        .street("street")
                        .localNumber("localNumber")
                        .build())
                .createCompanyDTO(CreateCompanyDTO.builder()
                        .name("companyName")
                        .email("test@email.com")
                        .build())
                .build();
    }

    public static Tenant tenant() {
        var tenant = Tenant.builder().companyId(testCompanyId).build();
        tenant.setId(tenantId);

        return tenant;
    }
}
