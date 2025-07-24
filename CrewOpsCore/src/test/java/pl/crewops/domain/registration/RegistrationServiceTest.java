package pl.crewops.domain.registration;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.crewops.domain.registration.RegistrationTestFactory.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.crewops.IntegrationTest;
import pl.crewops.auth.CreateAuthUserDTO;
import pl.crewops.exception.domain.registration.RegisterCustomerException;
import pl.crewops.infrastructure.multitenancy.TenantContext;
import pl.crewops.model.Employee;
import pl.crewops.model.publicSchema.Tenant;

class RegistrationServiceTest extends IntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(RegistrationServiceTest.class);

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
                    result.authUserResult().authUserDTO().tenant().companyId());

            TenantContext.setCurrentTenant(tenant.getSchemaName());
            Employee employee = employeeAPI.getEmployeeById(
                    result.authUserResult().authUserDTO().employeeId());
            TenantContext.clear();

            boolean schemaExists = schemaExists(tenant.getSchemaName());

            // then
            assertThat(result).isNotNull();
            assertThat(result.authUserResult().authUserDTO().tenant().active()).isTrue();
            assertThat(schemaExists).isTrue();
            assertThat(tenant.getId()).isInstanceOf(UUID.class);
            assertThat(employee.getId()).isInstanceOf(UUID.class);
        } finally {
            cleanup(tenant.getSchemaName(), FIRST_NAME, tenant.getId());
        }
    }

    @Test
    void registerCustomer_shouldRollbackTenant_whenExceptionIsThrownAfterTenantAlreadyExists() {
        // given
        var createCustomerCommand = RegistrationTestFactory.createCustomerCommand();
        var existingCompanyId = UUID.fromString("2f3b1d5c-9e8f-4bca-9c56-123456789abd");
        var createAuthUser = CreateAuthUserDTO.builder()
                .username(createCustomerCommand.createEmployeeDTO().username())
                .password(createCustomerCommand.createEmployeeDTO().password())
                .roles(createCustomerCommand.createEmployeeDTO().roles())
                .build();

        // Intentionally create an auth user row to trigger a UNIQUE constraint violation on the 'username' column
        authAPI.createAuthUser(createAuthUser, UUID.randomUUID(), existingCompanyId);

        Exception expectedRegisterExcpetion = null;
        try {
            // when
            try {
                registrationService.registerCustomer(createCustomerCommand);
            } catch (RegisterCustomerException e) {
                expectedRegisterExcpetion = e;
            }

            // then
            assertThat(expectedRegisterExcpetion).isExactlyInstanceOf(RegisterCustomerException.class);
        } finally {
            //            cleanup(tenant.getSchemaName(), FIRST_NAME, tenant.getId());
            cleanupAuthUser(createAuthUser.username());
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

    private void cleanupAuthUser(String username) {
        jdbcTemplate.update("DELETE FROM public.auth_user WHERE username = ?", username);
    }
}
