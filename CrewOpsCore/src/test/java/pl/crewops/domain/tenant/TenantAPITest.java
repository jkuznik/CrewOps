package pl.crewops.domain.tenant;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.IntegrationTest;
import pl.crewops.model.publicSchema.Tenant;

@Transactional
class TenantAPITest extends IntegrationTest {

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
}
