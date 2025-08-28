package pl.crewops;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import pl.crewops.domain.auth.AuthAPI;
import pl.crewops.domain.department.DepartmentAPI;
import pl.crewops.domain.employee.EmployeeAPI;
import pl.crewops.domain.machine.MachineAPI;
import pl.crewops.domain.machineType.MachineTypeAPI;
import pl.crewops.domain.message.MessageAPI;
import pl.crewops.domain.qualification.QualificationAPI;
import pl.crewops.domain.tenant.TenantAPI;
import pl.crewops.infrastructure.multitenancy.TenantContext;
import pl.crewops.util.multitenancy.LiquibaseSchemaMigrator;
import pl.crewops.util.multitenancy.SchemaManager;
import pl.crewops.util.spring.SpringContextBridge;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.profiles.active=integration")
@Testcontainers
public abstract class IntegrationTest {

    public static final String TESTCONTAINER_DB_NAME = "testdb";
    public static final String TESTCONTAINER_DB_USERNAME = "testUsername";
    public static final String TESTCONTAINER_DB_PASSWORD = "testPassword";
    public static final String TEST_SCHEMA_NAME = "testtenant_2f3b1d5c9e8f";
    public static final UUID TEST_TENANT_COMPANY_ID = UUID.fromString("2f3b1d5c-9e8f-4bca-9c56-123456789abd");
    // tenant with this relation is available in db by test values insertions

    public static final PostgreSQLContainer<?> postgresSQLContainer = new PostgreSQLContainer<>(
                    DockerImageName.parse("postgis/postgis:16-3.4").asCompatibleSubstituteFor("postgres"))
            .withDatabaseName(TESTCONTAINER_DB_NAME)
            .withUsername(TESTCONTAINER_DB_USERNAME)
            .withPassword(TESTCONTAINER_DB_PASSWORD)
            .withReuse(true);

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    protected AuthAPI authAPI;

    @Autowired
    protected EmployeeAPI employeeAPI;

    @Autowired
    protected QualificationAPI qualificationAPI;

    @Autowired
    protected TenantAPI tenantAPI;

    @Autowired
    protected MachineAPI machineAPI;

    @Autowired
    protected MessageAPI messageAPI;

    @Autowired
    protected MachineTypeAPI machineTypeAPI;

    @Autowired
    protected DepartmentAPI departmentAPI;

    @Autowired
    private LiquibaseSchemaMigrator schemaMigrator;

    @Autowired
    private SchemaManager schemaInitializer;

    @LocalServerPort
    protected int port;

    @BeforeAll
    public static void startContainer() {
        postgresSQLContainer.start();
    }

    @BeforeEach
    public void setupTenantSchema() {
        TenantContext.setCurrentTenant(TEST_SCHEMA_NAME);
        schemaInitializer.createSchemaIfNotExists(TEST_SCHEMA_NAME);
        schemaMigrator.runMigrations(TEST_SCHEMA_NAME);

        SpringContextBridge.setApplicationContext(applicationContext);
    }

    @BeforeTransaction
    public void setUpTenant() {
        TenantContext.setCurrentTenant(TEST_SCHEMA_NAME);
    }

    @DynamicPropertySource
    public static void setTestContainerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgresSQLContainer::getPassword);

        // liquibase parameters for address
        registry.add("spring.liquibase.parameters.address.id", () -> "8faf1d5c-9e8f-4bca-9c56-123456789aab");
        registry.add("spring.liquibase.parameters.address.postal.code", () -> "postalCode");
        registry.add("spring.liquibase.parameters.address.city", () -> "city");
        registry.add("spring.liquibase.parameters.address.street", () -> "street");
        registry.add("spring.liquibase.parameters.address.local.number", () -> "localNumber");

        // liquibase parameters for company
        registry.add("spring.liquibase.parameters.company.id", () -> "2b3b7d5c-9e8f-4bca-9c56-123456789abd");
        registry.add("spring.liquibase.parameters.company.name", () -> "CrewOps");
        registry.add("spring.liquibase.parameters.company.address.id", () -> "8faf1d5c-9e8f-4bca-9c56-123456789aab");
        registry.add("spring.liquibase.parameters.company.email", () -> "disaster.alert2@gmail.com");
        registry.add("spring.liquibase.parameters.company.status", () -> "ACTIVE");

        // liquibase parameters for employee
        registry.add("spring.liquibase.parameters.employee.id", () -> "1cd62a5c-9e2f-4bca-9c56-123456789abc");
        registry.add("spring.liquibase.parameters.employee.first.name", () -> "Admin");
        registry.add("spring.liquibase.parameters.employee.last.name", () -> "User");
        registry.add("spring.liquibase.parameters.employee.birth.date", () -> "2000-01-01");
        registry.add("spring.liquibase.parameters.employee.phone.number", () -> "123456789");

        // liquibase parameters for department
        registry.add("spring.liquibase.parameters.department.id", () -> "d0000000-0000-0000-0000-000000000001");
        registry.add("spring.liquibase.parameters.department.name", () -> "department");

        // liquibase parameters for tenant
        registry.add("spring.liquibase.parameters.tenant.id", () -> "2b3b7d5c-9e8f-4bca-9c56-123456789abc");
        registry.add("spring.liquibase.parameters.tenant.company.id", () -> "2b3b7d5c-9e8f-4bca-9c56-123456789abd");
        registry.add("spring.liquibase.parameters.tenant.schema.name", () -> "crewops_2b3b7d5c9e8f");
        registry.add("spring.liquibase.parameters.tenant.active", () -> "true");

        // liquibase parameters for auth.user
        registry.add("spring.liquibase.parameters.auth.user.id", () -> "2bc51b6d-1f3a-4bca-9c56-123456789abc");
        registry.add("spring.liquibase.parameters.auth.user.username", () -> "admin");
        registry.add(
                "spring.liquibase.parameters.auth.user.password",
                () -> "$2a$10$njg7g8ZWIxIpGEap4x7XQ.RGShM6ti/kLZ6402ZvnmqyeTtFlcseK");
        registry.add("spring.liquibase.parameters.auth.user.tenant.id", () -> "2b3b7d5c-9e8f-4bca-9c56-123456789abc");
        registry.add("spring.liquibase.parameters.auth.user.employee.id", () -> "1cd62a5c-9e2f-4bca-9c56-123456789abc");

        // liquibase parameters for auth.user.role
        registry.add(
                "spring.liquibase.parameters.auth.user.role.auth.user.id",
                () -> "2bc51b6d-1f3a-4bca-9c56-123456789abc");
        registry.add(
                "spring.liquibase.parameters.auth.user.role.role.id", () -> "e90a0e1e-8ab9-41ec-b98e-23a7bba4ef58");

        // security properties (JWT etc)
        registry.add(
                "security.properties.clientId", () -> "$2b$12$tVshluSeF.68u6mqQ/LXkOHbkpbbW/L9mPuxwhgFanIQsmNm0DveG");
        registry.add("security.properties.clientIdInput", () -> "HGQ3LviT5whGd0YB");
        registry.add(
                "security.properties.jwtSecret",
                () -> "c4b7a89f3e2d1f6b8a9d0c7e1b2f4d5a7c6e8b1a3f2d9c5e6a8f1b0c3d7e2a4");
        registry.add("security.properties.jwtExpiration", () -> "300");
    }

    protected String getBaseUrl() {
        return "http://localhost:" + port;
    }
}
