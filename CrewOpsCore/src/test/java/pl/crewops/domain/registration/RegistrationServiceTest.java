package pl.crewops.domain.registration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
import pl.crewops.dto.auth.CreateAuthUserDTO;
import pl.crewops.exception.domain.registration.RegisterCustomerException;
import pl.crewops.infrastructure.multitenancy.TenantContext;
import pl.crewops.model.Employee;
import pl.crewops.model.publicSchema.Tenant;
import pl.crewops.registration.CreateCustomerCommand;
import pl.crewops.util.credentialsGenerator.CredentialGenerator;

class RegistrationServiceTest extends IntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(RegistrationServiceTest.class);

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String createdSchemaName;
    private UUID createdTenantId;
    private String createdUsername;

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
            cleanup(tenant.getSchemaName(), tenant.getId());
        }
    }

    @Test
    void registerCustomer_shouldRollbackTenant_whenExceptionIsThrownAfterTenantAlreadyExists() {
        // given
        var createCustomerCommand = RegistrationTestFactory.createCustomerCommandThatBreakUniqueConstraints();
        var createAuthUser = CreateAuthUserDTO.builder()
                .username(CredentialGenerator.generateUsername("firstName", "lastName"))
                .password(CredentialGenerator.generatePassword())
                .roles(createCustomerCommand.createEmployeeDTO().roles())
                .build();

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

    @Test
    void registerCustomer_shouldCreateTenantCompanyEmployeeAndSchema() {
        // given
        CreateCustomerCommand command = RegistrationTestFactory.createCustomerCommand();

        // when
        try {
            var result = registrationService.registerCustomer(command);

            createdSchemaName = result.authUserResult().authUserDTO().tenant().schemaName();
            createdTenantId = result.authUserResult().authUserDTO().tenant().id();
            createdUsername = result.authUserResult().authUserDTO().username();

            // then
            assertThat(result).isNotNull();
            assertThat(result.companyDTO().name())
                    .isEqualTo(command.createTenantDTO().createCompanyDTO().name());

            Tenant tenant = tenantAPI.getByCompanyId(result.companyDTO().id());
            assertThat(tenant).isNotNull();
            assertThat(schemaExists(tenant.getSchemaName())).isTrue();

            // Check employee exists in tenant schema
            TenantContext.setCurrentTenant(tenant.getSchemaName());
            Employee employee = employeeAPI.getEmployeeById(
                    result.authUserResult().authUserDTO().employeeId());
            assertThat(employee.getId()).isNotNull();
            TenantContext.clear();

        } finally {
            cleanupAuthUser(createdUsername);
            cleanup(createdSchemaName, createdTenantId);
        }
    }

    @Test
    void registerCustomer_shouldRollbackTenant_whenUniqueConstraintFails() {
        // given
        CreateCustomerCommand command = RegistrationTestFactory.createCustomerCommandThatBreakUniqueConstraints();

        var createAuthUser = CreateAuthUserDTO.builder()
                .username(CredentialGenerator.generateUsername("firstName", "lastName"))
                .password(CredentialGenerator.generatePassword())
                .roles(command.createEmployeeDTO().roles())
                .build();
        createdUsername = createAuthUser.username();

        // when / then
        assertThatThrownBy(() -> {
                    registrationService.registerCustomer(command);
                })
                .isInstanceOf(RegisterCustomerException.class);
    }

    private void cleanup(String schemaName, UUID tenantId) {
        jdbcTemplate.update("DELETE FROM public.tenant WHERE id = ?", tenantId);
        jdbcTemplate.execute("DROP SCHEMA IF EXISTS " + schemaName + " CASCADE");
    }

    private void cleanupAuthUser(String username) {
        jdbcTemplate.update("DELETE FROM public.auth_user WHERE username = ?", username);
    }
}
