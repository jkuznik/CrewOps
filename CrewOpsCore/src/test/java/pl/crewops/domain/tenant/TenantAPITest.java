package pl.crewops.domain.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.IntegrationTest;
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
        var tenantName = "brandNewTenant";
        var createTenantDTO = CreateTenantDTO.builder().name(tenantName).build();

        // when
        TenantDTO result = tenantAPI.createTenant(createTenantDTO);
        Tenant tenant = tenantAPI.getByName(tenantName);

        boolean schemaExists = schemaExists(tenant.getSchemaName());

        // then
        assertThat(result).isNotNull();
        assertThat(result.active()).isTrue();
        assertThat(schemaExists).isTrue();
        assertThat(tenant.getName()).isEqualTo(tenantName);
    }

    @Test
    void getByName() {
        // given
        var tenantName = "TestTenant"; // hardcoded tenant in development and test environment insertions

        // when
        Tenant result = tenantAPI.getByName(tenantName);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(tenantName);
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
