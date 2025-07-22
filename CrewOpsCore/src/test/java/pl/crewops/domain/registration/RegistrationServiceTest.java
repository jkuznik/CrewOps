package pl.crewops.domain.registration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.UUID;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.crewops.IntegrationTest;
import pl.crewops.auth.RoleDTO;
import pl.crewops.dto.CreateCustomerCommand;
import pl.crewops.dto.address.CreateAddressDTO;
import pl.crewops.dto.company.CreateCompanyDTO;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.tenant.CreateTenantDTO;
import pl.crewops.dto.tenant.TenantDTO;
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
        var companyName = "companyName";
        var employeeFirstName = "employeeFirstName";
        var cityName = "cityName";
        var createTenantDTO = CreateTenantDTO.builder()
                .createAddressDTO(CreateAddressDTO.builder()
                        .postalCode("postalCode")
                        .city(cityName)
                        .street("street")
                        .localNumber("localNumber")
                        .build())
                .createCompanyDTO(CreateCompanyDTO.builder()
                        .name(companyName)
                        .email("test@email.com")
                        .build())
                .build();
        var createEmployeeDTO = CreateEmployeeDTO.builder()
                .firstName(employeeFirstName)
                .lastName("lastName")
                .department("department")
                .birthDate(LocalDate.now())
                .companyId(UUID.randomUUID())
                .username("username")
                .password("password")
                .phoneNumber("phoneNumber")
                .roles(new HashSet<RoleDTO>())
                .build();
        var createCustomerCommand = CreateCustomerCommand.builder()
                .createTenantDTO(createTenantDTO)
                .createEmployeeDTO(createEmployeeDTO)
                .build();

        Tenant tenant = new Tenant();
        try {
            // when
            TenantDTO result = registrationService.registerCustomer(createCustomerCommand);
            tenant = tenantAPI.getByCompanyId(result.companyId());

            boolean schemaExists = schemaExists(tenant.getSchemaName());

            // then
            assertThat(result).isNotNull();
            assertThat(result.active()).isTrue();
            assertThat(schemaExists).isTrue();
            assertThat(tenant.getCompanyId()).isInstanceOf(UUID.class);
        } finally {
            //            cleanup(tenant.getSchemaName(), employeeFirstName, tenant.getId());
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
