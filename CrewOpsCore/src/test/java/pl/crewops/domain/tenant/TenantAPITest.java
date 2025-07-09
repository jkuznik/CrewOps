package pl.crewops.domain.tenant;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.IntegrationTest;
import pl.crewops.dto.address.AddressDTO;
import pl.crewops.dto.address.CreateAddressDTO;
import pl.crewops.dto.company.CreateCompanyDTO;
import pl.crewops.dto.tenant.CreateTenantDTO;
import pl.crewops.dto.tenant.TenantDTO;
import pl.crewops.model.publicSchema.Tenant;

@Transactional
class TenantAPITest extends IntegrationTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void createTenant_shouldCreateNewTenantAndNewSchema() {
        // given
        var companyName = "companyName";
        var createTenantDTO = CreateTenantDTO.builder()
                .createAddressDTO(CreateAddressDTO.builder()
                        .postalCode("postalCode")
                        .city("city")
                        .street("street")
                        .localNumber("localNumber")
                        .build())
                .createCompanyDTO(CreateCompanyDTO.builder()
                        .name(companyName)
                        .email("test@email.com")
                        .address(AddressDTO.builder()
                                .postalCode("postalCode")
                                .city("city")
                                .street("street")
                                .localNumber("localNumber")
                                .build())
                        .build())
                .build();

        // when
        TenantDTO result = tenantAPI.createTenant(createTenantDTO);
        Tenant tenant = tenantAPI.getByCompanyId(result.companyId());

        boolean schemaExists = schemaExists(tenant.getSchemaName());

        // then
        assertThat(result).isNotNull();
        assertThat(result.active()).isTrue();
        assertThat(schemaExists).isTrue();
        assertThat(tenant.getCompanyId()).isInstanceOf(UUID.class);
    }

    @Test
    void getByName() {
        // when
        Tenant result = tenantAPI.getByCompanyId(
                IntegrationTest
                        .TEST_TENANT_COMPANY_ID); // hardcoded tenant in development and test environment insertions

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCompanyId()).isEqualTo(IntegrationTest.TEST_TENANT_COMPANY_ID);
        assertThat(result.getSchemaName()).isEqualTo("testtenant_2f3b1d5c9e8f"); // hardcoded in insertions
    }

    private boolean schemaExists(String schemaName) {
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT schema_name FROM information_schema.schemata WHERE schema_name = ?")) {
            stmt.setString(1, schemaName);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to check schema existence", e);
        }
    }
}
