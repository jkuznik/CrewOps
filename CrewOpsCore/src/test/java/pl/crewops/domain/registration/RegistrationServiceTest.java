package pl.crewops.domain.registration;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.crewops.domain.registration.RegistrationTestFactory.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.crewops.IntegrationTest;
import pl.crewops.model.Employee;
import pl.crewops.model.publicSchema.Tenant;

class RegistrationServiceTest extends IntegrationTest {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void registerCustomer() {}

    @Test
    void registerCustomer_happyPathShouldSaveNewCustomerAndNewSchema() {
        // given
        var createCustomerCommand = RegistrationTestFactory.createCustomerCommand();
        Tenant tenant = new Tenant();

        try {
            // when
            var result = registrationService.registerCustomer(createCustomerCommand);
            tenant = tenantAPI.getByCompanyId(
                    result.authUserResult().authUserDTO().tenant().id());
            Employee employee = employeeAPI.getEmployeeById(
                    result.authUserResult().authUserDTO().employeeId());

            boolean schemaExists = schemaExists(tenant.getSchemaName());

            // then
            assertThat(result).isNotNull();
            assertThat(result.authUserResult().authUserDTO().tenant().active()).isTrue();
            assertThat(schemaExists).isTrue();
            assertThat(tenant.getCompanyId()).isInstanceOf(UUID.class);
        } finally {
            cleanup(tenant.getSchemaName(), FIRST_NAME, tenant.getId());
        }
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

    private void cleanup(String schemaName, String employeeFirstName, UUID tenantId) {
        jdbcTemplate.update("DELETE FROM public.auth_user WHERE username = ?", employeeFirstName);
        jdbcTemplate.update("DELETE FROM public.tenant WHERE id = ?", tenantId);
        jdbcTemplate.execute("DROP SCHEMA IF EXISTS " + schemaName + " CASCADE");
    }

    @Test
    void testRegisterCustomer() {}
}
